package com.kxd.talos.trace.interceptor.server.http;

import java.util.Collection;
import java.util.Collections;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.interceptor.ClientRequestAdapter;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.core.utils.Nullable;

public class HttpClientRequestAdapter implements ClientRequestAdapter {
	private final HttpClientRequest clientRequest;
    private final SpanNameProvider spanNameProvider;

    public HttpClientRequestAdapter(HttpClientRequest clientRequest, SpanNameProvider spanNameProvider) {
        this.clientRequest = clientRequest;
        this.spanNameProvider = spanNameProvider;
    }
    
    @Override
    public String getSpanName() {
        return spanNameProvider.spanName(clientRequest);
    }
    
    @Override
    public void addSpanIdToRequest(@Nullable Span span){
    	if (span == null) {
    		clientRequest.setHttpHeaderValue(TalosHttpHeaders.Sampled.getName(), "0");
    	}else{
    		clientRequest.setHttpHeaderValue(TalosHttpHeaders.Sampled.getName(), "1");
            clientRequest.setHttpHeaderValue(TalosHttpHeaders.SpanId.getName(), span.getId());
            clientRequest.setHttpHeaderValue(TalosHttpHeaders.TraceId.getName(), span.getTrace_id());
            if(span.getParent_id() != null){
            	clientRequest.setHttpHeaderValue(TalosHttpHeaders.ParentSpanId.getName(), span.getParent_id());
            }
    	}
    }
    
    @Override
    public Collection<KeyValueAnnotation> requestAnnotations(){
    	KeyValueAnnotation uriAnnotation = KeyValueAnnotation.create(
                Constants.HTTP_PATH, clientRequest.getUri().toString());
        return Collections.singleton(uriAnnotation);
    }
    
    @Override
    @Nullable
    public Endpoint serverAddress(){
    	return null; 	
    }
    
    @Override
    public String serviceType(){
    	return Constants.SERVICE_TYPE_HTTP_CLIENT;
    }
}
