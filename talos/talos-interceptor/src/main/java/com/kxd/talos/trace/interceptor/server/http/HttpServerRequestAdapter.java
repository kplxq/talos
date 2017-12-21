package com.kxd.talos.trace.interceptor.server.http;

import java.util.Collection;
import java.util.Collections;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ServerRequestAdapter;
import com.kxd.talos.trace.core.span.SpanId;
import com.kxd.talos.trace.core.tracer.TraceData;
import com.kxd.talos.trace.core.utils.Constants;

public class HttpServerRequestAdapter implements ServerRequestAdapter {

    private final HttpServerRequest serverRequest;
    private final SpanNameProvider spanNameProvider;

    public HttpServerRequestAdapter(HttpServerRequest serverRequest, SpanNameProvider spanNameProvider) {
        this.serverRequest = serverRequest;
        this.spanNameProvider = spanNameProvider;
    }

    @Override
    public TraceData getTraceData() {
        final String sampled = serverRequest.getHttpHeaderValue(TalosHttpHeaders.Sampled.getName());
        if (sampled != null) {
            if ("0".equals(sampled) || "false".equals(sampled.toLowerCase())) {
                return TraceData.builder().sample(false).build();
            } else {
                final String parentSpanId = serverRequest.getHttpHeaderValue(TalosHttpHeaders.ParentSpanId.getName());
                final String traceId = serverRequest.getHttpHeaderValue(TalosHttpHeaders.TraceId.getName());
                final String spanId = serverRequest.getHttpHeaderValue(TalosHttpHeaders.SpanId.getName());

                if (traceId != null && spanId != null) {
                    SpanId span = getSpanId(traceId, spanId, parentSpanId);
                    return TraceData.builder().sample(true).spanId(span).build();
                }
            }
        }
        return TraceData.builder().build();
    }

    @Override
    public String getSpanName() {
        return spanNameProvider.spanName(serverRequest);
    }

    @Override
    public Collection<KeyValueAnnotation> requestAnnotations() {
        KeyValueAnnotation uriAnnotation = KeyValueAnnotation.create(
                Constants.HTTP_URL, serverRequest.getUri().toString());
        return Collections.singleton(uriAnnotation);
    }

    private SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
        return SpanId.builder()
            .traceId(traceId)
            .spanId(spanId)
            .parentId(parentSpanId == null ? null : parentSpanId).build();
   }

	@Override
	public String serviceType() {
		return Constants.SERVICE_TYPE_HTTP_SERVER;
	}
}
