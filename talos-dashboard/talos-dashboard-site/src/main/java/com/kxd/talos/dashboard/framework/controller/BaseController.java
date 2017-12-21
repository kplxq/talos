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
package com.kxd.talos.dashboard.framework.controller;

import java.util.HashMap;
import java.util.Map;


import com.kxd.framework.result.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.lang.Result;
import com.kxd.talos.dashboard.common.consts.CommonConst;

/**
 * BaseController
 *
 * @author qiaojs 2014年6月30日
 */
public class BaseController {

    /**
     * 成功的Status Code.
     */
    private static final int RESCODE_OK = 200;


    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected JsonResult toJsonResult(JsonResult jsonResult, Result result, String... params) {
        jsonResult.setSuccess(result.isSuccess());
        if (null == params || params.length < 1) {
            params = result.getArgs();
        }
        jsonResult.setCode(result.getErrorCode());
        return jsonResult;
    }

    protected JsonResult toJsonResult(Result result, String... params) {
        return toJsonResult(new JsonResult(), result, params);
    }

    /**
     * 重定向
     */
    protected String redirect(String redirectUrl) {
        return "redirect:" + redirectUrl;
    }

    /**
     * 重定向
     */
    protected String forword(String forwordUrl) {
        return "forward:" + forwordUrl;
    }

    private String view(String domail, String ftl, String sysPath) {
        return CommonConst.BASE_VIEW_PATH.concat(sysPath).concat("/").concat(domail).concat("/")
                .concat(ftl);
    }


    /**
     * 描述：组装json格式返回结果
     * @param isOk
     * @param resCode
     * @param errorMsg
     * @param obj
     * @return
     */
    protected Map<String, Object> createJson(boolean isOk, int resCode, String errorMsg, Object obj) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("result", isOk ? "ok" : "fail");
        jsonMap.put("rescode", resCode);
        jsonMap.put("msg", errorMsg);
        jsonMap.put("data", obj);
        return jsonMap;
    }

    /**
     * 获取成功结果
     * @param obj
     * @return
     */
    protected Map<String, Object> getSuccessResult(Object obj) {
        return createJson(true, RESCODE_OK, "操作成功", obj);
    }
}