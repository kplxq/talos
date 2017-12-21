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
package com.kxd.talos.dashboard.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.kxd.framework.result.JsonResult;
import com.kxd.talos.dashboard.monitor.IMonitorDataHolder;
import com.kxd.talos.dashboard.monitor.PerformanceDataResult;
import com.kxd.talos.dashboard.vo.HeartBeatDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kxd.framework.lang.Result;
import com.kxd.framework.utils.BeanUtil;
import com.kxd.framework.utils.DateUtil;
import com.kxd.talos.dashboard.monitor.HeartBeatDataResult;

import javax.annotation.Resource;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月26日
 */
@Controller
@RequestMapping("/monitor")
public class MonitorReportController {

    @Autowired
    private IMonitorDataHolder monitorDataHolder;

    // @AccessCode("TALOS01020101")
    @RequestMapping(value = "/report", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String process(@RequestBody String apiRequest) {
        Result result = monitorDataHolder.recieve(apiRequest);
        if (result.isSuccess())
            return "OK";
        else
            return result.getErrorCode();
    }

    @RequestMapping("/page")
    public String page() {
        return "screen/monitor/monitorPage";
    }

    @RequestMapping(value = "/trace")
    public String trace(String ip, String appId, Model model) {
        model.addAttribute("thisIp", ip);
        model.addAttribute("thisAppId", appId);
        return "screen/monitor/transformTrace";
    }

    @RequestMapping("hbData")
    public String hbData(Model model) {
        Map<String, HeartBeatDataResult> hbMap = monitorDataHolder.queryHBList();

        List<HeartBeatDataVo> voList = new ArrayList<>();
        if (null != hbMap) {
            for (HeartBeatDataResult result : hbMap.values()) {
                if (null == result) {
                    continue;
                }

                HeartBeatDataVo vo = new HeartBeatDataVo();
                BeanUtil.copyProperties(result, vo);
                vo.setLastReportTimeStr(DateUtil.format(result.getLastReportTime(), DateUtil.yyyy_MM_dd_HH_mm_ss));
                vo.setStatus((DateUtil.getDate().getTime() - result.getLastReportTime().getTime()) > 120000 ? HeartBeatDataVo.STATUS_OFFLINE
                        : HeartBeatDataVo.STATUS_ONLINE);
                voList.add(vo);
            }

        }

        model.addAttribute("data", voList);
        return "screen/monitor/monitorHBPage";

    }

    @RequestMapping("pfData")
    public String pfData(Model model) {
        Map<String, PerformanceDataResult> pfMap = monitorDataHolder.queryPFList();
        if (null != pfMap) {
            model.addAttribute("data", pfMap.values());
        }

        return "screen/monitor/monitorPFPage";

    }

    @RequestMapping(value = "/traceData")
    @ResponseBody
    public JsonResult traceData(String ip, String appId) {
        JsonResult jsonResult = new JsonResult(true);
        PerformanceDataResult result = monitorDataHolder.queryPFDetail(ip, appId);
        if (null == result) {
            jsonResult.fail("系统异常，result为null");
            return jsonResult;
        }
        if (!result.isSuccess()) {
            jsonResult.fail(result.getErrorCode());
        }

        SortedSet<PerformanceDataResult.PerformanceHisRecord> hisRecord = result.getHisRecord();
        if (null == hisRecord) {
            jsonResult.fail("数据为空");
        }

        List<String> timeList = new ArrayList<>();
        List<Long> tps60List = new ArrayList<>();
        List<Long> tpsTotal = new ArrayList<>();

        for (PerformanceDataResult.PerformanceHisRecord record : hisRecord) {
            timeList.add(DateUtil.format(record.getLastReportTime(), DateUtil.yyyy_MM_dd_HH_mm_ss));
            tps60List.add(record.getTps60());
            tpsTotal.add(record.getTpsTotal());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("xData", timeList);
        data.put("tps60List", tps60List);
        data.put("tpsTotal", tpsTotal);

        jsonResult.setData(data);
        return jsonResult;
    }
}
