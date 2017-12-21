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
package com.kxd.talos.storage.service.reciever;


import java.util.List;

import com.kxd.framework.monitor.consts.MonitorConstants;
import com.kxd.talos.storage.service.data.TalosMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchMessageListener;

import com.kxd.framework.lang.AppException;
import com.kxd.talos.storage.service.data.CountResult;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月24日
 * @param <K>
 */
public class TalosMultiMessageListener extends AbstractTalosMessageListener implements BatchMessageListener<String, String> {

	@Override
	public void onMessage(List<ConsumerRecord<String, String>> arg0) {
			CountResult result = new CountResult();
			try {
				result = workerPool.attach(TalosMessage.createBatch(arg0));
				monitor.count(MonitorConstants.KAFKA_RECIEVE_SUCCESS_COUNTER, result.getSuccessCount());
				monitor.count(MonitorConstants.KAFKA_RECIEVE_FAILED_COUNTER, result.getFailedCount());
			} catch (AppException appException) {
				result.fail(appException.getErrorCode(),appException.getMessage());
				monitor.count(MonitorConstants.KAFKA_RECIEVE_FAILED_COUNTER, 1);
			} catch (Exception e) {
				monitor.count(MonitorConstants.KAFKA_RECIEVE_FAILED_COUNTER, 1);
				String errorCode = "unknown";
				String message   = e.getMessage();
				result.fail(errorCode, message);
			}
	}



}
