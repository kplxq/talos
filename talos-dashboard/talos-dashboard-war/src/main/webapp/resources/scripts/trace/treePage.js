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
var init = function() {
	// 如果是从另一个页面跳过来的，初始化页面的traceId，一次性使用
	var menu = parent.$("#TALOS01010200");
	if(menu.length!=0) {
		var traceId = menu.attr("traceId");
		$("#traceId").val(traceId);
		menu.removeAttr("traceId");
	}
};

var initCallBack = function() {

	return function() {
		var option = {
			expandable : true
		};

		$("#trace-tree").treetable(option).treetable("expandAll");

		$(".viewContent").dblclick(function() {

			var traceId = $(this).parents("tr").attr("data-tt-id");
			var thisClass = "param-for-" + traceId.replace(/\./g, "");
			$("#span-param tbody").find("tr").each(function() {
				if ($(this).hasClass(thisClass)) {
					$(this).show();
				} else {
					$(this).hide();
				}
			});
			KXin.ui.layer({
				type : 1,
				title : "业务上下文",
				area : [ "auto", "auto" ],
				page : {
					dom : "#span-param"
				}
			});
		});
		
	      $(".viewErrorMsg").dblclick(function() {
	            var traceId = $(this).parents("tr").attr("data-tt-id");
	            //$("#errorMsgContent-"+traceId.replace(/\./g, "")).show();
	            var showId = $("#errorMsgContent-"+traceId.replace(/\./g, ""));
	            KXin.ui.layer({
	                type : 1,
	                title : "错误信息",
	                area : [ '900px', '480px' ],
	                page:{html:'<div  style=\"display:block;width:860px;\" class=\"info-list-view layer_scroll\"  >' + showId.html() + '</div>'},
//	                page : {
//	                    dom : "#span-error-msg"
//	                }
	            });
	        });
	};

};

$(document).ready(function() {

});