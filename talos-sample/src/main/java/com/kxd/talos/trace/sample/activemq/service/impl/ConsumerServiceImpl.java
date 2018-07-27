package com.kxd.talos.trace.sample.activemq.service.impl;

import com.kxd.talos.core.trace.TalosTrace;
import com.kxd.talos.trace.sample.activemq.service.ConsumerService;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

@TalosTrace
@Service("consumerService")
public class ConsumerServiceImpl implements ConsumerService {
	@Override
	public void receiveMessage(String message) throws JMSException {
		System.out.println("---------------消费者接收消息-----------------");
		System.out.println("---------------消费者收到一个文本消息：" + message);
	}
}
