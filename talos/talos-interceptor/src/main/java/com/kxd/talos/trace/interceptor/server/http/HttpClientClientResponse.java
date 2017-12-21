package com.kxd.talos.trace.interceptor.server.http;

import org.apache.http.HttpResponse;

public class HttpClientClientResponse implements HttpClientResponse {
	private final HttpResponse response;

    public HttpClientClientResponse(HttpResponse response) {
        this.response = response;
    }

    @Override
    public int getHttpStatusCode() {
        return response.getStatusLine().getStatusCode();
    }
}
