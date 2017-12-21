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
package com.kxd.talos.storage.service.worker;

import com.kxd.framework.lang.AppException;
import com.kxd.framework.lang.Result;
import com.kxd.talos.storage.service.data.CountResult;
import com.kxd.talos.storage.service.data.TalosMessage;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月10日
 */
public abstract class AbstractMessageWorker implements IMessageWorker{

	@Override
	public final CountResult execute(TalosMessage message) {
		CountResult finalResult = new CountResult();
		try {
			if(validate(message).isSuccess()){
				filter(message);
				format(message);
				transform(message);
				finalResult = deliver(message);
			}
		}catch (AppException appException){
			finalResult.fail(appException.getErrorCode(), appException.getMessage());
		}catch (Exception e) {
			String errorCode = "unknown";
			String errMsg    = e.getMessage();
			finalResult.fail(errorCode,errMsg);
		}finally{
			
		}
		return finalResult;
	}
	
	
	public Result validate(TalosMessage message){
		Result result = new Result();
		if(message == null || message.getWrappedMessage() == null){
			result.fail("message is null");
		}
		return result;
	}
	/**
	 * 过滤掉非合法数据
	 * @param message
	 * @param context
	 */
	public abstract void filter(TalosMessage message);
	
	/**
	 * 对数据进行规整,如裁剪、增补等
	 * @param message
	 * @param context
	 */
	public abstract void format(TalosMessage message);
	
	/**
	 * 将数据转换为目标存储器所能接受的数据类型
	 * @param message
	 * @param context
	 */
	public abstract void transform(TalosMessage message);
	
	/**
	 * 将数据发送至目标存储器
	 * @param message
	 * @param context
	 */
	public abstract CountResult deliver(TalosMessage message);
  
}
