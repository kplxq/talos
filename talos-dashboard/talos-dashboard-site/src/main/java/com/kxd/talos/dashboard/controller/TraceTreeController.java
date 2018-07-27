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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kxd.talos.dashboard.service.core.consts.DataLayerBizzTypeEnums;
import com.kxd.talos.dashboard.service.core.consts.DataLayerSourceTypeEnums;
import com.kxd.talos.dashboard.vo.TalosSpanDetailVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kxd.framework.utils.BeanUtil;
import com.kxd.framework.utils.DateUtil;
import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceSingleQueryConditonDto;
import com.kxd.talos.dashboard.service.client.intf.ITalosDataProvider;
import com.kxd.talos.dashboard.service.client.result.SingleQueryResult;
import com.kxd.talos.dashboard.service.client.result.TalosHbaseQueryDetailDto;
import com.kxd.talos.dashboard.service.client.result.TalosSpanDetailDto;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月19日
 */
@Controller
@RequestMapping("tree")
public class TraceTreeController {

    @Autowired
    private ITalosDataProvider iTalosDataProvider;

    @RequestMapping("page")
    public String page(String traceId, Model model) {
        model.addAttribute("thisTraceId", traceId);
        return "screen/trace/treePage";
    }

    @RequestMapping("query")
    public String query(String traceId, Model model) {
        if (StringUtils.isBlank(traceId)) {
            return "screen/trace/treeResult";
        }
        QueryConditionRequest<TalosTraceSingleQueryConditonDto> conditon = new QueryConditionRequest<TalosTraceSingleQueryConditonDto>();
        TalosTraceSingleQueryConditonDto realCondition = new TalosTraceSingleQueryConditonDto();

        realCondition.setTraceId(traceId);
        conditon.setBizzType(DataLayerBizzTypeEnums.HBASE_SINGLE_QUERY.code());
        conditon.setDataSourceType(DataLayerSourceTypeEnums.HBASE.code());
        conditon.setRealQueryCondition(realCondition);

        SingleQueryResult<TalosHbaseQueryDetailDto> result = iTalosDataProvider.query(conditon);

        if (null == result || null == result.getInner()) {
            return "screen/trace/treeResult";
        }

        Map<String, TalosSpanDetailDto> spanInfos = result.getInner().getSpanInfos();

        List<TalosSpanDetailVo> list = new ArrayList<>();
        list = convertToZTree(spanInfos, list, "0.1");

        if (list.size() != spanInfos.size()) {
            list = convertToNoSortTree(spanInfos);
        }

        model.addAttribute("dataList", list);
        model.addAttribute("traceId", traceId);
        return "screen/trace/treeResult";
    }

    private List<TalosSpanDetailVo> convertToNoSortTree(Map<String, TalosSpanDetailDto> spanInfos) {
        List<TalosSpanDetailVo> list = new ArrayList<>();
        for (TalosSpanDetailDto dto : spanInfos.values()) {
            list.add(contructTalosSpanDetailVo(dto));
        }
        return list;
    }

    /**
     * 递归遍历map，排序调用链
     * @param spanInfos
     * @param list
     * @param currentId
     * @return
     */
    private List<TalosSpanDetailVo> convertToZTree(Map<String, TalosSpanDetailDto> spanInfos,
            List<TalosSpanDetailVo> list, String currentId) {

        if (spanInfos.get(currentId) != null) {
            list.add(contructTalosSpanDetailVo(spanInfos.get(currentId)));
        }

        // 已遍历完成，或者下一节点已不存在
        if (list.size() == spanInfos.size() || "0.2".equals(currentId) || currentId.indexOf(".") <= 0) {
            return list;
        }

        String childId = currentId + ".1";
        String brotherId = getBrotherId(currentId);
        String uncleId = getBrotherId(getParentId(currentId));
        TalosSpanDetailDto childDto = spanInfos.get(childId);
        TalosSpanDetailDto brotherDto = spanInfos.get(brotherId);
        TalosSpanDetailDto uncleDto = spanInfos.get(uncleId);

        if (null != childDto) {
            // 如果儿子节点有值，再去找孙子节点
            convertToZTree(spanInfos, list, childId);
        } else if (null != brotherDto) {
            // 如果儿子节点为空，兄弟节点有值
            convertToZTree(spanInfos, list, brotherId);
        } else if (null != uncleDto) {
            // 如果兄弟节点为空，叔叔节点有值
            convertToZTree(spanInfos, list, uncleId);
        } else {
            // 如果叔叔节点为空，去遍历叔叔节点的叔叔节点
            convertToZTree(spanInfos, list, getBrotherId(getParentId(uncleId)));
        }

        return list;
    }

    TalosSpanDetailVo contructTalosSpanDetailVo(TalosSpanDetailDto dto) {
        if (null == dto) {
            return null;
        }
        TalosSpanDetailVo vo = new TalosSpanDetailVo();
        BeanUtil.copyProperties(dto, vo);
        if (!"0".equals(vo.getHost())) {
            vo.setHostIp(toIp(Integer.valueOf(vo.getHost())));
        } else {
            vo.setHostIp("");
        }
        if(vo.getContent() != null){
        	for (Entry<String, String> entry : vo.getContent().entrySet()) {
                if(StringUtils.equals(entry.getKey(), "ss")||StringUtils.equals(entry.getKey(), "sr")||StringUtils.equals(entry.getKey(), "cs")||StringUtils.equals(entry.getKey(), "cr")){
                	Long time = null;
                	try {
                	    time = Long.valueOf(entry.getValue());
                    } catch (Exception e) {
                	    continue;
                    }
                    Date date = new Date(time);
                    entry.setValue(DateUtil.format(date, DateUtil.yyyy_MM_dd_HH_mm_ss));
                }
            }
        }
        return vo;
    }

    public static String getBrotherId(String currentId) {
        try {
            if (currentId.contains(".")) {
                return currentId.substring(0, currentId.lastIndexOf(".")) + "."
                        + (Integer.valueOf(currentId.substring(currentId.lastIndexOf(".") + 1)) + 1);
            } else {
                return String.valueOf(Integer.valueOf(currentId) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static String getParentId(String currentId) {
        try {
            return currentId.substring(0, currentId.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
     * from InetAddressUtilities
     */
    public static String toIp(int ipInt) {
        return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.').append((ipInt >> 16) & 0xff).append('.')
                .append((ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff)).toString();
    }

}
