/**
 *  Copyright 2013 <kristofa@github.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kxd.talos.trace.core.tracer;

import static com.kxd.talos.trace.core.utils.Util.*;

import java.util.Random;

import com.kxd.talos.trace.core.collector.SpanCollector;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.endpoint.SpanAndEndpoint;
import com.kxd.talos.trace.core.interceptor.ClientResponseInterceptor;
import com.kxd.talos.trace.core.interceptor.ClientResponseStatus;
import com.kxd.talos.trace.core.sampler.Sampler;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.span.SpanId;
import com.kxd.talos.trace.core.state.ServerClientAndLocalSpanState;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.core.utils.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.ClientRequestInterceptor;

/**
 * Low level api that deals with client side of a request:
 * 
 * <ol>
 * <li>Decide on tracing or not (sampling)</li>
 * <li>Sending client set / client received annotations</li>
 * </ol>
 * 
 * It is advised that you use ClientRequestInterceptor and
 * ClientResponseInterceptor which build upon ClientTracer and provide a higher
 * level api.
 * 
 * @see ClientRequestInterceptor
 * @see ClientResponseInterceptor
 * @author kristof
 */
public class ClientTracer extends AnnotationSubmitter {

	private SpanAndEndpoint.ClientSpanAndEndpoint spanAndEndpoint;

	private Random randomGenerator;

	private SpanCollector spanCollector;

	private Sampler traceSampler;
	
	public static Builder builder() {
		return new ClientTracer.Builder();
	}

	@Override
	SpanAndEndpoint.ClientSpanAndEndpoint spanAndEndpoint() {
		return spanAndEndpoint;
	}

	void spanAndEndpoint(SpanAndEndpoint.ClientSpanAndEndpoint spanAndEndpoint) {
		this.spanAndEndpoint = spanAndEndpoint;
	}

	Random randomGenerator() {
		return randomGenerator;
	}

