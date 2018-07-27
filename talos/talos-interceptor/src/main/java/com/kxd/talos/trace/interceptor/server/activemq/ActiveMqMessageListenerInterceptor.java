package com.kxd.talos.trace.interceptor.server.activemq;


import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.interceptor.ServerRequestInterceptor;
import com.kxd.talos.trace.core.interceptor.ServerResponseInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author lhldyf
 */
public class ActiveMqMessageListenerInterceptor implements MethodInterceptor {

	private Talos talos;

	private ServerRequestInterceptor serverRequestInterceptor;
	private ServerResponseInterceptor serverResponseInterceptor;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] object = invocation.getArguments();
		if(null != object && 1 == object.length && object[0] instanceof TalosActiveMqMessage) {
			Method method = invocation.getMethod();
			Object target = invocation.getThis();
			Object result = null;
			Exception exception = null;
			TalosActiveMqMessage talosActiveMqMessage = (TalosActiveMqMessage) object[0];
			String methodName = target.getClass().getSimpleName() + "." + method.getName();

			object[0] = talosActiveMqMessage.getWrappedMessage();

			try {

				serverRequestInterceptor.handle(new ActiveMqServerRequestAdapter( talosActiveMqMessage, methodName));
				result = invocation.proceed();
			} catch (Exception e) {
				exception = e;
			} finally {
				serverResponseInterceptor.handle(new ActiveMqServerResponseAdapter(exception));
				if( null != exception ) {
					throw exception;
				}
			}

			return result;
		} else {
			return invocation.proceed();
		}

	}

	public Talos getTalos() {
		return talos;
	}

	public void setTalos(Talos talos) {
		this.talos = talos;
		if(null != talos) {
			this.serverRequestInterceptor = this.talos.serverRequestInterceptor();
			this.serverResponseInterceptor = this.talos.serverResponseInterceptor();
		}
	}
}
