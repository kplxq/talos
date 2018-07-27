package com.kxd.talos.trace.sample.activemq.service;

import javax.jms.JMSException;

public interface ConsumerService {
	public void receiveMessage(String message) throws JMSException;
}