	void randomGenerator(Random randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	SpanCollector spanCollector() {
		return spanCollector;
	}

	void spanCollector(SpanCollector spanCollector) {
		this.spanCollector = spanCollector;
	}

	Sampler traceSampler() {
		return traceSampler;
	}

	void traceSampler(Sampler traceSampler) {
		this.traceSampler = traceSampler;
	}

	public static class Builder {

		private ClientTracer clientTracer = new ClientTracer();

		public Builder state(ServerClientAndLocalSpanState state) {
			return spanAndEndpoint(SpanAndEndpoint.ClientSpanAndEndpoint.create(state));
		}

		Builder spanAndEndpoint(SpanAndEndpoint.ClientSpanAndEndpoint spanAndEndpoint) {
			clientTracer.spanAndEndpoint(spanAndEndpoint);
			return this;
		}

		/**
		 * Used to generate new trace/span ids.
		 */
		public Builder randomGenerator(Random randomGenerator) {
            clientTracer.randomGenerator(randomGenerator);
			return this;
		}

		public Builder spanCollector(SpanCollector spanCollector) {
			clientTracer.spanCollector(spanCollector);
			return this;
		}

		public Builder traceSampler(Sampler traceSampler) {
			clientTracer.traceSampler(traceSampler);
			return this;
		}

		public ClientTracer build() {
			return clientTracer;
		}
	}

	/**
	 * Sets 'client sent' event for current thread.
	 */
	public void setClientSent() {
		submitStartAnnotation(Constants.CLIENT_SEND);
	}

	/**
	 * Like {@link #setClientSent()}, except you can log the network context of
	 * the destination.
	 * 
	 * @param ipv4
	 *            ipv4 of the server as an int. Ex for 1.2.3.4, it would be (1
	 *            << 24) | (2 << 16) | (3 << 8) | 4
	 * @param port
	 *            listen port the client is connecting to, or 0 if unknown
	 * @param serviceName
	 *            lowercase {@link Endpoint#service_name name} of the service
	 *            being called or null if unknown
	 */
	public void setClientSent(int ipv4, int port, @Nullable String serviceName) {
		submitAddress(Constants.SERVER_ADDR, ipv4, port, serviceName);
		submitStartAnnotation(Constants.CLIENT_SEND);
	}

	/**
	 * Sets the 'client received' event for current thread. This will also
	 * submit span because setting a client received event means this span is
	 * finished.
	 */
	public void setClientReceived(ClientResponseStatus responseStatus) {
		Span currentSpan = spanAndEndpoint().span();
		if(currentSpan == Span.EMPTY_SPAN) return;
		else{
			currentSpan.setErrorCode(responseStatus.getExceptionCode());
			currentSpan.setExType(responseStatus.getExceptionType());
		}
		
		if (submitEndAnnotation(Constants.CLIENT_RECV, spanCollector())) {
			Span span = spanAndEndpoint().span();
	        if (span == Span.EMPTY_SPAN) return;
			spanAndEndpoint().state().setCurrentLocalSpan(span.getParentSpan());
		}
	}

	/**
	 * Start a new span for a new client request that will be bound to current
	 * thread. The ClientTracer can decide to return <code>null</code> in case
	 * this request should not be traced (eg sampling).
	 * 
	 * @param requestName
	 *            Request name. Should be lowercase and not <code>null</code> or
	 *            empty.
	 * @return Span id for new request or <code>null</code> in case we should
	 *         not trace this new client request.
	 */
	public Span startNewSpan(String requestName,String caller,String serviceType) {

		Boolean sample = spanAndEndpoint().state().sample();
		Span parentSpan = spanAndEndpoint().span();
		if (Boolean.FALSE.equals(sample)) {
			spanAndEndpoint().state().setCurrentLocalSpan(null);
			return Span.EMPTY_SPAN;
		}

		SpanId newSpanId = getNewSpanId();
		if (sample == null) {
			// No sample indication is present.
			if (!traceSampler().isSampled(newSpanId.traceId)) {
				spanAndEndpoint().state().setCurrentClientSpan(Span.EMPTY_SPAN);
				return Span.EMPTY_SPAN;
			}
		}

		Span newSpan = newSpanId.toSpan();
		newSpan.setName(requestName);
		if (parentSpan != null) {
			boolean isBrother = false;
			//前一调用者与当前调用者为同一个,则表明为兄弟级调用链
			if(equal(caller, parentSpan.getCaller())){
				isBrother = true;
			}
			
			//前一调用者与当前调用者非同一个,则表明为父子级别调用链
			initSpan(newSpan,parentSpan,isBrother,caller);
		}
		newSpan.setHost(spanAndEndpoint().endpoint().ipv4);
        newSpan.setProcessId(spanAndEndpoint().endpoint().processId);
        newSpan.setAppName(spanAndEndpoint().endpoint().service_name);
        newSpan.setParentSpan(parentSpan);
        newSpan.setType(serviceType);
        newSpan.setTimestamp(System.currentTimeMillis());
		spanAndEndpoint().state().setCurrentLocalSpan(newSpan);
		return newSpan;
	}

	private SpanId getNewSpanId() {
        Span parentSpan = spanAndEndpoint().state().getCurrentLocalSpan();
        String newSpanId = String.valueOf(randomGenerator().nextLong());
        SpanId.Builder builder = SpanId.builder().spanId(newSpanId);
        if (parentSpan == null) {
        	return builder.spanId("0.1").traceId(newSpanId).build(); // new trace
        }
        else{
            return builder.traceId(parentSpan.getTrace_id()).parentId(parentSpan.getId()).build();
        }
	}
	
	private void initSpan(Span origSpan,Span preSpan,boolean isBrotherCall,String caller){
		if(isBrotherCall){
			//同caller的情况下,parentSpanId与caller是一致的
			origSpan.setParent_id(preSpan.getParent_id());
			origSpan.setCaller(preSpan.getCaller());
			String brotherSpanId = preSpan.getId();
			String brotherIndex = brotherSpanId.substring(brotherSpanId.lastIndexOf(Constants.TRACE_SPAN_ID_SPLIT)+1);
			int myIndex = Integer.parseInt(brotherIndex) + 1;
			origSpan.setId(preSpan.getParent_id()+Constants.TRACE_SPAN_ID_SPLIT+myIndex);
		}
		
		else{
			//父子调用关系,则parentSpanId为原调用关系的spanId
			String newSpanId = null;
			origSpan.setParent_id(preSpan.getId());
			origSpan.setCaller(caller);
			String latestChildSpanId = preSpan.getLatestChildSpanId();
			if(StringUtils.isNotBlank(latestChildSpanId)){
				String brotherIndex = latestChildSpanId.substring(latestChildSpanId.lastIndexOf(Constants.TRACE_SPAN_ID_SPLIT)+1);
				int myIndex = Integer.parseInt(brotherIndex) + 1;
				newSpanId = preSpan.getId()+Constants.TRACE_SPAN_ID_SPLIT+myIndex;
			}
			
			else{
				newSpanId = preSpan.getId()+Constants.TRACE_SPAN_ID_SPLIT+Constants.TRACE_SPAN_ID_START_INDEX;
			}
			
			origSpan.setId(newSpanId);			
			//设置最新的子节点span id为最信值
			preSpan.setLatestChildSpanId(newSpanId);
		}
    }

	ClientTracer() {
	}
}
