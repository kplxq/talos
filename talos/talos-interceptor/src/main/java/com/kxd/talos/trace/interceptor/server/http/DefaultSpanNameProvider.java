package com.kxd.talos.trace.interceptor.server.http;


public class DefaultSpanNameProvider implements SpanNameProvider {

    @Override
    public String spanName(HttpRequest request) {
        return request.getHttpMethod()+"."+request.getUri().getPath();
    }
}
