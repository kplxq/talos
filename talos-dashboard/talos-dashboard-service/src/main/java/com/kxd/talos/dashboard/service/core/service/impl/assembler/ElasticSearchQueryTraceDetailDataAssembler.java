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
package com.kxd.talos.dashboard.service.core.service.impl.assembler;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceMultiQueryConditionDto;
import com.kxd.talos.dashboard.service.core.dmo.TalosTraceElasticsearchEntity;
import com.kxd.talos.dashboard.service.core.service.intf.IBizzDataAssembler;
import org.apache.commons.lang3.StringUtils;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.utils.DateUtil;
import com.kxd.talos.dashboard.service.client.result.ElasticsearchQueryDetailDto;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月14日
 */
public class ElasticSearchQueryTraceDetailDataAssembler
        implements
		IBizzDataAssembler<ElasticsearchQueryDetailDto, TalosTraceMultiQueryConditionDto, TalosTraceElasticsearchEntity> {

    static final Pattern method_pt    = Pattern.compile("method=(.*?)\\s");

    static final Pattern startTime_pt = Pattern.compile("startTime=(.*?)\\s");

    @Override
    public TalosTraceMultiQueryConditionDto assembleRequestData(QueryConditionRequest<? extends Entity> request) {
        return (TalosTraceMultiQueryConditionDto) request.getRealQueryCondition();
    }

    @Override
    public ElasticsearchQueryDetailDto assembleResultData(TalosTraceElasticsearchEntity origResult) {
        ElasticsearchQueryDetailDto detail = null;
        if (origResult == null) {
            return detail;
        }
        detail = new ElasticsearchQueryDetailDto();
        detail.setTraceId(origResult.getTraceid());

        String[] contents = origResult.getContents();
        if (null != contents && 0 != contents.length) {
            for (String content : contents) {
                Matcher method_mt = method_pt.matcher(content);
                if (method_mt.find()) {
                    if (method_mt.groupCount() == 1) {
                        detail.setMethodName(method_mt.group(1));
                    } else {
                        detail.setMethodName(method_mt.group());
                    }

                }

                Matcher startTime_mt = startTime_pt.matcher(content);
                if (startTime_mt.find()) {
                    String startTimeStr = null;
                    if (startTime_mt.groupCount() == 1) {
                        startTimeStr = startTime_mt.group(1);
                    } else {
                        startTimeStr = startTime_mt.group();
                    }

                    if (StringUtils.isNotBlank(startTimeStr)) {
                        try {
                            Timestamp tm = new Timestamp(Long.valueOf(startTimeStr));
                            Date date = tm;
                            detail.setStartTime(DateUtil.format(date, DateUtil.yyyy_MM_dd_HH_mm_ss));
                        } catch (Exception e) {
                            detail.setStartTime(startTimeStr);
                        }

                    }

                }

            }
        }
        return detail;
    }

//    public static void main(String[] args) {
//        Pattern pt = Pattern.compile("method=.*?\\s");
//        Matcher mt = pt
//                .matcher("111method=POST./talos-trace-service/monitor/report 721517422750112 721517426303320 /talos-trace-service/monitor/report 200 ");
//        if (mt.find()) {
//            System.out.println(mt.groupCount());
//        }
//    }
}
