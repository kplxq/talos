package com.kxd.talos.trace.interceptor.server.activemq;

import org.apache.activemq.command.ActiveMQMessage;

import javax.jms.Message;

/**
 * @author lhldyf
 */
public  class TalosActiveMqMessage extends ActiveMQMessage implements Message {

	private Message wrappedMessage;

	private String sampled;

	private String spanId;

	private String parentSpanId;

	private String traceId;

	public TalosActiveMqMessage() {
		super();
	}

	/**
	 * ActiveMQMessageConsumer 会调用该方法取值
	 * @return
	 */
	@Override
	public org.apache.activemq.command.Message copy() {
		TalosActiveMqMessage copy = new TalosActiveMqMessage();
		super.copy(copy);
		copy.setTraceId(this.traceId);
		copy.setSpanId(this.spanId);
		copy.setParentSpanId(this.parentSpanId);
		copy.setSampled(this.sampled);
		copy.setWrappedMessage(this.wrappedMessage);
		return copy;
	}


	public TalosActiveMqMessage(Message message) {
		this.wrappedMessage = message;
	}

	public Message getWrappedMessage() {
		return wrappedMessage;
	}

	public void setWrappedMessage(Message wrappedMessage) {
		this.wrappedMessage = wrappedMessage;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getSampled() {
		return sampled;
	}

	public void setSampled(String sampled) {
		this.sampled = sampled;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public String getParentSpanId() {
		return parentSpanId;
	}

	public void setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
	}
}
