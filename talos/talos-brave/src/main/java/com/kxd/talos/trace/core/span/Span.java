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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kxd.talos.trace.core.annotation.Annotation;
import org.slf4j.MDC;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;

/**
 * A trace is a series of spans (often RPC calls) which form a latency tree.
 * 
 * The root span is where trace_id = id and parent_id = Nil. The root span is
 * usually the longest interval in the trace, starting with a SERVER_RECV
 * annotation and ending with a SERVER_SEND.
 */
public class Span implements Serializable {

	public static final Span EMPTY_SPAN = new Span();

	static final long serialVersionUID = 1L;

	/**
	 * Internal field, used for deriving duration with {@link System#nanoTime()}
	 * .
	 */
	public volatile Long startTick;

	private String trace_id; // required
	private String name; // required
	private String id; // required
	private String parent_id; // optional
	private List<Annotation> annotations = Collections.emptyList(); // required
	private List<KeyValueAnnotation> keyValue_annotations = Collections.emptyList(); // required
	private Boolean debug; // optional
	private Long timestamp; // optional
	private Long duration; // optional
	private Span parentSpan; // optional
	private String caller;// optional;
	private String appName;
	private String processId;
	private String threadName;
	private int host;
	private String type;
	/** 异常类型 */
	private String exType = "OK";
	/** 错误码 */
	private String errorCode = "success";
	/**错误信息*/
	private String errorMessage = "";

	/** 超过调用时间阈值 */
	private String beyondThd = "N";

	private transient String latestChildSpanId;// optional

    /** 日志MDC调用流水号Key. 定义见TraceLog.java */
    public static final String       MDC_INVOKE_NO        = "invokeNo";

    private String                   invokeNo;


	/**
	 * Span name in lowercase, rpc method for example
	 * 
	 * Conventionally, when the span name isn't known, name = "unknown".
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Span name in lowercase, rpc method for example
	 * 
	 * Conventionally, when the span name isn't known, name = "unknown".
	 */
	public Span setName(String name) {
		this.name = name;
		return this;
	}

	public String getId() {
		return this.id;
	}

	public Span setId(String id) {
		this.id = id;
		return this;
	}

	public String getParent_id() {
		return this.parent_id;
	}

	public Span setParent_id(String parent_id) {
		this.parent_id = parent_id;
		return this;
	}

	public Span addToAnnotations(Annotation elem) {
		if (this.annotations == Collections.EMPTY_LIST) {
			this.annotations = new ArrayList<Annotation>();
		}
		this.annotations.add(elem);
		return this;
	}

	public List<Annotation> getAnnotations() {
		return this.annotations;
	}

