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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ServerResponseAdapter;
import com.kxd.talos.trace.core.interceptor.ServerResponseStatus;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年9月6日
 */
public class DubboServerResponseAdapter implements ServerResponseAdapter {

	private Invoker<?> invoker;
	private Invocation invocation;
	private RpcException exception;

	public DubboServerResponseAdapter(Invoker<?> invoker,
			Invocation invocation, RpcException rpcException) {
		this.invocation = invocation;
		this.invoker = invoker;
		this.exception = rpcException;
	}

	@Override
	public Collection<KeyValueAnnotation> responseAnnotations() {
		List<KeyValueAnnotation> annotations = new ArrayList<KeyValueAnnotation>();
		return annotations;
	}

	@Override
	public ServerResponseStatus responseStatus() {
		ServerResponseStatus status = new ServerResponseStatus();
		if(exception != null){
			status.fail("D", String.valueOf(exception.getCode()));
		}
		return status;
	}

}
