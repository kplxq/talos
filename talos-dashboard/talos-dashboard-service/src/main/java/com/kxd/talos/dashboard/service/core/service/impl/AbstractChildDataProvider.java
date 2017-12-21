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
package com.kxd.talos.dashboard.service.core.service.impl;

import java.util.Map;

import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.result.MultiQueryResult;
import com.kxd.talos.dashboard.service.core.service.intf.IBizzDataAssembler;
import com.kxd.talos.dashboard.service.core.service.intf.IChildDataProvider;
import com.kxd.talos.dashboard.service.core.service.intf.IDataSourceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.lang.AppException;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年9月29日
 */
public abstract class AbstractChildDataProvider implements IChildDataProvider {
    private static Logger                   logger = LoggerFactory.getLogger(AbstractChildDataProvider.class);

    private Map<String, IBizzDataAssembler> assemblerList;

    private IDataSourceExecutor executor;

    @Override
    public <T extends Entity, R extends QueryConditionRequest<?>> T query(R condition) {
        T realResult = null;
        IBizzDataAssembler dataAssembler = assemblerList.get(condition.getBizzType());

        try {
            if (dataAssembler == null) {
                logger.error("cant find a data assembler for data provider " + dataProviderType()
                        + " and bizz type is " + condition.getBizzType());
                throw new AppException("");
            }

            realResult = (T) executor.execute(dataAssembler, condition);

        } catch (AppException appException) {
            throw appException;
        } catch (Exception e) {
            String errmsg = e.getMessage();
            String errorCode = "unknown";
            e.printStackTrace();
            throw new AppException(errmsg, errorCode);
        }
        return realResult;
    }

    @Override
    public <K extends Entity, J extends QueryConditionRequest<?>> MultiQueryResult<K> queryMulti(J condition) {
        MultiQueryResult<K> realResultList = null;
        IBizzDataAssembler dataAssembler = assemblerList.get(condition.getBizzType());
        try {
            if (dataAssembler == null) {
                logger.error("cant find a data assembler for data provider " + dataProviderType()
                        + " and bizz type is " + condition.getBizzType());
                throw new AppException("");
            }

            realResultList = executor.executeM(dataAssembler, condition);

        } catch (AppException appException) {
            throw appException;
        } catch (Exception e) {
            String errmsg = e.getMessage();
            String errorCode = "unknown";
            e.printStackTrace();
            throw new AppException(errmsg, errorCode);
        }
        return realResultList;
    }

    public Map<String, IBizzDataAssembler> getAssemblerList() {
        return assemblerList;
    }

    public void setAssemblerList(Map<String, IBizzDataAssembler> assemblerList) {
        this.assemblerList = assemblerList;
    }

    public IDataSourceExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(IDataSourceExecutor executor) {
        this.executor = executor;
    }

    public abstract String dataProviderType();

}
