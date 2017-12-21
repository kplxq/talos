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
package com.kxd.talos.trace.core.state;

import com.kxd.talos.trace.core.endpoint.Endpoint;

/**
 * Keeps track of common trace/span state information.
 * <p>
 * Should be thread aware since we can have multiple parallel request which means multiple trace/spans.
 * </p>
 * 
 * @author kristof
 */
public interface CommonSpanState {

    /**
     * Indicates if we should sample current request.
     * <p/>
     * Should be thread-aware to support multiple parallel requests.
     * 
     * @return <code>null</code> in case there is no indication if we should sample or not. <code>true</code> in case we got
     *         the indication we should sample current request, <code>false</code> in case we should not sample the current
     *         request.
     */
    Boolean sample();

    /**
     * Gets the Endpoint (ip, port, service name) for this service.
     *
     * @return Endpoint for this service.
     */
    Endpoint endpoint();
    
}
