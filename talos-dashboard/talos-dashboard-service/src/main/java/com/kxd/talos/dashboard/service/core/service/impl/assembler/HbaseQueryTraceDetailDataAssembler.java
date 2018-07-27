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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.result.TalosHbaseQueryDetailDto;
import com.kxd.talos.dashboard.service.client.result.TalosSpanDetailDto;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.core.entity.Entity;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceSingleQueryConditonDto;
import com.kxd.talos.dashboard.service.core.dmo.TalosTraceHbaseTableDmo;
import com.kxd.talos.dashboard.service.core.service.intf.IBizzDataAssembler;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月14日
 */
public class HbaseQueryTraceDetailDataAssembler implements
        IBizzDataAssembler<TalosHbaseQueryDetailDto, TalosTraceHbaseTableDmo, TalosTraceHbaseTableDmo> {

    static final Logger logger = LoggerFactory.getLogger(HbaseQueryTraceDetailDataAssembler.class);

    static final String OLD_SPLIT_STR = "&";

    static final String NEW_SPLIT_STR = "&@&@&";

    @Override
    public TalosTraceHbaseTableDmo assembleRequestData(QueryConditionRequest<? extends Entity> request) {
        TalosTraceHbaseTableDmo dmo = new TalosTraceHbaseTableDmo();
        TalosTraceSingleQueryConditonDto conditionDto = (TalosTraceSingleQueryConditonDto) request
                .getRealQueryCondition();
        dmo.setId(conditionDto.getTraceId());
        return dmo;
    }

    @Override
    public TalosHbaseQueryDetailDto assembleResultData(TalosTraceHbaseTableDmo origResult) {
        TalosHbaseQueryDetailDto detail = null;
        if (origResult == null) {
            return detail;
        }
        detail = new TalosHbaseQueryDetailDto();
        detail.setTraceId(origResult.getId());
        detail.setColumns(origResult.getRow());
        detail.setSpanInfos(convertColumnsToDto(origResult.getRow()));
        return detail;
    }

    /**
     * @param row
     * @return
     */
    private static Map<String, TalosSpanDetailDto> convertColumnsToDto(Map<String, String> row) {
        if (null == row) {
            return null;
        }

        Map<String, TalosSpanDetailDto> result = new HashMap<>();

        for (Entry<String, String> entry : row.entrySet()) {
            String key = entry.getKey();
            String span = entry.getValue();
            if (StringUtils.isEmpty(span)) {
                continue;
            }

            TalosSpanDetailDto dto = new TalosSpanDetailDto();

            for (String data : span.split("\\|")) {
                if (StringUtils.isEmpty(data)) {
                    continue;
                }

                String title, val;

                if(data.indexOf(":") > 0) {
                    title = data.substring(0, data.indexOf(":"));
                    val = data.substring(data.indexOf(":")+1);
                } else {
                    if (!"TALOS".equals(data) && !"SKYEYE".equals(data) && !"CT:".equals(data)) {
                        logger.warn("data format error, can't split by ':', data:{}", data);
                    }
                    continue;
                }

                switch (title) {
                case "TD":
                    dto.setTraceId(val);
                    break;
                case "SI":
                    dto.setSpanId(val);
                    break;
                case "PI":
                    dto.setParentSpanId(val);
                    break;
                case "SN":
                    dto.setMethod(val);
                    break;
                case "HT":
                    dto.setHost(val);
                    break;
                case "TP":
                    dto.setType(val);
                    break;
                case "ET":
                    dto.setExType(val);
                    break;
                case "EC":
                    dto.setErrorCode(val);
                    break;
                case "ST":
                    dto.setStartTime(val);
                    break;
                case "RT":
                    dto.setDuration(val);
                    break;
                case "TN":
                    dto.setThreadName(val);
                    break;
                case "AN":
                    dto.setAppName(val);
                    break;
                case "PD":
                    dto.setProcessId(val);
                    break;
                case "EM":
                    dto.setErrorMessage(val);
                    break;
                case "CT":
                    dto.setContent(convertContentToMap(val));
                    break;
                default:
                    logger.error("undefinded title:{}, value:{}", key, val);
                    break;
                }
            }

            result.put(key, dto);
        }

        return result;
    }

    /**
     * @param val
     * @return
     */
    private static Map<String, String> convertContentToMap(String val) {
        if (StringUtils.isEmpty(val)) {
            return null;
        }

        if(val.indexOf(NEW_SPLIT_STR) > -1) {
            return convertContentToMap(val, NEW_SPLIT_STR);
        } else if(val.indexOf(OLD_SPLIT_STR) > -1) {
            return convertContentToMap(val, OLD_SPLIT_STR);
        } else {
            return null;
        }
    }

    static Map<String, String> convertContentToMap(String val, String splitStr) {
        Map<String, String> content = new HashMap<>();
        for (String row : val.split(splitStr)) {
            String[] oneRow = row.split("=");
            if (oneRow.length != 2) {
                logger.warn("data format error, can't split by '=', data:{}", row);
                continue;
            }

            content.put(oneRow[0], oneRow[1]);
        }
        return content;
    }

    public static void main(String[] args) {
        Map<String, String> row = new HashMap<>();
        row.put("0.1.1", "TALOS|TD:-4897174466458811430|SI:0.1.1|PI:0.1|SN:ServiceA.methodA1|"
                + "HT:-1407228991|TP:local|ET:N|EC:success|ST:81960590|RT:9|"
                + "TN:31289256@qtp-23166263-2|AN:talos-service|PD:4276|" + "CT:lhldyf=lhldyf-methodA1&aaa=bbb");
        System.out.println(convertColumnsToDto(row));
    }

}
