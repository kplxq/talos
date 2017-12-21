package com.kxd.talos.trace.core.interceptor;

import java.util.Collection;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;

public interface ClientResponseAdapter {

    /**
     * Returns a collection of annotations that should be added to span
     * based on response.
     *
     * Can be used to indicate errors when response was not successful.
     *
     * @return Collection of annotations.
     */
    Collection<KeyValueAnnotation> responseAnnotations();
    
    ClientResponseStatus responseStatus();
    
}
