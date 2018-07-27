package com.kxd.talos.trace.sample.activemq.service.impl;

import com.kxd.talos.core.trace.TalosTrace;
import com.kxd.talos.trace.interceptor.server.activemq.TalosActiveMqJmsTemplate;
import com.kxd.talos.trace.sample.activemq.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@TalosTrace
@Service("producerService")
public class ProducerServiceImpl implements ProducerService {

	@Autowired
	private TalosActiveMqJmsTemplate talosJmsTemplate;


	@Override
	public void sendMessage(final String message) {
		System.out.println("---------------生产者发送消息-----------------");
		System.out.println("---------------生产者发了一个消息：" + message);
		talosJmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});

	}

}
