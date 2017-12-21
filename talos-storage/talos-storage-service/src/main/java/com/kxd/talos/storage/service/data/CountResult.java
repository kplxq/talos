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
package com.kxd.talos.storage.service.data;

import java.util.concurrent.atomic.AtomicInteger;

import com.kxd.framework.lang.Result;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月24日
 */
public class CountResult extends Result {
	private AtomicInteger successCount = new AtomicInteger();
	private AtomicInteger failedCount = new AtomicInteger();
	public void success(int scale){
		successCount.getAndAdd(scale);
	}
	
	public void fail(int scale){
		failedCount.getAndAdd(scale);
	}
	
	public int getSuccessCount(){
		return successCount.get();
	}
	
	public int getFailedCount(){
		return failedCount.get();
	}

}
