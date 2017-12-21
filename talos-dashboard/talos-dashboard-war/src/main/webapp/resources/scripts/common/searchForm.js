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
//调用回掉callback

var callback;


var myValidator;



/*****************************获取条件**********************************************************/

function seturl(){
    var formUrl=$("#form").data("url");
    return KXin.util.ctxPath(formUrl);
}

/**
 * 获取searchForm的数据 可直接传到后端
 * @returns {*|jQuery}
 */
function getCondition(){
    var condition=$("#form").serializeArray();

    if($("#dateRange").length>0){
        var dateRange = KXin.util.divideDateRange($("#dateRange").val());
        var startDate = '';
        var endDate = '';
        if(""!=dateRange) {
            startDate = dateRange[0];
            endDate = dateRange[1];
        }
        condition.push({name:'beginDate',value:startDate});
        condition.push({name:'endDate',value:endDate})
    }
    return condition;
}

function setPageCondition(){
    var condition=getCondition();
    condition.push({name:'pageSize',value:'10'})
    condition.push({name:'pageNum',value:'1'})
    return condition;
}

/***************************初始化**********************************************************/

function init(){
    var dateRange= $("#dateRange");
    if(dateRange.length>0){
        $('#dateRange').dateRangePicker({
            showShortcuts: false
        });
    }

    $("#form select").each(
        function(i,o){
            $(o).selectList();

        });
}



$("document").ready(function(){


    if (typeof(initCallBack) != "undefined"){
        callback=initCallBack();
    }

    if (typeof(initValidator) != "undefined"){
        myValidator= $("#form").validate(initValidator());
    }


    if (typeof(myInit) != "undefined"){
        myInit();
    }

    init();

    $("#submitQuery").click(function(){
        // 校验表单后才能提交
        if (typeof(myValidator) != "undefined"){
            if(!myValidator.form()) return false;
        }
        postformInfo();
    });

    postformInfo();
});


/**************************page查询******************************************************/


/**
 * 查询直接调用的方法
 * @param condition
 */
var postformInfo = function(condition){
    if(typeof(condition)== "undefined"){
        condition=setPageCondition();
    }
    if(typeof(callback)== "undefined"){
        KXin.ui.tableLoad("#infoList", seturl(), condition);
    }else{
        KXin.ui.tableLoad("#infoList", seturl(), condition,callback);
    }
};

var pageHandler = function(event,pageSize){
    event.preventDefault();
    var condition=getCondition();
    if(pageSize != null && typeof(pageSize)!= "undefined"){
        condition.push({name:'pageSize',value:pageSize})
        condition.push({name:'pageNum',value:'1'})
    }else{
        condition.push({name:'pageSize',value:10})
        condition.push({name:'pageNum',value:$(event.target).attr("title")})
    }

    postformInfo(condition);
};

/**
 * searchForm 提交
 * @param url
 */
var submitSearchForm=function(url){
    if (typeof(myValidator) != "undefined"){
        if(!myValidator.form()) return false;
    }
    var form = $("#form");
    form.attr("action", KXin.util.ctxPath(url));
    var dateRange = KXin.util.divideDateRange($("#dateRange").val());
    if(""!=dateRange) {
        var beginDate = $("<input id='beginDate' type='hidden' name='beginDate' value='"+dateRange[0]+"'> ");
        var endDate = $("<input id='endDate' type='hidden' name='endDate' value='"+dateRange[1]+"'> ");
        form.append(beginDate);
        form.append(endDate);
    }
    form.submit();
    if(""!=dateRange) {
        form.find("#beginDate").remove()
        form.find("#endDate").remove()
    }
}



