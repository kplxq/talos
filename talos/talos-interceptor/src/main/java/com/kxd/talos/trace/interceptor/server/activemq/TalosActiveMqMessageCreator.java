package com.kxd.talos.trace.interceptor.server.activemq;

import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Session;

public interface TalosActiveMqMessageCreator extends MessageCreator{
	@Override
	TalosActiveMqMessage createMessage(Session var1) throws JMSException;
}
