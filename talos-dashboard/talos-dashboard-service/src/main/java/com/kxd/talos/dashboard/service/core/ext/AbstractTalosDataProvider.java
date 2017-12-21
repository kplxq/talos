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
package com.kxd.talos.dashboard.service.core.ext;

import java.util.List;
import java.util.Map;

import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.intf.ITalosDataProvider;
import com.kxd.talos.dashboard.service.client.result.SingleQueryResult;
import com.kxd.talos.dashboard.service.core.service.intf.IChildDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.lang.AppException;
import com.kxd.talos.dashboard.service.client.result.MultiQueryResult;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年9月29日
 */
public class AbstractTalosDataProvider implements ITalosDataProvider {

    private static Logger                   logger = LoggerFactory.getLogger(AbstractTalosDataProvider.class);

    private Map<String, IChildDataProvider> providerList;

    @Override
    public <Res extends Entity, Req extends Entity> SingleQueryResult<Res> query(QueryConditionRequest<Req> request) {

        IChildDataProvider childDataProvider = null;
        Res realResult = null;
        SingleQueryResult<Res> returnResult = new SingleQueryResult<Res>();

        try {
            if (providerList != null && providerList.size() >= 1) {
                childDataProvider = providerList.get(request.getDataSourceType());
                Assert.isTrue(childDataProvider != null, "provider is null");
            }

            realResult = childDataProvider.query(request);
            returnResult.setInner(realResult);

        } catch (AppException appException) {
            returnResult.fail(appException.getErrorCode(), appException.getMessage());
        } catch (Exception e) {
            returnResult.fail("unknown", e.getMessage());
        }
        return returnResult;
    }

    @Override
    public <Res extends Entity, Req extends Entity> MultiQueryResult<Res> queryMultiResult(
            QueryConditionRequest<Req> request) {
        IChildDataProvider childDataProvider = null;
        List<Res> realResultList = null;
        MultiQueryResult<Res> returnResult = new MultiQueryResult<Res>();

        try {
            if (providerList != null && providerList.size() >= 1) {
                childDataProvider = providerList.get(request.getDataSourceType());
                Assert.isTrue(childDataProvider != null, "provider is null");
            }

            returnResult = childDataProvider.queryMulti(request);

        } catch (AppException appException) {
            returnResult.fail(appException.getErrorCode(), appException.getMessage());
        } catch (Exception e) {
            returnResult.fail("unknown", e.getMessage());
        }
        return returnResult;
    }

    public Map<String, IChildDataProvider> getProviderList() {
        return providerList;
    }

    public void setProviderList(Map<String, IChildDataProvider> providerList) {
        this.providerList = providerList;
    }
}
