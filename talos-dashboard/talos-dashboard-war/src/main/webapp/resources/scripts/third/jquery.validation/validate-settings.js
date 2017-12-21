var validateSettings = {
	onkeyup : false,
	focusCleanup : false,
	focusInvalid : false,
	errorPlacement : function(msgObj,element){
		KXin.ui.errorTip(element,msgObj.text());
	},
	success : function(element) {
		KXin.ui.removeErrorTip("#"+$(element).attr("for"));
	},
	// 目前错误信息的移除添加都在errorPlacement和success中完成，所以用这两个方法覆盖为不做操作
	highlight : function(element, errorClass, validClass) {
	},
	unhighlight : function(element, errorClass, validClass) {
	}
}; 