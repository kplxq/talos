package com.kxd.talos.trace.sample.activemq.listener;

import com.kxd.talos.core.trace.TalosTrace;
import com.kxd.talos.trace.interceptor.server.activemq.TalosActiveMqMessage;
import com.kxd.talos.trace.sample.activemq.service.ConsumerService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author lhldyf
 */
@TalosTrace
public class ConsumerMessageListener implements MessageListener {

	@Autowired
	private ConsumerService consumerService;

	@Override
	public void onMessage(Message message) {

		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			System.out.println("TextMessage");
			try {
				consumerService.receiveMessage(textMessage.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if(message instanceof TalosActiveMqMessage){
			TalosActiveMqMessage talosJmsMessage = (TalosActiveMqMessage) message;
			System.out.println("TalosJmsMessage, traceId:"+talosJmsMessage.getTraceId());
			try {
				consumerService.receiveMessage(((TextMessage)talosJmsMessage.getWrappedMessage()).getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if(message instanceof ActiveMQObjectMessage){
			ActiveMQObjectMessage talosJmsMessage = (ActiveMQObjectMessage) message;
			System.out.println("ActiveMQObjectMessage");
			try {
				consumerService.receiveMessage(talosJmsMessage.getObject().toString());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("message 类型不正确");
		}
	}
}
