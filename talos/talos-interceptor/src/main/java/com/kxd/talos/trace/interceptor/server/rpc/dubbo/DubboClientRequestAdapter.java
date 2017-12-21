/**
 * Copyright 2012-2017 Kaixindai Financing Services Jiangsu Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kxd.talos.trace.interceptor.server.rpc.dubbo;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.support.RpcUtils;
import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.interceptor.ClientRequestAdapter;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.core.utils.InetAddressUtilities;
import com.kxd.talos.trace.interceptor.server.http.TalosHttpHeaders;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月7日
 */
public class DubboClientRequestAdapter implements ClientRequestAdapter {

	private Invocation requestHeaders;
	private Invoker<?> invoker;
	
	public DubboClientRequestAdapter(Invoker<?> invoker,Invocation invocation){
		this.invoker        = invoker;
		this.requestHeaders = invocation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSpanName() {
		String clazzName = invoker.getInterface().getSimpleName();
        String method = RpcUtils.getMethodName(requestHeaders); // 获取方法名
		return clazzName+"."+method;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSpanIdToRequest(Span span) {
		if(span == Span.EMPTY_SPAN){
			requestHeaders.getAttachments().put(TalosHttpHeaders.Sampled.getName(), "0");
		}
		
		else{
			requestHeaders.getAttachments().put(TalosHttpHeaders.Sampled.getName(), "1");
			requestHeaders.getAttachments().put(TalosHttpHeaders.TraceId.getName(), span.getTrace_id());
			requestHeaders.getAttachments().put(TalosHttpHeaders.SpanId.getName(), span.getId());
			requestHeaders.getAttachments().put(TalosHttpHeaders.ParentSpanId.getName(), span.getParent_id());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<KeyValueAnnotation> requestAnnotations() {
        return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Endpoint serverAddress() {
	    InetSocketAddress remoteAddress = RpcContext.getContext().getRemoteAddress();
	    int ipv4 = 0;
		try {
			if(remoteAddress != null){ipv4 = InetAddressUtilities.toInt(remoteAddress.getAddress());}
		} catch (Exception e) {
			ipv4 = 0;
		}
		String serviceName = invoker.getUrl().getParameter("application");
	    int port = RpcContext.getContext().getRemotePort();
	    Endpoint endPoint = Endpoint.create(serviceName, ipv4, port);
		return endPoint;
	}

	@Override
	public String serviceType() {
		// TODO Auto-generated method stub
		return Constants.SERVICE_TYPE_DUBBO_CLIENT;
	}

}
