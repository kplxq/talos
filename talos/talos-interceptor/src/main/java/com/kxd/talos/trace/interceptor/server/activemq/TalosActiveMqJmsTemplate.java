package com.kxd.talos.trace.interceptor.server.activemq;

import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.interceptor.ClientRequestInterceptor;
import com.kxd.talos.trace.core.interceptor.ClientResponseInterceptor;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author lhldyf
 *
 */
public class TalosActiveMqJmsTemplate extends JmsTemplate {
	private JmsTemplate wrappedJmsTemplate;

	private Talos talos;

	private ClientRequestInterceptor clientRequestInterceptor;
	private ClientResponseInterceptor clientResponseInterceptor;

	public TalosActiveMqJmsTemplate() {
		super();
	}

	@Override
	public void send(final Destination destination, final MessageCreator messageCreator) throws JmsException {

		JmsException exception = null;
		try{
			TalosActiveMqMessageCreator talosJmsMessageCreator =  new TalosActiveMqMessageCreator() {
				@Override
				public TalosActiveMqMessage createMessage(Session session) throws JMSException {
					Message message = messageCreator.createMessage(session);
					TalosActiveMqMessage talosJmsMessage = new TalosActiveMqMessage();
					talosJmsMessage.setWrappedMessage(message);
					clientRequestInterceptor.handle(new ActiveMqClientRequestAdapter(talosJmsMessage, session));
					return talosJmsMessage;
				}
			};

			wrappedJmsTemplate.send(destination, talosJmsMessageCreator);

		} catch (JmsException jmsException) {
			exception = jmsException;
		} finally {
			clientResponseInterceptor.handle(new ActiveMqClientResponseAdapter(exception));
			if(null != exception) {
				throw exception;
			}
		}
	}

	public JmsTemplate getWrappedJmsTemplate() {
		return wrappedJmsTemplate;
	}

	public void setWrappedJmsTemplate(JmsTemplate wrappedJmsTemplate) {
		this.wrappedJmsTemplate = wrappedJmsTemplate;
	}

	public Talos getTalos() {
		return talos;
	}

	public void setTalos(Talos talos) {
		this.talos = talos;
		this.clientRequestInterceptor = this.talos.clientRequestInterceptor();
		this.clientResponseInterceptor = this.talos.clientResponseInterceptor();
	}
}
