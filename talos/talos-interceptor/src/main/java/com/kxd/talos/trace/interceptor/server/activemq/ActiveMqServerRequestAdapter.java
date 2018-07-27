package com.kxd.talos.trace.interceptor.server.activemq;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ServerRequestAdapter;
import com.kxd.talos.trace.core.span.SpanId;
import com.kxd.talos.trace.core.tracer.TraceData;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.interceptor.server.http.TalosHttpHeaders;

import java.util.Collection;
import java.util.Collections;

/**
 * @author lhldyf
 */
public class ActiveMqServerRequestAdapter implements ServerRequestAdapter {

	private TalosActiveMqMessage talosActiveMqMessage;

	private String methodName;

	public ActiveMqServerRequestAdapter(TalosActiveMqMessage message, String methodName) {
		this.talosActiveMqMessage = message;
		this.methodName = methodName;
	}

	@Override
	public TraceData getTraceData() {
		final String sampled = talosActiveMqMessage.getSampled();
		if (sampled != null) {
			if ("0".equals(sampled) || "false".equals(sampled.toLowerCase())) {
				return TraceData.builder().sample(false).build();
			} else {
				final String parentSpanId = talosActiveMqMessage.getParentSpanId();
				final String traceId = talosActiveMqMessage.getTraceId();
				final String spanId = talosActiveMqMessage.getSpanId();

				if (traceId != null && spanId != null) {
					SpanId span = SpanId.builder().spanId(spanId).traceId(traceId).parentId(parentSpanId).build();
					return TraceData.builder().sample(true).spanId(span).build();
				}
			}
		}
		return TraceData.builder().build();
	}

	@Override
	public String getSpanName() {
		return this.methodName;
	}

	@Override
	public Collection<KeyValueAnnotation> requestAnnotations() {
		KeyValueAnnotation uriAnnotation = KeyValueAnnotation.create(
				Constants.ACTIVE_MQ_DESTINATION, talosActiveMqMessage.getDestination().getQualifiedName());
		return Collections.singleton(uriAnnotation);
	}

	@Override
	public String serviceType() {
		return Constants.SERVICE_TYPE_ACTIVE_MQ_SERVER;
	}
}
