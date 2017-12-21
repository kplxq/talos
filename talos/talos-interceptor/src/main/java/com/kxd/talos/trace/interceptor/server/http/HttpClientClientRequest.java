package com.kxd.talos.trace.interceptor.server.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;

/**
 * @author admin
 *
 */
public class HttpClientClientRequest implements HttpClientRequest {
	private final HttpRequest request;

    public HttpClientClientRequest(HttpRequest request) {
        this.request = request;
    }

    @Override
    public void setHttpHeaderValue(String header, String value) {
        request.addHeader(header, value);
    }

    @Override
    public URI getUri() {
        try {
            return new URI(request.getRequestLine().getUri());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getHttpMethod() {
        return request.getRequestLine().getMethod();
    }
}
