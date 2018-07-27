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

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.interceptor.ClientRequestInterceptor;
import com.kxd.talos.trace.core.interceptor.ClientResponseInterceptor;
import org.apache.commons.lang3.StringUtils;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月7日
 */
@Activate(group = { Constants.CONSUMER },order=-25000)
public class TalosDubboRpcClientInterceptor implements Filter {
	
	private ClientRequestInterceptor clientRequestInterceptor;
	private ClientResponseInterceptor clientResponseInterceptor;
	private Talos brave;
	private volatile Object talosLock = new Object();

	public TalosDubboRpcClientInterceptor(){
		System.out.println(" i am client interceptor");
	}
	
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation)
			throws RpcException {
		
		synchronized(talosLock){
			if(brave == null){
				String application = invoker.getUrl().getParameter("application");
				application = StringUtils.isBlank(application) ? "unknownApplication" : application;
				String sampleRateStr = invoker.getUrl().getParameter("sampleRate");
				float sampleRate =1.0f;
				try {sampleRate = Float.parseFloat(sampleRateStr);} catch (Exception e) {}
				brave = new Talos(application,sampleRate);
				clientRequestInterceptor = brave.clientRequestInterceptor();
				clientResponseInterceptor = brave.clientResponseInterceptor();
		}
		}
		
		Result result = null;
        RpcException rpcException = null;
		
		try {
			clientRequestInterceptor.handle(new DubboClientRequestAdapter(invoker,invocation));
			result = invoker.invoke(invocation);
			if(result.hasException()){
				throw new RpcException(result.getException());
			}
		} catch (RpcException exception) {
			rpcException = exception;
			throw exception;
		} finally{
			clientResponseInterceptor.handle(new DubboClientResponseAdapter(invoker, invocation, rpcException));
		}
		return result;
	}
	
}
