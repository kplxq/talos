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
package com.kxd.talos.trace.sample.service;

import com.kxd.framework.lang.Result;
import com.kxd.talos.core.trace.TalosCallback;
import com.kxd.talos.core.trace.TalosCallbackTemplate;
import com.kxd.talos.core.trace.TalosTrace;
import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.sample.dto.ParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@TalosTrace
@Service("aopServiceA")
public class AopServiceA {

	@Autowired
	private TalosCallbackTemplate template;

	@Autowired
	AopServiceB aopServiceB;

	public void aop(){
		aopServiceB.step1();
		aopServiceB.step2();
		step3();
	}

	public Result exception() {
		aopServiceB.step1();
		Integer.parseInt("aaa");
		return new Result();
	}

	public void sleep1s() {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {

		}
	}

	private void step3() {
		template.execute(null, new TalosCallback(){
			@Override
			public Object execute(Object request) {
				aopServiceB.step4();
				return null;
			}
		} );
	}

	public void withParam(ParamDto paramDto) {
		Talos.collect("userName", paramDto.getUserName());
		aopServiceB.step1();
	}

}