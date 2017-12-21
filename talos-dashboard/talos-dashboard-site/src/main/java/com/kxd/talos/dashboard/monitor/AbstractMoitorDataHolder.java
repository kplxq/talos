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
package com.kxd.talos.dashboard.monitor;

import java.util.concurrent.ConcurrentHashMap;

import com.kxd.framework.monitor.consts.MonitorConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.lang.AppException;
import com.kxd.framework.lang.Result;
import com.kxd.framework.utils.DateUtil;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月26日
 */
public abstract class AbstractMoitorDataHolder implements IMonitorDataHolder {
    protected Logger                                                 logger           = LoggerFactory
                                                                                              .getLogger(MemoryMonitorDataHolder.class);

    protected final ConcurrentHashMap<String, HeartBeatDataResult>   hbDataResultList = new ConcurrentHashMap<String, HeartBeatDataResult>();

    protected final ConcurrentHashMap<String, PerformanceDataResult> pfDataResultList = new ConcurrentHashMap<String, PerformanceDataResult>();

    @Override
    public Result recieve(String content) {
        Result result = new Result();
        if (StringUtils.isBlank(content)) {
            logger.info("content is blank memorymonitordataholder.recieve will return");
            return result;
        }
        PerformanceDataResult pfDataResult = null;
        HeartBeatDataResult hbDataResult = null;

        try {
            if (StringUtils.startsWith(content, MonitorConstants.MONITOR_HB_KPI_CODE)) {
                hbDataResult = parseHB(content);
            } else if (StringUtils.startsWith(content, MonitorConstants.MONITOR_COUNTER_KPI_CODE)) {
                pfDataResult = parsePF(content);
            }

            else {
                result.fail("error-kpi-code", "kpi-code is not supported");
                return result;
            }

            if (pfDataResult != null && pfDataResult.isSuccess()) {
                PerformanceDataResult tmpPfDataResult = pfDataResultList.get(pfDataResult.getHost() + "_"
                        + pfDataResult.getAppId());
                if (tmpPfDataResult == null) {
                    tmpPfDataResult = pfDataResultList.putIfAbsent(
                            pfDataResult.getHost() + "_" + pfDataResult.getAppId(), pfDataResult);
                }

                if (tmpPfDataResult != null) {
                    synchronized (tmpPfDataResult) {
                        tmpPfDataResult.setTps60(pfDataResult.getTps60());
                        tmpPfDataResult.setTpsTotal(pfDataResult.getTpsTotal());
                        tmpPfDataResult.setLastReportTime(pfDataResult.getLastReportTime());
                        tmpPfDataResult.setProcessId(pfDataResult.getProcessId());
                        // 将数据插入sortedset中
                        tmpPfDataResult.addHisRecord(pfDataResult.getHisRecord().first());
                    }
                }
            }

            if (hbDataResult != null && hbDataResult.isSuccess()) {
                hbDataResultList.put(hbDataResult.getHost() + "_" + hbDataResult.getAppId(), hbDataResult);
            }
        } catch (AppException apE) {
            result.fail(apE.getErrorCode(), apE.getMessage());
        } catch (Exception e) {
            String errorCode = "recieve-failed";
            String errMsg = e.getMessage();
            result.fail(errorCode, errMsg);
        }

        return result;
    }

    protected PerformanceDataResult parsePF(String content) {
        PerformanceDataResult result = new PerformanceDataResult();
        String[] parseEles = content.split(";");
        if (ArrayUtils.isEmpty(parseEles)) {
            result.fail("sys-error-blank", "content is blan");
        } else if (parseEles.length < 10) {
            result.fail("sys-error-illegal-content", "data is illegal");
        }

        else {
            result.setHost(parseEles[8]);
            result.setProcessId(parseEles[9]);
            result.setAppId(parseEles.length == 11 ? parseEles[10] : "");
            result.setLastReportTime(DateUtil.parseDate(parseEles[2], DateUtil.yyyyMMddHHmmss));
            result.setTps60(Long.parseLong(parseEles[3].substring(0, parseEles[3].indexOf("."))));
            result.setTpsTotal(Long.parseLong(parseEles[5].substring(0, parseEles[5].indexOf("."))));
            PerformanceDataResult.PerformanceHisRecord hisRecord = new PerformanceDataResult.PerformanceHisRecord(result.getLastReportTime(), result.getTps60(),
                    result.getTpsTotal());
            result.addHisRecord(hisRecord);
        }
        return result;
    }

    private HeartBeatDataResult parseHB(String content) {
        HeartBeatDataResult result = new HeartBeatDataResult();
        String[] parseEles = content.split(";");
        if (ArrayUtils.isEmpty(parseEles)) {
            result.fail("sys-error-blank", "content is blan");
        } else if (parseEles.length < 5) {
            result.fail("sys-error-illegal-content", "data is illegal");
        }

        else {
            result.setHost(parseEles[2]);
            result.setProcessId(parseEles[3]);
            result.setLastReportTime(DateUtil.parseDate(parseEles[1], DateUtil.yyyyMMddHHmmss));
            result.setTotalRunningTimeInmills(Long.parseLong(parseEles[4]));
            result.setAppId(parseEles.length == 6 ? parseEles[5] : "");
        }
        return result;
    }
}
