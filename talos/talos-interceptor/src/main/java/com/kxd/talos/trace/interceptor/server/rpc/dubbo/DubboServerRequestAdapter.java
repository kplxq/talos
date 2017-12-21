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
import com.alibaba.dubbo.rpc.support.RpcUtils;
import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.interceptor.ServerRequestAdapter;
import com.kxd.talos.trace.core.span.SpanId;
import com.kxd.talos.trace.core.tracer.TraceData;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.interceptor.server.http.TalosHttpHeaders;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月6日
 */
public class DubboServerRequestAdapter implements ServerRequestAdapter{
	
	private Invocation requestHeaders;
	private Invoker<?> invoker;
	
	public DubboServerRequestAdapter(Invoker<?> invoker,Invocation invocation){
		this.invoker        = invoker;
		this.requestHeaders = invocation;
	}

	@Override
	public TraceData getTraceData() {
        final String sampled = requestHeaders.getAttachment(TalosHttpHeaders.Sampled.getName());
        if (sampled != null) {
            if ("0".equals(sampled) || "false".equals(sampled.toLowerCase())) {
                return TraceData.builder().sample(false).build();
            } else {
                final String parentSpanId = requestHeaders.getAttachment(TalosHttpHeaders.ParentSpanId.getName());
                final String traceId = requestHeaders.getAttachment(TalosHttpHeaders.TraceId.getName());
                final String spanId = requestHeaders.getAttachment(TalosHttpHeaders.SpanId.getName());

                if (traceId != null && spanId != null) {
                    SpanId span = getSpanId(traceId, spanId, parentSpanId);
                    return TraceData.builder().sample(true).spanId(span).build();
                }
            }
        }
        return TraceData.builder().build();
	}

	@Override
	public String getSpanName() {
		String clazzName = invoker.getInterface().getSimpleName();
        String method = RpcUtils.getMethodName(requestHeaders); // 获取方法名
		return clazzName+"."+method;

	}

	@Override
	public Collection<KeyValueAnnotation> requestAnnotations() {
		return Collections.EMPTY_LIST;
	}
	
    private SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
        return SpanId.builder()
            .traceId(traceId)
            .spanId(spanId)
            .parentId(parentSpanId == null ? null : parentSpanId).build();
   }

	@Override
	public String serviceType() {
		return Constants.SERVICE_TYPE_DUBBO_SERVER;
	}

}
