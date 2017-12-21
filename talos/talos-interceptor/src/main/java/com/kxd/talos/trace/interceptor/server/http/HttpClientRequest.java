package com.kxd.talos.trace.interceptor.server.http;

public interface HttpClientRequest extends HttpRequest {
	/**
     * Set http header value.
     *
     * @param headerName
     * @return
     */
    void setHttpHeaderValue(String headerName, String headerValue);
}
