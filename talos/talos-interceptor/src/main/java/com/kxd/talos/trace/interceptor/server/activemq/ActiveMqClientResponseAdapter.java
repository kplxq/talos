package com.kxd.talos.trace.interceptor.server.activemq;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ClientResponseAdapter;
import com.kxd.talos.trace.core.interceptor.ClientResponseStatus;
import org.springframework.jms.JmsException;

import java.util.Collection;
import java.util.Collections;

/**
 * @author lhldyf
 */
public class ActiveMqClientResponseAdapter implements ClientResponseAdapter {

	public ActiveMqClientResponseAdapter(JmsException jmsException) {
		this.exception = jmsException;
	}

	private JmsException exception;

	@Override
	public Collection<KeyValueAnnotation> responseAnnotations() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public ClientResponseStatus responseStatus() {
		ClientResponseStatus status = new ClientResponseStatus();
		if(exception != null) {
			status.fail("JmsException", exception.getErrorCode());
		}
		return status;
	}
}
