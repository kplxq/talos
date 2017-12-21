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

import java.util.Random;

import com.kxd.talos.trace.core.collector.SpanCollector;
import com.kxd.talos.trace.core.sampler.Sampler;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.span.SpanId;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.core.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.kxd.talos.trace.core.endpoint.SpanAndEndpoint.LocalSpanAndEndpoint;

/**
 * Local tracer is designed for in-process activity that explains latency.
 *
 * <p/>For example, a local span could represent bootstrap, codec, file i/o or
 * other activity that notably impacts performance.
 *
 * <p/>Local spans always have a binary annotation "lc" which indicates the
 * component name. Usings zipkin's UI or Api, you can query by for spans that
 * use a component like this: {@code lc=spring-boot}.
 *
 * <p/>Here's an example of allocating precise duration for a local span:
 * <pre>
 * tracer.startNewSpan("codec", "encode");
 * try {
 *   return codec.encode(input);
 * } finally {
 *   tracer.finishSpan();
 * }
 * </pre>
 *
 * @see Constants#LOCAL_COMPONENT
 */
public  class LocalTracer extends AnnotationSubmitter {

    public static Builder builder() {
        return new LocalTracer.Builder();
    }
    
    private LocalSpanAndEndpoint spanAndEndpoint;
    
    private Random randomGenerator;
    
    private SpanCollector spanCollector;
    
    private Sampler traceSampler;
    
    private String  localServiceType = Constants.SERVICE_TYPE_LOCAL;

    @Override
    public LocalSpanAndEndpoint spanAndEndpoint(){
    	return spanAndEndpoint;
    }
    
    public void spanAndEndpoint(LocalSpanAndEndpoint spanAndEndpoint){
    	this.spanAndEndpoint = spanAndEndpoint;
    }

    public Random randomGenerator(){
    	return randomGenerator;
    }
    
    public void randomGenerator(Random randomGenerator){
    	this.randomGenerator = randomGenerator;
    }

    public SpanCollector spanCollector(){
    	return spanCollector;
    }

    public void spanCollector(SpanCollector spanCollector){
    	this.spanCollector = spanCollector;
    }
    
    public  Sampler traceSampler(){
    	return traceSampler;
    }
    
    public  void  traceSampler(Sampler traceSampler){
    	this.traceSampler = traceSampler;
    }

    public static class Builder {
    	 private LocalTracer localTracer = new LocalTracer();

         public Builder spanAndEndpoint(LocalSpanAndEndpoint spanAndEndpoint){
        	 localTracer.spanAndEndpoint(spanAndEndpoint);
        	 return this;
         }

         public Builder randomGenerator(Random randomGenerator){
        	 localTracer.randomGenerator(randomGenerator);
        	 return this;
         }

         public Builder spanCollector(SpanCollector spanCollector){
        	 localTracer.spanCollector(spanCollector);
        	 return this;
        }

         public Builder traceSampler(Sampler sampler){
        	 localTracer.traceSampler(sampler);
        	 return this;
        }

         public LocalTracer build(){
        	 return localTracer;
         }
    }

    /**
     * Request a new local span, which starts now.
     *
     * @param component {@link Constants#LOCAL_COMPONENT component} responsible for the operation
     * @param operation name of the operation that's begun
     * @return metadata about the new span or null if one wasn't started due to sampling policy.
     * @see Constants#LOCAL_COMPONENT
     */
    
    public Span startSpan(String component, String operation) {
        // 获取当前调用链
        String caller = Util.getCallerMethod();
        Span span = startNewSpan(component, operation, currentTimeMicroseconds(),caller);
        if (span == null) return Span.EMPTY_SPAN;
        span.startTick = System.currentTimeMillis(); // embezzle start tick into an internal field.
        MDC.put("invokeNo", span.getTrace_id());
        return span;
    }

