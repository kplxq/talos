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
package com.kxd.talos.trace.sample.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年9月21日
 */
public class TestInheritedThread {
	public static void withExecutor(){
		ExecutorService threadPool = Executors.newFixedThreadPool(3);
		threadPool.submit(new CallableTask());
	}
	
	public static void withNewThread(){
		Thread newThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println(" i am here "+RuntimeContext.get("here")+" thread name is "+Thread.currentThread().getName());
			}
			
		});
		
		newThread.start();
	}
	
	public static void main(String[] args){
		RuntimeContext.put("name", "Value");
		withExecutor();
	}

}
