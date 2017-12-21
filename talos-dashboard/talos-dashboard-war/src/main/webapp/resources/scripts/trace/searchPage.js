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
/**
 * 设置校验器
 */
var initValidator=function(){
    // validateSettings 变量定义在validate-settings.js中
    validateSettings.rules = {
    	condition : {
    		reg_check:[true],
    		maxlength : [128]
    	}
    };
    // 给id为exportForm的表单开启校验
   return  validateSettings;
};

var initCallBack=function(){
    return function(){
        $(".operate a.viewDetail").click(function(e){
        	viewDetail(e);
        });
    };
};

var viewDetail = function(e) {
	var traceId = $(e.target).parent("td").parent("tr").children(".traceId").text();
	var menu_tab = parent.$("#tab_TALOS01010200");
	var menu = parent.$("#TALOS01010200");
	
	// 已经打开了调用链展示标签页，则设置traceId查询，并跳到该页面
	if( menu_tab.length > 0 ) {
		parent.frames["iframe_TALOS01010200"].$("#traceId").val(traceId);
		parent.frames["iframe_TALOS01010200"].$("#submitQuery").click();
		menu.trigger("click");
		return false;
	}else {
		if(menu.length>0) {
			menu.attr("traceId",traceId).trigger("click");
			return false;
		} else {
			window.open(KXin.util.ctxPath("/tree/page?traceId="+traceId));
			return false;
		}
	}
};
