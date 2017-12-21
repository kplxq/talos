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
(function($) {
    $.fn.kxin = function(options) {  
        var defaults = { 
        };
        var options = $.extend(defaults, options); 
    };
})(jQuery);

;!function(window,undefined){
    window.KXin = {};
    KXin.util = KXin.util  || {};
    KXin.ui = KXin.ui  || {};
    
    KXin.ui={
            //显示错误信息提示
         errorTip:function (id, msg, direction){
            var _$error = $(id);
            _$error.parent().css("z-index",999).parents("div.input").addClass("error");
            if(direction == "" || typeof direction == "undefined"){
            	if(_$error.next("em").length == 0) {
            		_$error.after("<em><i></i>"+ msg +"</em>");
            	} else {
            		_$error.next("em").html("<i></i>"+msg);
            	}
            }else{
                _$error.after("<em class="+ direction +"><i></i>"+ msg +"</em>");
            }
          },
          removeErrorTip:function (id){
            var _$error = $(id);
            _$error.parents("div.input").removeClass("error");
            _$error.next("em").remove();
          },
          //iframe 自适应高度
          iFrameHeight:function(id) {
            var ifm= document.getElementById(id);
            var subWeb = document.frames ? document.frames[id].document : ifm.contentDocument;
            if(ifm != null && subWeb != null) {
                ifm.height = subWeb.body.scrollHeight;
            }
          },
          global_load_index : -1,	// 全局控制一个弹出等待层的index
          loading:function(msg){
        	  if(null==msg || ""==msg){
        		  msg = "处理中，请稍后……";
        	  }
        	  global_load_index = layer.load(msg);
        	  return global_load_index;
          },
          closeLoading:function(i){
        	  if(null==i || typeof i == "undefined") {
        		  return layer.close(global_load_index);
        	  }
        	  return layer.close(i);
          },
          alert:function(msg, fn, yes){
        	  layer.alert(msg, -1, fn, yes);
          },
          confirm:function(msg, yes, fn, no){
        	  return layer.confirm(msg, yes, fn, no);
          },
          tableTrColor:function(obj){
        	  //表格效果
              obj.find("tr:even").addClass("odd");
              obj.find("tr").on({
                  mouseover:function(){
                      $(this).addClass("hover");
                  },
                  mouseout:function(){
                      $(this).removeClass("hover");
                  }
              });
          },
          tableLayer : function(element,url,data,callback,title) {
        	  KXin.ui.tableLoad(element,url,data,callback);
        	  var $table = $(element);
        	  if(null==title || typeof title =="undefined" ) title = "温馨提示";
        	  $table.css("width","1000px");	// 固定宽度1000px
        	  $.layer({
        		 type : 1,
        		 title : title,
        		 area : ["1050px","480px"],
         		 offset : ["12px","68px"],
         		 page : { dom : $table }
        	  });
          },
          disableButton : function(element) {
        	  $(element).addClass("disgrey").attr("disabled","disabled");
          },
          ableButton : function(element) {
        	  $(element).removeClass("disgrey").removeAttr("disabled");
          },
          tableLoad : function(element,url,data,callback) {
          	var $table = $(element);
          	var table_height = $table.height();
          	if(table_height<60){
          		table_height=60;
          		$table.css("min-height",table_height+"px");
          	}
          	// 查询loading画面
          	$table.append("<div class='dataLoading' height="+table_height+"></div><div class='dataLoadingImg'></div>");
          	callback = ( typeof data == "function" ) ? data : callback;
          	$table.load(url,data,function(){
          		if( $table.find("tr").length <= 1) {
          			var td = $table.find("th").length;
          			$table.find("table").append('<tr><td colspan='+td+'>暂无数据</td></tr>');
          		}
          		//表格效果
          		KXin.ui.tableTrColor($table);
          		// 表单数据格式
          		initFormat();
          		// 自定义回调函数
          		if("function" == typeof callback )	callback();
          	});
          },
          layer : function(layerData){
        	  var i = $.layer(layerData);
        	  initFormat();
        	  return i;
          },
          closeAllLayer: function(){
              var layerObj = $('.xubox_layer');
              
              $.each(layerObj, function(){
                var i = $(this).attr('times');
                layer.close(i);
              });
          }
    };
    
    KXin.util={
        ctxPath :function (src){
            return KXin_Config.bathPath+src;
        },
        //时间控件拆分时间,第二个字段是返回的日期是否为日期值
        divideDateRange:function(dateRange,isDate){
        	if(""==dateRange) return "";
            var dates =  dateRange.split(" 至 ");
            if(!isDate) {
            	dates[0] = dates[0]+' 00:00:00';
                dates[1] = dates[1]+' 23:59:59';
            }
            return dates;
        },
        moneyUppercase:function(num) {
        	if (!/^\d*(\.\d*)?$/.test(num)) {
        		return "";
        	}
        	if (num.length > 9) {
        		return "";
        	}
        	var AA = new Array("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖");
        	var BB = new Array("元", "拾", "佰", "仟", "万", "亿", "点", "");
        	var CC = new Array("", "拾", "佰", "仟", "万", "亿", "点", "");
        	var a = ("" + num).replace(/(^0*)/g, "").split("."), k = 0, re = "";

        	for (var i = a[0].length - 1; i >= 0; i--) {
        		switch (k) {
        		case 0:
        			re = BB[7] + re;
        			break;
        		case 4:
        			if (!new RegExp("0{4}\\d{" + (a[0].length - i - 1) + "}$").test(a[0]))
        				re = BB[4] + re;
        			break;
        		case 8:
        			re = BB[5] + re;
        			BB[7] = BB[5];
        			k = 0;
        			break;
        		}
        		if (k % 4 == 2 && a[0].charAt(i + 2) != 0 && a[0].charAt(i + 1) == 0)
        			re = AA[0] + re;
        		if (a[0].charAt(i) != 0) {
        			if (k % 4 == 0) {
        				re = AA[a[0].charAt(i)] + CC[k % 4] + re;
        			} else {
        				re = AA[a[0].charAt(i)] + BB[k % 4] + re;
        			}
        		}
        		k++;
        	}
        	var s = ("" + num).replace(/(^0*)/g, "").split(".");
        	if (("" + s).substring(0, 1) == "0" || ("" + s).substring(0, 1) == ",") {
        		re = AA[0];
        	}
        	if (a.length > 1) { // 加上小数部分(如果有小数部分)
        		// re += re.substring(0,re.length-1);
        		re += BB[6];
        		for (var i = 0; i < a[1].length; i++)
        			re += AA[a[1].charAt(i)];
        		// re += BB[0];
        	}
        	if (re != "") {
        		re += BB[0];
        	}
        	return re;
        },
        money2ThousandsComma: function(value){
        	var num = $.trim(value);
        	if(""==num){
        		return num;
        	}
        	var r = "";
         	if (num.indexOf('￥')>=0){
         		num = num.substr(1);
         		r+="￥";
        	}
         	
         	if (isNaN(num)) {
         		return num; 
         	}
         	
         	var intNum = num; 
        	var pointNum = "";	
        	var idx = num.indexOf('.');
        	if (idx >= 0 ){		   
        		intNum = num.substring(0,idx);
        		pointNum = num.substr(idx);	
        	}
        	var regexStr = /(\d{1,3})(?=(\d{3})+(?:$|\.))/g;
          return r+intNum.replace(regexStr, "$1,")+pointNum;
        },
        money_point_sub : function(element){
            var value = $.trim(element.html()).replace(/\s|\r/g,"");
            var rv = KXin.util.money2ThousandsComma(value);
            var integer = rv.split('.')[0];
            var pointer = rv.split('.')[1];
            var html = integer;
            if( pointer == null || typeof pointer == 'undefined' ) {
            	pointer = '00';
            } else {
            	var length = pointer.length;
            	for( var i = 2 ; i > length ; i-- ) {
                	pointer = pointer + '0';
                }
            }
            	html+="."+pointer;
            element.html(html);
        },
        rates_sub : function(element){
        	var rate = element.text();
        	if( rate != null && rate != '' && typeof rate != 'undefined' && rate.indexOf('%') < 0) {
        		var num = rate*100;
                element.text(num.toFixed(2)+"%");
        	}
        },
        getPreDate : function(d) {
        	var date = new Date();
        	date.setTime(Date.parse(d)-24*60*60*1000);
        	var s = date.getFullYear()+"-" + (date.getMonth()+1) + "-" + date.getDate();
        	return s;
        },
        removeThousandsComma : function(amount_str) {
        	if("undefined" == typeof amount_str || '' == amount_str) {
        		return 0;
        	}
        	var amount = amount_str.replace(',','') - 0;
        	return amount;
        }
    };
    

    /**
     * String：startWith
     * @param s
     * @returns
     */
    String.prototype.startWith=function(s){
      if(s==null||s==""||this.length==0||s.length>this.length)
          return false;
      if(this.substr(0,s.length)==s)
         return true;
      else
         return false;
      return true;
    };

    /**
     * String：endWith
     * @param s
     * @returns
     */
    String.prototype.endWith=function(s){
      if(s==null||s==""||this.length==0||s.length>this.length)
         return false;
      if(this.substring(this.length-s.length)==s)
         return true;
      else
         return false;
      return true;
    };

    /**
     * 根据制定格式转换Date为字符串
     * @param format
     * @returns
     */
    Date.prototype.format = function(format) {
       var date = {
          "M+": this.getMonth() + 1,
          "d+": this.getDate(),
          "h+": this.getHours(),
          "m+": this.getMinutes(),
          "s+": this.getSeconds(),
          "q+": Math.floor((this.getMonth() + 3) / 3),
          "S+": this.getMilliseconds()
       };
       if (/(y+)/i.test(format)) {
          format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
       }
       for (var k in date) {
          if (new RegExp("(" + k + ")").test(format)) {
                 format = format.replace(RegExp.$1, RegExp.$1.length == 1
                        ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
          }
       }
       return format;
    };

    /**
     * ajax全局设置
     */
    $.ajaxSetup({
        dataFilter : function(data, type){
        	try{
        		var d = $.parseJSON(data);
        		var code = d.code;
        		if(undefined==code || null==code || !code.startWith("_GE_")){//_GE_表示返回的是全局错误信息
        			return data;
        		}else{
        			KXin.ui.alert(d.message);
        			return d;
        		}
        	}catch(e){return data;}//异常不截取，交由业务处理
        }
    });
  
    // 页面格式化的一些
    showJs = {
    	    "amount":KXin.util.money_point_sub,
    	    "rates":KXin.util.rates_sub
    	};
    
    initFormat = function(){
    	//页面数据格式加载
    	(function(element){
    	    element.each(function(){
    	        if(undefined!=$(this).attr("format")){
    	            showJs[$(this).attr("format")]($(this));
    	        }
    	    });
    	})($("*[format]"));
    };
}(window);

$(function(){
    // 输入框获取焦点移除错误信息
    $(".condis").find(':text,:password').focus(function(){
    	KXin.ui.removeErrorTip($(this));
    });
    //表格变色效果
    KXin.ui.tableTrColor($("div.data"));
    initFormat();
});
