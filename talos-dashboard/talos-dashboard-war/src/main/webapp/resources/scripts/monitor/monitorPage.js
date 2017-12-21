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
var viewTrace = function(host,appId) {
	
	$("#transform-trace").load(KXin.util.ctxPath("/monitor/trace?ip="+host+"&&appId="+appId),null,function(result){
		KXin.ui.layer({
			type : 1,
			title : "性能数据",
			area :['620px','460px'],
			page : { dom : "#transform-trace" },
			btn : ["确定"],
			btns : 1
		});
	});
};

(function($){
	var initHBData = function() {
		KXin.ui.tableLoad("#heartbeat-data",KXin.util.ctxPath("/monitor/hbData"));
	};
	
	var initPFData = function() {
		KXin.ui.tableLoad("#performance-data",KXin.util.ctxPath("/monitor/pfData"));
	};
	
	var init = function(){
		initHBData();
		initPFData();
	};
	
	init();
	
})(jQuery);