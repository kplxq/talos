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
    var codetype=function(typeId){

        var typeData=sessionStorage.getItem(typeId);

        if('undefined'===typeData||"null"===typeData||""===typeData||null===typeData) {
            $.ajax({
                url: "/talos-demo-admin/common/code?typeId=" + typeId,
                type: "POST",
                async: false,
                contentType: "application/json",
                success: function (json) {
                    if (json.result === 'ok') {
                        sessionStorage.setItem(typeId, JSON.stringify(json.data));
                        setCodeType(typeId,json.data);
                    } else {
                        return ""
                    }
                }
            });
        }else{
            setCodeType(typeId,JSON.parse(typeData));
        }

    };



var initcodeType=function(codeStrs){
    var codeArrs=codeStrs.split(",");
    for(var i=0;i<codeArrs.length;i++){
        codetype(codeArrs[i]);
    }
}

var  setCodeType = function(typeId,data){
    var codeSelect=$("#"+typeId);
    if(codeSelect.length>0){
        var options='<option value="" selected="selected">全部</option>';
        for (var sProp in data) {
            options+='<option value="{{CODE}}">{{NAME}}</option>'
                .replace(/\{\{CODE\}\}/g, sProp || "")
                .replace(/\{\{NAME\}\}/g, data[sProp] || "")
        }

        codeSelect.append(options);

    }
}
