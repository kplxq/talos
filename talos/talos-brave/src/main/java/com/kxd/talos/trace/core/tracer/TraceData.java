package com.kxd.talos.trace.core.tracer;

import com.kxd.talos.trace.core.span.SpanId;
import com.kxd.talos.trace.core.utils.Nullable;

/**
 * Trace properties we potentially get from incoming request.
 */
public  class TraceData {
	
	private SpanId spanId;
	
	private Boolean sample;

    public static Builder builder(){
        return new TraceData.Builder();
    }

    /**
     * Span id.
     *
     * @return Nullable Span id.
     */
    @Nullable
    public  SpanId getSpanId(){
    	return this.spanId;
    }
    
    public void spanId(SpanId spanId){
    	this.spanId = spanId;
    }
    
    /**
     * Indication of request should be sampled or not.
     *
     * @return Nullable Indication if request should be sampled or not.
     */
    @Nullable
    public  Boolean getSample(){
    	return this.sample;
    }
    
    public void sample(Boolean sample){
    	this.sample = sample;
    }

    public static class Builder {
    	private TraceData traceData = new TraceData();

        public Builder spanId(@Nullable SpanId spanId){
        	traceData.spanId(spanId);
        	return this;
        }

        public Builder sample(@Nullable Boolean sample){
        	traceData.sample(sample);
        	return this;
        }

        public TraceData build(){
        	return this.traceData;
        }
    }
}
