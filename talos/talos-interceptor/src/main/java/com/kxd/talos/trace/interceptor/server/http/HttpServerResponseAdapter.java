package com.kxd.talos.trace.interceptor.server.http;

import java.util.Arrays;
import java.util.Collection;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ServerResponseAdapter;
import com.kxd.talos.trace.core.interceptor.ServerResponseStatus;
import com.kxd.talos.trace.core.utils.Constants;

public class HttpServerResponseAdapter implements ServerResponseAdapter {

    private final HttpResponse response;

    public HttpServerResponseAdapter(HttpResponse response)
    {
        this.response = response;
    }

    @Override
    public Collection<KeyValueAnnotation> responseAnnotations() {
        KeyValueAnnotation statusAnnotation = KeyValueAnnotation.create(
                Constants.HTTP_STATUS_CODE, String.valueOf(response.getHttpStatusCode()));
        return Arrays.asList(statusAnnotation);
    }

	@Override
	public ServerResponseStatus responseStatus() {
		ServerResponseStatus status = new ServerResponseStatus();
		if(HttpStatus.OK.value()!=response.getHttpStatusCode()){
			status.fail("H", HttpStatus.valueOf(response.getHttpStatusCode()).getReasonPhrase());
		}
		return status;
	}
}
