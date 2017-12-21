package com.kxd.talos.trace.core.tracer;

import java.util.Random;

import com.kxd.talos.trace.core.collector.SpanCollector;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.interceptor.ServerResponseStatus;
import com.kxd.talos.trace.core.sampler.Sampler;
import com.kxd.talos.trace.core.span.ServerSpan;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.state.ServerClientAndLocalSpanState;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.core.utils.Nullable;
import com.kxd.talos.trace.core.endpoint.SpanAndEndpoint;
import com.kxd.talos.trace.core.utils.Util;

/**
 * Used for setting up trace information for a request. When a request is
 * received we typically do this:
 * <ol>
 * <li>Detect if we are part of existing trace/span. For example with services
 * doing http requests this can be done by detecting and getting values of http
 * header that reresent trace/span ids.</li>
 * <li>Once detected we submit state using one of 3 following methods depending
 * on the state we are in:
 * {@link ServerTracer#setStateCurrentTrace(long, long, Long, String),
 * {@link ServerTracer#setStateNoTracing()} or
 * {@link ServerTracer#setStateUnknown(String)}.</li>
 * <li>Next we execute {@link ServerTracer#setServerReceived()} to mark the
 * point in time at which we received the request.</li>
 * <li>Service request executes its logic...
 * <li>Just before sending response we execute
 * {@link ServerTracer#setServerSend()}.
 * </ol>
 * 
 * @author kristof
 */
public class ServerTracer extends AnnotationSubmitter {

	private SpanAndEndpoint.ServerSpanAndEndpoint spanAndEndpoint;
	private Random randomGenerator;
	private SpanCollector spanCollector;
	private Sampler traceSampler;

	public static Builder builder() {
		return new ServerTracer.Builder();
	}

	@Override
	SpanAndEndpoint.ServerSpanAndEndpoint spanAndEndpoint() {
		return spanAndEndpoint;
	}

	void spanAndEndpoint(SpanAndEndpoint.ServerSpanAndEndpoint serverSpanAndEndpoint) {
		this.spanAndEndpoint = serverSpanAndEndpoint;
	}

	Random randomGenerator() {
		return randomGenerator;
	}

