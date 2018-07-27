package com.kxd.talos.trace.interceptor.server.activemq;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ServerResponseAdapter;
import com.kxd.talos.trace.core.interceptor.ServerResponseStatus;

import java.util.Collection;
import java.util.Collections;

/**
 * @author lhldyf
 */
public class ActiveMqServerResponseAdapter implements ServerResponseAdapter {

	private Exception exception;

	public ActiveMqServerResponseAdapter(Exception exception) {
		this.exception = exception;
	}

	@Override
	public Collection<KeyValueAnnotation> responseAnnotations() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public ServerResponseStatus responseStatus() {
		ServerResponseStatus status = new ServerResponseStatus();
		if(null != exception) {
			status.fail("Exception", exception.getMessage());
		}
 		return status;
	}
}
