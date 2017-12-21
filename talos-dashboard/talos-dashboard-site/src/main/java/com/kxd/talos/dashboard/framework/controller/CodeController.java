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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kxd.talos.dashboard.framework.consts.CodeInfoConstants;

/**
 * Created by Ad
 * ministrator on 2015/8/24 0024.
 */
@Controller
public class CodeController extends BaseController {

    /**
     *
     * @param typeId
     * @return
     */
    @RequestMapping(value = "/common/code")
    @ResponseBody
    public Map<String,Object> commonData(String typeId,HttpServletRequest request) {


        Map<String, String> typeMap = CodeInfoConstants.map.get(typeId);
        if (null == typeMap || typeMap.isEmpty()) {
            return null;
        }else {
            return getSuccessResult(typeMap);
        }
    }
}