	void randomGenerator(Random random) {
		this.randomGenerator = random;
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

	void traceSampler(Sampler sampler) {
		this.traceSampler = sampler;
	}

	public static class Builder {
		private ServerTracer serverTracer = new ServerTracer();

		public Builder state(ServerClientAndLocalSpanState state) {
			return spanAndEndpoint(SpanAndEndpoint.ServerSpanAndEndpoint.create(state));
		}

		Builder spanAndEndpoint(SpanAndEndpoint.ServerSpanAndEndpoint spanAndEndpoint){
        	 serverTracer.spanAndEndpoint(spanAndEndpoint);
        	return this;
        }

		/**
		 * Used to generate new trace/span ids.
		 */
		public Builder randomGenerator(Random randomGenerator) {
			serverTracer.randomGenerator(randomGenerator);
			return this;
		}

		public Builder spanCollector(SpanCollector spanCollector) {
			serverTracer.spanCollector(spanCollector);
			return this;
		}

		public Builder traceSampler(Sampler sampler) {
			serverTracer.traceSampler(sampler);
			return this;
		}

		public ServerTracer build() {
			return serverTracer;
		}
	}

	/**
	 * Clears current span.
	 */
	public void clearCurrentSpan() {
		spanAndEndpoint().state().setCurrentLocalSpan(null);
	}

	/**
	 * Sets the current Trace/Span state. Using this method indicates we are
	 * part of an existing trace/span.
	 * 
	 * @param traceId
	 *            Trace id.
	 * @param spanId
	 *            Span id.
	 * @param parentSpanId
	 *            Parent span id. Can be <code>null</code>.
	 * @param name
	 *            Name should not be empty or <code>null</code>.
	 * @see ServerTracer#setStateNoTracing()
	 * @see ServerTracer#setStateUnknown(String)
	 */
	public void setStateCurrentTrace(String traceId, String spanId,
									 @Nullable String parentSpanId, @Nullable String name, String serviceType) {
		Util.checkNotBlank(name, "Null or blank span name");
		Span newSpan = getNewSpan(traceId, spanId, parentSpanId, name);
		newSpan.setType(serviceType);
		newSpan.setHost(spanAndEndpoint().endpoint().ipv4);
		newSpan.setProcessId(spanAndEndpoint().endpoint().processId);
		newSpan.setAppName(spanAndEndpoint().endpoint().service_name);
		spanAndEndpoint().state().setCurrentLocalSpan(newSpan);
	}

	/**
	 * Sets the current Trace/Span state. Using this method indicates that a
	 * parent request has decided that we should not trace the current request.
	 * 
	 * @see ServerTracer#setStateCurrentTrace(long, long, Long, String)
	 * @see ServerTracer#setStateUnknown(String)
	 */
	public void setStateNoTracing() {
		spanAndEndpoint().state().setCurrentLocalSpan(null);
	}

	/**
	 * Sets the current Trace/Span state. Using this method indicates that we
	 * got no information about being part of an existing trace or about the
	 * fact that we should not trace the current request. In this case the
	 * ServerTracer will decide what to do.
	 * 
	 * @param spanName
	 *            The name of our current request/span.
	 */
	public void setStateUnknown(String spanName,String serviceType) {
		Util.checkNotBlank(spanName, "Null or blank span name");
		String newTraceId = String.valueOf(randomGenerator().nextLong());
		if (!traceSampler().isSampled(newTraceId)) {
			spanAndEndpoint().state().setCurrentServerSpan(
					ServerSpan.NOT_SAMPLED);
			return;
		}
		Span newSpan = getNewSpan(newTraceId, Constants.TRACE_FIRST_CHILD_ID,Constants.TRACE_ROOT_SPAN_ID,spanName);
		newSpan.setType(serviceType);
		newSpan.setHost(spanAndEndpoint().endpoint().ipv4);
		newSpan.setProcessId(spanAndEndpoint().endpoint().processId);
		newSpan.setAppName(spanAndEndpoint().endpoint().service_name);
		spanAndEndpoint().state().setCurrentLocalSpan(newSpan);
	}
	
	private Span getNewSpan(String traceId,String spanId,String parentSpanId,String spanName){
		Span span = ServerSpan.create(traceId, spanId, parentSpanId, spanName).getSpan();
        span.setTimestamp(System.currentTimeMillis());
		span.setAppName(spanAndEndpoint().endpoint().service_name);
		return span;
	}

	/**
	 * Sets server received event for current request. This should be done after
	 * setting state using one of 3 methods
	 * {@link ServerTracer#setStateCurrentTrace(long, long, Long, String)} ,
	 * {@link ServerTracer#setStateNoTracing()} or
	 * {@link ServerTracer#setStateUnknown(String)}.
	 */
	public void setServerReceived() {
		submitStartAnnotation(Constants.SERVER_RECV);
	}

	/**
	 * Like {@link #setServerReceived()}, except you can log the network context
	 * of the caller, for example an IP address from the {@code X-Forwarded-For}
	 * header.
	 * 
	 * @param ipv4
	 *            ipv4 of the client as an int. Ex for 1.2.3.4, it would be (1
	 *            << 24) | (2 << 16) | (3 << 8) | 4
	 * @param port
	 *            port for client-side of the socket, or 0 if unknown
	 * @param clientService
	 *            lowercase {@link Endpoint#service_name name} of the callee
	 *            service or null if unknown
	 */
	public void setServerReceived(int ipv4, int port,
			@Nullable String clientService) {
		submitAddress(Constants.CLIENT_ADDR, ipv4, port, clientService);
		submitStartAnnotation(Constants.SERVER_RECV);
	}

	/**
	 * Sets the server sent event for current thread.
	 */
	public void setServerSend(ServerResponseStatus status) {
		Span span = spanAndEndpoint().span();
		if (span != null && !status.isSuccess()) {
			span.setExType(status.getExceptionType());
			span.setErrorCode(status.getExceptionCode());
        }
		
		if (submitEndAnnotation(Constants.SERVER_SEND, spanCollector())) {
			spanAndEndpoint().state().setCurrentLocalSpan(null);
		}
	}

	ServerTracer() {
	}
}
