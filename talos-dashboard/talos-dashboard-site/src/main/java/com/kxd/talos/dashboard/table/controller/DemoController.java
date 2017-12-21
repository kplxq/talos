///**
// * Kaixindai Financing Services Jiangsu Co., Ltd.
// * Copyright (c) 2012-2014 All Rights Reserved.
// */
//package com.kxd.framework.dashboard.table.controller;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import Page;
//import DateUtil;
//import com.kxd.framework.web.util.accesscode.AccessCode;
//import PageVo;
//import BaseController;
//import DemoQueryConditionForm;
//
///**
// * demo 页面
// * 
// * @authorw
// */
//@Controller
//@RequestMapping(value = "/demo")
//public class DemoController extends BaseController {
//
//    @Autowired
//    private TableDemoExtService tableDemoExtService;
//
//
//    /**
//     * demo from页面
//     *
//     * @param request
//     * @param model
//     * @return
//     */
//    @AccessCode("D010001")
//    @RequestMapping(value = "/table/list", method = RequestMethod.GET)
//    public String payTradeList(HttpServletRequest request, Model model) {
//
//        String today = DateUtil.format(DateUtil.getDate(), DateUtil.yyyy_MM_dd);
//        String fromday = DateUtil.format(DateUtil.addDays(DateUtil.getDate(), -2), DateUtil.yyyy_MM_dd);
//        String initDateRange = fromday + " 至 " + today;
//        model.addAttribute("initDateRange", initDateRange);
//
//
//        return "screen/system/tableDemoManage";
//    }
//
//    /**
//     * demo table 页面
//     *
//     * @param request
//     * @param model
//     * @return
//     */
//    @AccessCode("DE010001001")
//    @RequestMapping(value = "/table/queryList", method = RequestMethod.POST)
//    public String queryPayTradeList(HttpServletRequest request, Model model,
//                                    DemoQueryConditionForm demoQueryConditionForm) {
//
//        if (demoQueryConditionForm == null) {
//            demoQueryConditionForm = new DemoQueryConditionForm();
//        }
//
//        TableDemoConDto condition = transferQueryFormToDto(demoQueryConditionForm);
//
//        TableDemoListResult result = tableDemoExtService
//                .queryByCondition(condition);
//        if (result.isSuccess()) {
//
//            PageVo pageVo = PageVo.createPageVo(request, result.getPage());
//            model.addAttribute("data", result.getTableDemoResults());
//            model.addAttribute("page", pageVo);
//        }
//
//        return "screen/system/tableDemoTable";
//    }
//
//
//    /**
//     * 查询列表form转查询dto
//     *
//     * @param demoQueryConditionForm
//     * @return
//     * @throws java.text.ParseException
//     */
//    private TableDemoConDto transferQueryFormToDto(DemoQueryConditionForm demoQueryConditionForm) {
//        Page page = new Page();
//        page.setCurrentPage(demoQueryConditionForm.getPageNum());
//        page.setPageSize(demoQueryConditionForm.getPageSize());
//        TableDemoConDto condition = new TableDemoConDto();
//
//        condition.setBeginDate(DateUtil.parseDate(demoQueryConditionForm.getBeginDate(),
//                DateUtil.yyyy_MM_dd_HH_mm_ss));
//        condition.setEndDate(DateUtil.parseDate(demoQueryConditionForm.getEndDate(), DateUtil.yyyy_MM_dd_HH_mm_ss));
//
//
//        condition.setOrderNo(returnString(demoQueryConditionForm.getTradeNo()));
//        condition.setMerchantOrderNo(returnString(demoQueryConditionForm.getMerchantTradeNo()));
//        condition.setMerchantNo(returnString(demoQueryConditionForm.getMerchantNo()));
//        condition.setUserName(returnString(demoQueryConditionForm.getUserName()));
//        condition.setPage(page);
//
//        return condition;
//    }
//
//
//
//    // TODO
//    private String returnString(String str) {
//        if (StringUtils.isNotEmpty(str)) {
//            return str;
//        }
//        return null;
//    }
//
//
// }