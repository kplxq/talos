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

import javax.servlet.http.HttpServletRequest;

import com.kxd.talos.dashboard.service.core.consts.DataLayerBizzTypeEnums;
import com.kxd.talos.dashboard.service.core.consts.DataLayerSourceTypeEnums;
import com.kxd.talos.dashboard.form.SearchForm;
import com.kxd.talos.dashboard.framework.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kxd.talos.dashboard.common.vo.PageVo;
import com.kxd.talos.dashboard.service.client.dto.QueryConditionRequest;
import com.kxd.talos.dashboard.service.client.dto.TalosTraceMultiQueryConditionDto;
import com.kxd.talos.dashboard.service.client.intf.ITalosDataProvider;
import com.kxd.talos.dashboard.service.client.result.ElasticsearchQueryDetailDto;
import com.kxd.talos.dashboard.service.client.result.MultiQueryResult;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月19日
 */

@Controller
@RequestMapping("/search")
public class TraceSearchController extends BaseController {

    @Autowired
    private ITalosDataProvider iTalosDataProvider;

    private long                queryTotalMax = 10000L;

    @RequestMapping("/page")
    public String page(Model model) {
        model.addAttribute("queryTotalMax", queryTotalMax);
        return "screen/trace/searchPage";
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String query(SearchForm form, HttpServletRequest request, Model model) {

        checkTotalMax(form);

        QueryConditionRequest<TalosTraceMultiQueryConditionDto> conditon = new QueryConditionRequest<TalosTraceMultiQueryConditionDto>();
        TalosTraceMultiQueryConditionDto realCondition = new TalosTraceMultiQueryConditionDto();
        realCondition.setBizzNo(form.getContent().replace("/"," "));
        realCondition.setPage(form.getPageNum(), form.getPageSize());
        conditon.setBizzType(DataLayerBizzTypeEnums.ELASTICSEARCH_MUTIL_QUERY.code());
        conditon.setDataSourceType(DataLayerSourceTypeEnums.ES.code());
        conditon.setRealQueryCondition(realCondition);
        MultiQueryResult<ElasticsearchQueryDetailDto> result = iTalosDataProvider.queryMultiResult(conditon);
        model.addAttribute("dataList", result.getRealResultList());
        if (result.getPage() != null) {
            PageVo pageVo = PageVo.createPageVo(request, result.getPage());
            model.addAttribute("page", pageVo);
        }

        return "screen/trace/searchResult";
    }

    /**
     * 检查form大小，如果超过可查询的笔数，只能查最后那几条
     * @param form
     */
    private void checkTotalMax(SearchForm form) {
        if ((form.getPageNum() - 1) * (form.getPageSize()) > Long.valueOf(queryTotalMax)) {
            form.setPageNum((int) (Long.valueOf(queryTotalMax) / (form.getPageSize()) - 1));
        }
    }

}
