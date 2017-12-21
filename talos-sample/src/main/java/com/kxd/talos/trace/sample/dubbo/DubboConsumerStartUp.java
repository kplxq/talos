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

package com.kxd.talos.trace.sample.dubbo;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月7日
 */
public class DubboConsumerStartUp {
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:/spring/dubbo-demo-consumer.xml","classpath:/spring/spring-system-base.xml","classpath:/spring/talos-config-base.xml","classpath:/spring/talos-config-interceptors-for-dubbo.xml");
		context.start();
		
		DubboService dubboService = context.getBean(DubboService.class);
		String lhldyf = dubboService.echoService("lhldyf");
		System.out.println(lhldyf);
	}
}
