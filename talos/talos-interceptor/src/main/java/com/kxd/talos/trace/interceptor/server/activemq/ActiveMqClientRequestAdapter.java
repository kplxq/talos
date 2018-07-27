package com.kxd.talos.trace.interceptor.server.activemq;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.interceptor.ClientRequestAdapter;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Constants;
import org.apache.activemq.ActiveMQSession;

import javax.jms.Session;
import java.util.Collection;
import java.util.Collections;

/**
 * @author lhldyf
 */
public class ActiveMqClientRequestAdapter implements ClientRequestAdapter {

	private Session session;

	private TalosActiveMqMessage talosActiveMqMessage;

	public ActiveMqClientRequestAdapter(TalosActiveMqMessage message, Session session) {
		this.talosActiveMqMessage = message;
		this.session = session;
	}

	@Override
	public String getSpanName() {
		try {
			return ((ActiveMQSession) this.session).getConnection().getTransport().getRemoteAddress();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void addSpanIdToRequest(Span span) {
		if(null == span || span == Span.EMPTY_SPAN){
			this.talosActiveMqMessage.setSampled("0");
		} else {
			this.talosActiveMqMessage.setTraceId(span.getTrace_id());
			this.talosActiveMqMessage.setParentSpanId(span.getParent_id());
			this.talosActiveMqMessage.setSampled("1");
			this.talosActiveMqMessage.setSpanId(span.getId());
		}
	}

	@Override
	public Collection<KeyValueAnnotation> requestAnnotations() {
		return Collections.emptyList();
	}

	@Override
	public Endpoint serverAddress() {
		// TODO
		return null;
	}

	@Override
	public String serviceType() {
		return Constants.SERVICE_TYPE_ACTIVE_MQ_CLIENT;
	}
}
