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
package com.kxd.talos.trace.core.collector;

import com.kxd.talos.trace.core.span.Span;

/**
 * Collects spans that are submitted by {@link ServerTracer} and {@link ClientTracer}. We can have implementations that
 * simply log the collected spans or implementations that persist the spans to a database, submit them to a service,...
 * 
 * @author kristof
 */
public interface SpanCollector {

    /**
     * Collect span.
     * 
     * @param span Span, should not be <code>null</code>.
     */
    void collect(final Span span);
}
