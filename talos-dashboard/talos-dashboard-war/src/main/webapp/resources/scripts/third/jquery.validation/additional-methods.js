/*!
 * jQuery Validation Plugin v1.13.0
 *
 * http://jqueryvalidation.org/
 *
 * Copyright (c) 2014 Jörn Zaefferer
 * Released under the MIT license
 */
(function( factory ) {
	if ( typeof define === "function" && define.amd ) {
		define( ["jquery", "./jquery.validate"], factory );
	} else {
		factory( jQuery );
	}
}(function( $ ) {

(function() {

	function stripHtml(value) {
		// remove html tags and space chars
		return value.replace(/<.[^<>]*?>/g, " ").replace(/&nbsp;|&#160;/gi, " ")
		// remove punctuation
		.replace(/[.(),;:!?%#$'\"_+=\/\-“”’]*/g, "");
	}

	$.validator.addMethod("maxWords", function(value, element, params) {
		return this.optional(element) || stripHtml(value).match(/\b\w+\b/g).length <= params;
	}, $.validator.format("Please enter {0} words or less."));

	$.validator.addMethod("minWords", function(value, element, params) {
		return this.optional(element) || stripHtml(value).match(/\b\w+\b/g).length >= params;
	}, $.validator.format("Please enter at least {0} words."));

	$.validator.addMethod("rangeWords", function(value, element, params) {
		var valueStripped = stripHtml(value),
			regex = /\b\w+\b/g;
		return this.optional(element) || valueStripped.match(regex).length >= params[0] && valueStripped.match(regex).length <= params[1];
	}, $.validator.format("Please enter between {0} and {1} words."));

}());

// 长度等于
$.validator.addMethod("lengthEqual", function(value, element, params) {
	return this.optional( element ) || $.trim(value).length == params[0];
}, "{1}的长度应为{0}位");

//长度等于
$.validator.addMethod("decimal2dot", function(value, element, params) {
	var result = this.optional( element ) || (!isNaN(value) && value > 0);
	if(result) {
		if(value.indexOf(".")>=0 && value.length-value.indexOf(".")>3) {
			result = false;
		}
	}
	return result;
}, "{1}只能输入最多两位小数的有效金额");
//可包含0的校验
$.validator.addMethod("decimal2dotIncludeZero", function(value, element, params) {
	var result = this.optional( element ) || (!isNaN(value) && value >= 0);
	if(result) {
		if(value.indexOf(".")>=0 && value.length-value.indexOf(".")>3) {
			result = false;
		}
	}
	return result;
}, "{1}只能输入最多两位小数的有效金额");

// 非法字符校验BEGIN
var regArray = new Array();
regArray[0]="script";
regArray[1]="eval";
regArray[2]="alert";
regArray[3]="exec";
regArray[4]="drop";
regArray[5]="select";
regArray[6]="alter";
regArray[7]="insert";
regArray[8]="update";
regArray[9]="delete";
regArray[10]="exists";
//regArray[11]="or";
//regArray[12]="xor";
regArray[13]="execute";
regArray[14]="xp_cmdshell";
regArray[15]="declare";
regArray[16]="sp_oacreate";
regArray[17]="where";
regArray[18]="wscript.shell";
regArray[19]="xp_regwrite";
regArray[20]="'";
regArray[21]="<";
regArray[22]=">";
regArray[23]="$";
regArray[24]="&";
regArray[25]="\\";
regArray[26]="\"";
regArray[27]="~";
regArray[28]="`";
regArray[29]="*";
regArray[30]="%0d%0a";

function strFilter(value){
	if(null==value || ""==value){
		return "success";
	}
	var val = value.toLowerCase();
	for(var i = 0;i<regArray.length;i++){
		if(-1!=val.indexOf(regArray[i])){
			return i == 21 ? "&lt;" : i== 22 ?"&gt;" :  i == 24 ? "&amp;" : regArray[i];
		}
	}
	if(/\bor\b/.test(val)){
		return "or";
	}
	if(/\bxor\b/.test(val)){
		return "xor";
	}
	return "success";
}

//非法字符验证 
jQuery.validator.addMethod("reg_check", function(value, element,param) {
	if (value=="") return true;
	var rst = strFilter(value);
	if(rst=="success"){
		return this.optional(element) || true; 
	}
	else
	{
		param[0] = rst;
		return this.optional(element) || false; 
	}
}, "不能含有{0}");

// 非法字符校验END

//计算字节长度 
$.validator.addMethod("rangeCharLength", function(value, element,param) {
	
	var realLength = 0;  
    for (var i = 0; i < value.length; i++)   
    {  
        charCode = value.charCodeAt(i);  
        if (charCode >= 0 && charCode <= 128)   
        realLength += 1;  
        else   
        realLength += 3;  
    }
    
	if(param[0] <= realLength && param[1] >= realLength){
		return this.optional(element) || true; 
	}
	else
	{
		return false;
	}
}, "输入长度范围为{0}~{1}");



}));