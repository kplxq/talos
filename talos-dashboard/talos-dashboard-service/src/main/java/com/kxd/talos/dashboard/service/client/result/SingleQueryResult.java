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
package com.kxd.talos.dashboard.service.client.result;

import java.util.Map;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.lang.Result;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年9月29日
 */
public class SingleQueryResult<T extends Entity> extends Result {
	private static final long serialVersionUID = 6951404593542250804L;

	private T inner;
	
	private Map<String,String> parameters;

	public T getInner() {
		return inner;
	}

	public void setInner(T realResult) {
		this.inner = realResult;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public String getParameter(String key){
		if(parameters!=null && parameters.size()!=0){
			return parameters.get(key);
		}
		else return null;
	}
}
