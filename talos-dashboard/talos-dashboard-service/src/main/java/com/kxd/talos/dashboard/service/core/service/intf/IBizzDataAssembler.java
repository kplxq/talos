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
package com.kxd.talos.dashboard.service.core.service.intf;

import com.kxd.framework.core.entity.Entity;
import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月29日
 */
public interface IBizzDataAssembler<T,R,M> {
	
	/**
	 * 组装实际的请求数据
	 * request为实际的请求数据
	 * @return
	 */
	public R assembleRequestData(QueryConditionRequest<? extends Entity> request);
	
	/**
	 * 组装实际的响应数据
	 * origResult是指由datasource直接查询所获得的原始结果
	 * @return
	 */
	public T assembleResultData(M origResult);

}