	public Span setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
		return this;
	}

	public Span addToBinary_annotations(KeyValueAnnotation elem) {
		if (this.keyValue_annotations == Collections.EMPTY_LIST) {
			this.keyValue_annotations = new ArrayList<KeyValueAnnotation>();
		}
		this.keyValue_annotations.add(elem);
		return this;
	}

	public Boolean isDebug() {
		return this.debug;
	}

	public Span setDebug(Boolean debug) {
		this.debug = debug;
		return this;
	}

	/**
	 * Microseconds from epoch of the creation of this span.
	 * 
	 * This value should be set directly by instrumentation, using the most
	 * precise value possible. For example, gettimeofday or syncing nanoTime
	 * against a tick of currentTimeMillis.
	 * 
	 * For compatibilty with instrumentation that precede this field, collectors
	 * or span stores can derive this via Annotation.timestamp. For example,
	 * SERVER_RECV.timestamp or CLIENT_SEND.timestamp.
	 * 
	 * This field is optional for compatibility with old data: first-party span
	 * stores are expected to support this at time of introduction.
	 */
	public Long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Microseconds from epoch of the creation of this span.
	 * 
	 * This value should be set directly by instrumentation, using the most
	 * precise value possible. For example, gettimeofday or syncing nanoTime
	 * against a tick of currentTimeMillis.
	 * 
	 * For compatibilty with instrumentation that precede this field, collectors
	 * or span stores can derive this via Annotation.timestamp. For example,
	 * SERVER_RECV.timestamp or CLIENT_SEND.timestamp.
	 * 
	 * This field is optional for compatibility with old data: first-party span
	 * stores are expected to support this at time of introduction.
	 */
	public Span setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	/**
	 * Measurement of duration in microseconds, used to support queries.
	 * 
	 * This value should be set directly, where possible. Doing so encourages
	 * precise measurement decoupled from problems of clocks, such as skew or
	 * NTP updates causing time to move backwards.
	 * 
	 * For compatibilty with instrumentation that precede this field, collectors
	 * or span stores can derive this by subtracting Annotation.timestamp. For
	 * example, SERVER_SEND.timestamp - SERVER_RECV.timestamp.
	 * 
	 * If this field is persisted as unset, zipkin will continue to work, except
	 * duration query support will be implementation-specific. Similarly,
	 * setting this field non-atomically is implementation-specific.
	 * 
	 * This field is i64 vs i32 to support spans longer than 35 minutes.
	 */
	public Long getDuration() {
		return this.duration;
	}

	/**
	 * Measurement of duration in microseconds, used to support queries.
	 * 
	 * This value should be set directly, where possible. Doing so encourages
	 * precise measurement decoupled from problems of clocks, such as skew or
	 * NTP updates causing time to move backwards.
	 * 
	 * For compatibilty with instrumentation that precede this field, collectors
	 * or span stores can derive this by subtracting Annotation.timestamp. For
	 * example, SERVER_SEND.timestamp - SERVER_RECV.timestamp.
	 * 
	 * If this field is persisted as unset, zipkin will continue to work, except
	 * duration query support will be implementation-specific. Similarly,
	 * setting this field non-atomically is implementation-specific.
	 * 
	 * This field is i64 vs i32 to support spans longer than 35 minutes.
	 */
	public Span setDuration(Long duration) {
		this.duration = duration;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuffer message = new StringBuffer();
		message.append("TALOS").append("|TD:").append(getTrace_id())
				.append("|SI:").append(getId())
				.append("|PI:").append(getParent_id())
				.append("|SN:").append(getName())
				.append("|HT:").append(getHost())
				.append("|TP:").append(getType())
				.append("|ET:").append(getExType())
				.append("|EC:").append(getErrorCode())
				.append("|ST:").append((getTimestamp()!=null?getTimestamp():0))
				.append("|RT:").append(getDuration())
				.append("|TN:").append(getThreadName())
				.append("|AN:").append(getAppName())
				.append("|PD:").append(getProcessId())
				.append("|CT:");
		
		for(Annotation anno:this.annotations){
		   message.append(anno.value).append("=").append(anno.timestamp).append("&@&@&");
		}
		
		for(KeyValueAnnotation keyValueAnno:this.keyValue_annotations){
		  message.append(keyValueAnno.getKey()).append("=").append(keyValueAnno.getValue()).append("&@&@&");
		}
		message.append("|EM:").append(getErrorMessage());
		return message.toString();
	}

	public Span getParentSpan() {
		return parentSpan;
	}

	public void setParentSpan(Span parentSpan) {
		this.parentSpan = parentSpan;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getTrace_id() {
		return trace_id;
	}

	public void setTrace_id(String trace_id) {
		this.trace_id = trace_id;
	}

	public String getLatestChildSpanId() {
		return latestChildSpanId;
	}

	public void setLatestChildSpanId(String latestChildSpanId) {
		this.latestChildSpanId = latestChildSpanId;
	}

	public String getExType() {
		return exType;
	}

	public void setExType(String exType) {
		this.exType = exType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getBeyondThd() {
		return beyondThd;
	}

	public void setBeyondThd(String beyondThd) {
		this.beyondThd = beyondThd;
	}

	public Long getStartTick() {
		return startTick;
	}

	public void setStartTick(Long startTick) {
		this.startTick = startTick;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	public Boolean getDebug() {
		return debug;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<KeyValueAnnotation> getKeyValue_annotations() {
		return keyValue_annotations;
	}

	public void setKeyValue_annotations(
			List<KeyValueAnnotation> keyValue_annotations) {
		this.keyValue_annotations = keyValue_annotations;
	}

    /**
     * 节点开始，初始化invokeNo
     */
    public void start() {
        this.invokeNo = MDC.get(MDC_INVOKE_NO);

        if (null == this.invokeNo) {
            MDC.put(MDC_INVOKE_NO, this.getTrace_id());
        }
    }

    /**
     * 结束时移除
     */
    public void finish() {
        if (null == this.invokeNo) {
            MDC.remove(MDC_INVOKE_NO);
        }
    }

    /**
     * @return the invokeNo
     */
    public String getInvokeNo() {
        return invokeNo;
    }

    /**
     * @param invokeNo the invokeNo to set
     */
    public void setInvokeNo(String invokeNo) {
        this.invokeNo = invokeNo;
    }
}
