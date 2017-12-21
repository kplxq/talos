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
package com.kxd.talos.dashboard.service.client.intf;

import com.kxd.framework.core.entity.Entity;
import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.result.MultiQueryResult;
import com.kxd.talos.dashboard.service.client.result.SingleQueryResult;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月29日
 */
public interface ITalosDataProvider {
	/**
	 * 查询单条数据
	 * @param request
	 * @return
	 */
	public <Res extends Entity,Req extends Entity> SingleQueryResult<Res> query(QueryConditionRequest<Req> request);
	
	/**
	 * 查询多条数据
	 * @param request
	 * @return
	 */
	public <Res extends Entity,Req extends Entity> MultiQueryResult<Res> queryMultiResult(QueryConditionRequest<Req> request);

}
