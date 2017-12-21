package com.kxd.talos.trace.core.interceptor;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.tracer.ClientTracer;
import com.kxd.talos.trace.core.utils.Util;

/**
 * Contains logic for handling an outgoing client request.
 * This means it will:
 *
 * - Start a new span
 * - Make sure span parameters are added to outgoing request
 * - Submit client sent annotation
 *
 *
 * The only thing you have to do is implement a ClientRequestAdapter.
 *
 * @see ClientRequestAdapter
 *
 */
public class ClientRequestInterceptor {

    private final ClientTracer clientTracer;

    public ClientRequestInterceptor(ClientTracer clientTracer) {
        this.clientTracer = Util.checkNotNull(clientTracer, "Null clientTracer");
    }

    /**
     * Handles outgoing request.
     *
     * @param adapter The adapter deals with implementation specific details.
     */
    public void handle(ClientRequestAdapter adapter) {

        Span span = clientTracer.startNewSpan(adapter.getSpanName(), Util.getCallerMethod(),adapter.serviceType());
        if (span == Span.EMPTY_SPAN) {
            // We will not trace this request.
            adapter.addSpanIdToRequest(null);
        } else {
            adapter.addSpanIdToRequest(span);
            for (KeyValueAnnotation annotation : adapter.requestAnnotations()) {
                clientTracer.submitKeyValueAnnotation(annotation.getKey(), annotation.getValue());
            }
            recordClientSentAnnotations(adapter.serverAddress());
        }
    }

    private void recordClientSentAnnotations(Endpoint serverAddress) {
        if (serverAddress == null) {
            clientTracer.setClientSent();
        } else {
            clientTracer.setClientSent(serverAddress.ipv4, serverAddress.port, serverAddress.service_name);
        }
    }
}