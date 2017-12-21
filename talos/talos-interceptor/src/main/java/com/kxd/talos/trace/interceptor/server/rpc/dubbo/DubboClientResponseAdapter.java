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

import java.util.Collection;
import java.util.Collections;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ClientResponseAdapter;
import com.kxd.talos.trace.core.interceptor.ClientResponseStatus;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月7日
 */
public class DubboClientResponseAdapter implements ClientResponseAdapter {

	private Invoker<?> invoker;
	private Invocation invocation;
	private RpcException exception;

	public DubboClientResponseAdapter(Invoker<?> invoker,
			Invocation invocation, RpcException rpcException) {
		this.invocation = invocation;
		this.invoker = invoker;
		this.exception = rpcException;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<KeyValueAnnotation> responseAnnotations() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public ClientResponseStatus responseStatus() {
		ClientResponseStatus status = new ClientResponseStatus();
		if(exception != null){
			status.fail("D", String.valueOf(exception.getCode()));
		}
		return status;
	}

}
