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
(function($){
	
	var myChart = echarts.init(document.getElementById("trace-echarts"));
	
	var ip = $("#ip").val();
	var appId = $("#appId").val();
	
	option = {
	    title: {
	        text: ip + " " + appId
	    },
	    legend: {
	        data:['60s tps','total tps']
	    },
	    xAxis:  {
	        data: ['2016-10-26 16:54:16', '2016-10-26 16:55:16', '2016-10-26 16:56:16', '2016-10-26 16:57:16', '2016-10-26 16:58:16', '2016-10-26 16:59:16', '2016-10-26 17:00:16', '2016-10-26 17:01:16', '2016-10-26 17:02:16', '2016-10-26 17:03:16', '2016-10-26 17:04:16']
	    },
	    yAxis: {
	    },
	    series: [
	        {
	            name:'60s tps',
	            type:'line',
	            data:[2271, 2232, 2194, 2158, 2123, 2088, 2055, 2023, 1992, 1962, 1933],
	            markPoint: {
	                data: [
	                    {type: 'max', name: '最大值'},
	                    {type: 'min', name: '最小值'}
	                ]
	            },
	            markLine: {
	                data: [
	                    {type: 'average', name: '平均值'}
	                ]
	            }
	        },
	        {
	            name:'total tps',
	            type:'line',
	            data:[1271, 1232, 1194, 1158, 1123, 1088, 1055, 1023, 992, 962, 933],
	            markPoint: {
	                data: [
	                    {type: 'max', name: '最大值'},
	                    {type: 'min', name: '最小值'}
	                ]
	            },
	            markLine: {
	                data: [
	                    {type: 'average', name: '平均值'}
	                ]
	            }
	        }
	    ]
	};
	
	myChart.setOption(option);
	
	$.post(KXin.util.ctxPath("/monitor/traceData"),{ip:ip, appId: appId}, function(result){
		if(!result.success) {
			KXin.ui.alert(result.message);
		} else {
			option.xAxis.data = result.data.xData;
			option.series[0].data = result.data.tps60List;
			option.series[1].data = result.data.tpsTotal;
			myChart.setOption(option);
		}
	});

})(jQuery);
