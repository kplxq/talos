package com.kxd.talos.trace.interceptor.server.http;

import java.util.Collection;
import java.util.Collections;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ClientResponseAdapter;
import com.kxd.talos.trace.core.interceptor.ClientResponseStatus;
import com.kxd.talos.trace.core.utils.Constants;

public class HttpClientResponseAdapter implements ClientResponseAdapter {
	private final HttpResponse response;

    public HttpClientResponseAdapter(HttpResponse response)
    {
        this.response = response;
    }

    @Override
    public Collection<KeyValueAnnotation> responseAnnotations() {
    	KeyValueAnnotation statusAnnotation = KeyValueAnnotation.create(
                Constants.HTTP_STATUS_CODE, String.valueOf(response.getHttpStatusCode()));
        return Collections.singleton(statusAnnotation); 
    }

	@Override
	public ClientResponseStatus responseStatus() {
		ClientResponseStatus status = new ClientResponseStatus();
		if(HttpStatus.OK.value()!=response.getHttpStatusCode()){
			status.fail("H", HttpStatus.valueOf(response.getHttpStatusCode()).getReasonPhrase());
		}
		return status;
	}
}
