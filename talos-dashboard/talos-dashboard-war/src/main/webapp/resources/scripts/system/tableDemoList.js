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
/*******************************************使用说明************************************************************/

/**
 *    form页面 编写规则
 *
 *
 *    1. 条件框 需要name  2 时间组件id ='dateRange'（后台接受参数为 beginDate\endDate）
 *    3 form id='form' url='调用的url' 4 table 显示div id='infoList' 5 查询按钮 id='submitQuery'
 *
 *
 *    form页面 加入 searchForm.js
 *    后台不变 此时 该页面已经具有  查询 分页 功能 此时如果是存展示不用写单独的js
 *
 *    调用周期 ----->移步看代码 人人看得懂
 *
 *
 *    暴露2个初始化 功能点：
 *    1  initCallBack方：return一个function，初始table选择传入如下：
         KXin.ui.tableLoad("#infoList", seturl(), condition,callback);

 *    2 myValidator return form校验器配置 如下 return validateSettings
 *
 *    3  如果自定义初始内容  写在myInit中 也可自己整
 *
 *
 *    功能方法
 *
 *    1. getCondition 生成 form中数据 键值队 可以直接最为数据调用 可以封装 ajax请求
 *
 *    2 postformInfo 重新刷新页面方法
 *
 *    3 submitSearchForm(url) form以post的方式向后台请求
 *
 *
 */



/***********************设置table查询个性处理方法***********************************************************/



/**
 * 设置 table callBack方法
 */

var initCallBack=function(){
    return function(){
        $(".operate a.viewDetail").click(function(e){
            loadDetail(e);
        });
        $(".operate a.processBtn").click(function(e){
            processPayTrade(e);
        });
        $(".operate a.cashDetail").click(function(e) {
            queryCashDetail(e);
        });
    }
}


/**
 * 设置校验器
 */
var initValidator=function(){
    // validateSettings 变量定义在validate-settings.js中
    validateSettings.rules = {
        tradeNo : {
            maxlength : [32],
            reg_check:[true]
        },
        merchantTradeNo : {
            maxlength : [32],
            reg_check:[true]
        },
        userName : {
            maxlength : [32],
            reg_check:[true]
        },
        merchantNo: {
            maxlength : [32],
            reg_check:[true]
        }
    };
    // 给id为exportForm的表单开启校验
   return  validateSettings;
}


var myInit=function(){
    initcodeType("bankCode");
}






/*******************************form多个button处理区*************************************************/


$("#exportBtn").click(function(){
    /**
     * 调用 form提交 只需传url
     */

    submitSearchForm("/demo/table/exportList");
});


/*****************************业务处理方法**************************************************/


var queryCashDetail = function(e) {
    KXin.ui.loading();
    var orderNo = $(e.target).parent("td").parent("tr").children(".orderNo").text();
    var url = KXin.util.ctxPath("/payment/queryCashDetail/"+orderNo);
    $("#cashDetail").load(url, function(result) {
        KXin.ui.closeLoading();
        KXin.ui.layer({
            type: 1,
            title: "收银明细",
            page: {dom: "#cashDetail"},
            area: ["420px", 'auto'],
            offset: ['25%', '']
        });
    });
}



 var loadDetail = function(e) {
    KXin.ui.loading();
    var orderNo = $(e.target).parent("td").parent("tr").children(".orderNo").text();
    var url = KXin.util.ctxPath("/payment/viewDetail/"+orderNo);
    $.get(url,function(result){
        KXin.ui.closeLoading();

        KXin.ui.layer({
            type : 1,
            title : '平台支付交易明细',
            page:{html:'<div id=\"detailView\" style=\"display:block\" class=\"info-list-view\">' + result + '</div>'},
            area: ['700px', 'auto'],
            offset : ['8%', '']
        });
    });
};

 var processPayTrade = function(e){
    KXin.ui.loading();
    var orderNo = $(e.target).parent("td").parent("tr").children(".orderNo").text();
    var url = KXin.util.ctxPath("/payment/proSingalPay/"+orderNo);

    $.get(url,function(result){
        KXin.ui.closeLoading();

        if(!result.hasOwnProperty('success')){
            KXin.ui.layer({
                type : 1,
                title : '查询结果',
                page:{html:'<div id=\"detailView\" style=\"display:block\" class=\"info-list-view\">' + result + '</div>'},
                area: ['430px', 'auto'],
            });
        }
        postformInfo();
    });
};