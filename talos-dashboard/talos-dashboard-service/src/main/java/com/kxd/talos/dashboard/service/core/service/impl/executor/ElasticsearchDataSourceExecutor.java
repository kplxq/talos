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

import java.util.ArrayList;
import java.util.List;

import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceMultiQueryConditionDto;
import com.kxd.talos.dashboard.service.client.result.MultiQueryResult;
import com.kxd.talos.dashboard.service.core.dmo.TalosTraceElasticsearchEntity;
import com.kxd.talos.dashboard.service.core.service.intf.IBizzDataAssembler;
import com.kxd.talos.dashboard.service.core.service.intf.IDataSourceExecutor;
import com.kxd.talos.dashboard.service.core.service.intf.elasticsearch.TalosTraceRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kxd.framework.lang.AppException;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceSingleQueryConditonDto;
import com.kxd.talos.dashboard.service.client.result.ElasticsearchQueryDetailDto;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月17日
 */

@Service("elasticsearchExecutor")
public class ElasticsearchDataSourceExecutor
        implements
		IDataSourceExecutor<TalosTraceSingleQueryConditonDto, TalosTraceMultiQueryConditionDto, ElasticsearchQueryDetailDto, TalosTraceMultiQueryConditionDto, TalosTraceElasticsearchEntity> {

    private static Logger         logger = LoggerFactory.getLogger(ElasticsearchDataSourceExecutor.class);

    @Autowired
    private TalosTraceRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ElasticsearchQueryDetailDto execute(
            IBizzDataAssembler<ElasticsearchQueryDetailDto, TalosTraceMultiQueryConditionDto, TalosTraceElasticsearchEntity> assembler,
            QueryConditionRequest<TalosTraceSingleQueryConditonDto> request) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiQueryResult<ElasticsearchQueryDetailDto> executeM(
            IBizzDataAssembler<ElasticsearchQueryDetailDto, TalosTraceMultiQueryConditionDto, TalosTraceElasticsearchEntity> assembler,
            QueryConditionRequest<TalosTraceMultiQueryConditionDto> request) {
        TalosTraceMultiQueryConditionDto queryCondition = assembler.assembleRequestData(request);
        if (queryCondition == null) {
            String msg = "cant assemble real query condition with " + request.getBizzType();
            logger.error(msg);
            throw new AppException(msg);
        }

        if (queryCondition.getPage() == null) {
            String msg = "cant assemble real query condition with null page with condition:" + request.getBizzType();
            logger.error(msg);
            throw new AppException(msg);
        }

        Page<TalosTraceElasticsearchEntity> origResultList = null;
        ElasticsearchQueryDetailDto realResult = null;
        MultiQueryResult<ElasticsearchQueryDetailDto> result = new MultiQueryResult<ElasticsearchQueryDetailDto>();
        List<ElasticsearchQueryDetailDto> realResultList = new ArrayList<>();

        try {
            Pageable page = new PageRequest(queryCondition.getPage().getCurrentPage() - 1, queryCondition.getPage()
                    .getPageSize(), Sort.Direction.DESC, "createTime");

            if(StringUtils.isBlank(queryCondition.getBizzNo())) {
                origResultList = repository.findAll(page);
            } else {
                origResultList = repository.findByContents(queryCondition.getBizzNo(), page);
            }

            for (TalosTraceElasticsearchEntity origResult : origResultList) {
                realResult = assembler.assembleResultData(origResult);
                realResultList.add(realResult);
            }

        } catch (AppException appException) {
            throw appException;
        } catch (Exception e) {
            String errmsg = e.getMessage();
            String errorCode = "unknown";
            e.printStackTrace();
            throw new AppException(errmsg, errorCode);
        }

        result.setRealResultList(realResultList);

        com.kxd.framework.page.Page resultPage = new com.kxd.framework.page.Page();
        resultPage.setCurrentPage(queryCondition.getPage().getCurrentPage());
        resultPage.setPageSize(queryCondition.getPage().getPageSize());
        resultPage.setCount(origResultList.getTotalElements());
        result.setPage(resultPage);
        return result;
    }
}
