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

package com.kxd.talos.trace.sample.controller;

import com.kxd.talos.trace.interceptor.server.http.HttpClientTemplate;
import com.kxd.talos.trace.sample.dto.ParamDto;
import com.kxd.talos.trace.sample.dubbo.DubboService;
import com.kxd.talos.trace.sample.service.AopServiceA;
import com.kxd.talos.trace.sample.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {


	@Autowired
	private ThreadService threadService;

	@Autowired
	private AopServiceA aopServiceA;

	@Autowired
	private DubboService dubboService;
	
	@Autowired
	private HttpClientTemplate httpClientTemplate;

	@RequestMapping("/aop")
	@ResponseBody public String aop() {
		aopServiceA.aop();
		return "aop finish";
	}

	@RequestMapping("/http")
	@ResponseBody public String http() {
		String result = httpClientTemplate.executePost("http://127.0.0.1:9908/talos-sample/http/service" ,"http-client-param");
		return result;
	}
	
	@RequestMapping("/dubbo")
	@ResponseBody public String dubbo() {
		try {
			dubboService.echoService("lhldyf");
			return "dubbo finish";
		} catch (Exception e) {
			return "请确认您已启动sample工程中的dubbo provider服务";
		}

	}

	@RequestMapping("/mq")
	@ResponseBody public String mq() {
		return "mq todo!";
	}

	@RequestMapping("/exception")
	@ResponseBody public String exception() {
		aopServiceA.exception();
		return "exception OK!";
	}

	@RequestMapping("/thread")
	@ResponseBody public String thread() {
		threadService.call2thread();
		return "thread finish";
	}

	@RequestMapping("/sleep1s")
	@ResponseBody public String sleep1s() {
		aopServiceA.sleep1s();
		return "finish";
	}

	@RequestMapping("/with/param")
	@ResponseBody public String param(ParamDto paramDto) {
		aopServiceA.withParam(paramDto);
		return "with param finish";
	}

	@RequestMapping("/http/service")
	@ResponseBody public String httpService(@RequestBody String param) {
		return "http service finish with your param:" + param;
	}
	
}
