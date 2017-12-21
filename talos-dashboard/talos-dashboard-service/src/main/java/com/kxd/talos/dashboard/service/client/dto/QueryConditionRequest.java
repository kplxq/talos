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
package com.kxd.talos.dashboard.service.client.dto;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.lang.Request;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月29日
 */
public class QueryConditionRequest<T extends Entity> extends Request{
	private static final long serialVersionUID = -2510462262965836596L;
	/**
	 * 实际的请求参数
	 */
	private T realQueryCondition;
	
	/**
	 * 业务类型,如查询trace数据(hbase)、查询traceId等(es)
	 */
	private String bizzType;
	
	/**
	 * 当前仅支持hbase/elasticsearch
	 */
	private String dataSourceType;

	public T getRealQueryCondition() {
		return realQueryCondition;
	}

	public void setRealQueryCondition(T realQueryCondition) {
		this.realQueryCondition = realQueryCondition;
	}

	public String getBizzType() {
		return bizzType;
	}

	public void setBizzType(String bizzType) {
		this.bizzType = bizzType;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

}
