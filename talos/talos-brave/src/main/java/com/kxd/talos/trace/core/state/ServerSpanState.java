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

import com.kxd.talos.trace.core.span.ServerSpan;

/**
 * Maintains state for a single server span.
 *
 * <p/>Server spans can be at the following locations in the span tree.
 * <ul>
 *     <li>The root-span of a trace originated by Brave</li>
 *     <li>A child of a span propagated to Brave</li>
 * </ul>
 *
 * @author kristof
 */
public interface ServerSpanState extends CommonSpanState {

    /**
     * Gets the Span for the server request we are currently part of.
     * <p/>
     * Should be thread-aware to support multiple parallel requests.
     * 
     * @return Server request span for current thread. This will return the span we are part of. In case we should not trace
     *         current request <code>null</code> will be returned.
     */
    ServerSpan getCurrentServerSpan();

    /**
     * Set span for current request.
     * 
     * @param span Span for current request.
     */
    void setCurrentServerSpan(final ServerSpan span);
}
