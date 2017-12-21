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
package com.kxd.talos.trace.core.span;

import com.kxd.talos.trace.core.utils.Nullable;
import com.kxd.talos.trace.core.utils.JdkRuntimeUtilities;

/**
 * The ServerSpan is initialized by {@link ServerTracer} and keeps track of Trace/Span state of our service request.
 *
 * @author adriaens
 */
public class ServerSpan {

    public final static ServerSpan EMPTY = new ServerSpan(null,null);
    public static final ServerSpan NOT_SAMPLED = new ServerSpan(null,false);
    
    private Span span = null;
    private Boolean sample;

    public static ServerSpan create(Span span, Boolean sample) {
        return new ServerSpan(span, sample);
    }

    /**
     * Creates a new initializes instance. Using this constructor also indicates we need to sample this request.
     *
     * @param traceId Trace id.
     * @param spanId Span id.
     * @param parentSpanId Parent span id, can be <code>null</code>.
     * @param name Span name. Should be lowercase and not <code>null</code> or empty.
     */
     public static ServerSpan create(String traceId, String spanId, @Nullable String parentSpanId, String name) {
        Span span = new Span();
        span.setTrace_id(traceId);
        span.setId(spanId);
        if (parentSpanId != null) {
            span.setParent_id(parentSpanId);
        }
        span.setName(name);
        span.setThreadName(JdkRuntimeUtilities.getCurrentThreadName());
        span.setProcessId(JdkRuntimeUtilities.getProcessId());
        return create(span, true);
    }

    /**
     * Creates a new empty instance with no Span but with sample indication.
     *
     * @param sample Indicates if we should sample this span.
     */
    public ServerSpan create(final Boolean sample) {
        return create(null, sample);
    }

    private ServerSpan(Span span, Boolean sample){
    	this.sample = sample;
    	this.span   = span;
    }

	public Span getSpan() {
		return span;
	}

	public void setSpan(Span span) {
		this.span = span;
	}

	public Boolean getSample() {
		return sample;
	}

	public void setSample(Boolean sample) {
		this.sample = sample;
	}
}
