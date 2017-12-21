/**
 *  Copyright 2013 <kristofa@github.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kxd.talos.trace.core.interceptor;

import java.util.Collection;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Nullable;

/**
 * Adapter used to get tracing information from and add tracing information to a new request.
 *
 */
public interface ClientRequestAdapter {

    /**
     * Gets the span name for request.
     *
     * @return Span name for request.
     */
    String getSpanName();

    /**
     * Enrich the request with the Spanid so we pass the state to the
     * service we are calling.
     *
     * @param spanId Nullable span id. If null we don't need to trace request and you
     *               should pass an indication along with the request that indicates we won't trace this request.
     */
    void addSpanIdToRequest(@Nullable Span span);

    /**
     * Returns a collection of annotations that should be added to span
     * for given request.
     *
     * Can be used to indicate more details about request next to span name.
     * For example for http requests an annotation containing the uri path could be added.
     *
     * @return Collection of annotations.
     */
    Collection<KeyValueAnnotation> requestAnnotations();

    /**
     * Provides the remote server address information for additional tracking.
     *
     * Can be useful when communicating with non-traced services by adding server address to span
     * i.e. {@link zipkin.Constants#SERVER_ADDR}
     *
     * @return request's target server endpoint information
     */
    @Nullable
	Endpoint serverAddress();
    
    String serviceType();
}