    private SpanId getNewSpanId() {
        Span parentSpan = spanAndEndpoint().state().getCurrentLocalSpan();
        String newSpanId = String.valueOf(randomGenerator().nextLong());
        SpanId.Builder builder = SpanId.builder().spanId(newSpanId);
        if (parentSpan == null) {
        	return builder.spanId(Constants.TRACE_FIRST_CHILD_ID).traceId(newSpanId).build(); // new trace
        }
        else{
            return builder.traceId(parentSpan.getTrace_id()).parentId(parentSpan.getId()).build();
        }
    }

    /**
     * Request a new local span, which started at the given timestamp.
     *
     * @param component {@link Constants#LOCAL_COMPONENT component} responsible for the operation
     * @param operation name of the operation that's begun
     * @param timestamp time the operation started, in epoch microseconds.
     * @return metadata about the new span or null if one wasn't started due to sampling policy.
     * @see Constants#LOCAL_COMPONENT
     */
    public Span startNewSpan(String component, String operation, long timestamp,String caller) {

        Boolean sample  = spanAndEndpoint().state().sample();
        Span parentSpan = spanAndEndpoint().span();
        if (Boolean.FALSE.equals(sample)) {
            spanAndEndpoint().state().setCurrentLocalSpan(null);
            return null;
        }

        SpanId newSpanId = getNewSpanId();

		
        if (sample == null) {
            // No sample indication is present.
            if (!traceSampler().isSampled(newSpanId.traceId)) {
                spanAndEndpoint().state().setCurrentLocalSpan(null);
                return null;
            }
        }

        Span newSpan = newSpanId.toSpan();
        newSpan.start();
        newSpan.setName(operation);
        newSpan.setTimestamp(timestamp);
        newSpan.setType(localServiceType);
		if (parentSpan != null) {
			boolean isBrother = false;
            // 前一调用者与当前调用者为同一个,则表明为兄弟级调用链
			if(Util.equal(caller, parentSpan.getCaller())){
				isBrother = true;
			}
			
            // 前一调用者与当前调用者非同一个,则表明为父子级别调用链
            initSpan(newSpan, parentSpan, isBrother, caller);
		}
		newSpan.setHost(spanAndEndpoint().endpoint().ipv4);
        newSpan.setParentSpan(parentSpan);
        newSpan.setProcessId(spanAndEndpoint().endpoint().processId);
        newSpan.setAppName(spanAndEndpoint().endpoint().service_name);
        spanAndEndpoint().state().setCurrentLocalSpan(newSpan);
        return newSpan;
    }
    
    
    private void initSpan(Span origSpan,Span preSpan,boolean isBrotherCall,String caller){
		if(isBrotherCall){
            // 同caller的情况下,parentSpanId与caller是一致的
			origSpan.setParent_id(preSpan.getParent_id());
			origSpan.setCaller(preSpan.getCaller());
			String brotherSpanId = preSpan.getId();
			String brotherIndex = brotherSpanId.substring(brotherSpanId.lastIndexOf(Constants.TRACE_SPAN_ID_SPLIT)+1);
			int myIndex = Integer.parseInt(brotherIndex) + 1;
			origSpan.setId(preSpan.getParent_id()+Constants.TRACE_SPAN_ID_SPLIT+myIndex);
		}
		
		else{
            // 父子调用关系,则parentSpanId为原调用关系的spanId
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
            // 设置最新的子节点span id为最信值
			preSpan.setLatestChildSpanId(newSpanId);
		}
    }
    
    
    public void finishSpan(Span span){
        long endTick = System.currentTimeMillis();

        if (span == Span.EMPTY_SPAN) return;

        Long startTick = span.startTick;
        final long duration;
        if (startTick != null) {
            duration = Math.max(1L, (endTick - startTick));
        } else {
            duration = currentTimeMicroseconds() - span.getTimestamp();
        }
        finishSpan(duration,span);
    }
    
    public void finishSpan(long duration,Span span) {
        if (span == Span.EMPTY_SPAN) return;

        synchronized (span) {
            try {
                span.setDuration(duration);
                spanCollector().collect(span);
            } catch (Exception e) {
                e.printStackTrace();
                span.finish();
            }

        }
        spanAndEndpoint().state().setCurrentLocalSpan(span.getParentSpan());
    }

    LocalTracer() {
    }
}
