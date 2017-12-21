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
package com.kxd.talos.dashboard.service.core.service.impl.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.hbase.core.HBaseCommand;
import com.kxd.framework.lang.AppException;
import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceMultiQueryConditionDto;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceSingleQueryConditonDto;
import com.kxd.talos.dashboard.service.client.result.MultiQueryResult;
import com.kxd.talos.dashboard.service.client.result.TalosHbaseQueryDetailDto;
import com.kxd.talos.dashboard.service.core.dmo.TalosTraceHbaseTableDmo;
import com.kxd.talos.dashboard.service.core.service.intf.IBizzDataAssembler;
import com.kxd.talos.dashboard.service.core.service.intf.IDataSourceExecutor;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月14日
 */
public class HbaseDataSourceExecutor
        implements
        IDataSourceExecutor<TalosTraceSingleQueryConditonDto, TalosTraceMultiQueryConditionDto, TalosHbaseQueryDetailDto, TalosTraceHbaseTableDmo, TalosTraceHbaseTableDmo> {
    private static Logger logger = LoggerFactory.getLogger(HbaseDataSourceExecutor.class);

    private HBaseCommand  hbaseCommand;

    @Override
    public TalosHbaseQueryDetailDto execute(
            IBizzDataAssembler<TalosHbaseQueryDetailDto, TalosTraceHbaseTableDmo, TalosTraceHbaseTableDmo> assembler,
            QueryConditionRequest<TalosTraceSingleQueryConditonDto> request) {

        TalosTraceHbaseTableDmo realQueryCondtion = new TalosTraceHbaseTableDmo();
        realQueryCondtion = assembler.assembleRequestData(request);
        if (realQueryCondtion == null) {
            logger.error("cant assemble real query condition with " + request.getBizzType());
            throw new AppException("");
        }

        TalosTraceHbaseTableDmo origResult = null;
        TalosHbaseQueryDetailDto realResult = null;
        try {
            origResult = hbaseCommand.get(realQueryCondtion);
            if (hbaseCommand == null) {
                logger.error("cant query result with id " + realQueryCondtion.getId() + " of type "
                        + request.getBizzType());
                throw new AppException("");
            }
            realResult = assembler.assembleResultData(origResult);
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
    public MultiQueryResult<TalosHbaseQueryDetailDto> executeM(
            IBizzDataAssembler<TalosHbaseQueryDetailDto, TalosTraceHbaseTableDmo, TalosTraceHbaseTableDmo> assembler,
            QueryConditionRequest<TalosTraceMultiQueryConditionDto> request) {
        return null;
    }

    public HBaseCommand getHbaseCommand() {
        return hbaseCommand;
    }

    public void setHbaseCommand(HBaseCommand hbaseCommand) {
        this.hbaseCommand = hbaseCommand;
    }

}
