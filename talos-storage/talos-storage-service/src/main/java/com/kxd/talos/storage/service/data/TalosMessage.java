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

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.kxd.framework.core.entity.Entity;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月11日
 */
public class TalosMessage {
	private List<ConsumerRecord<String, String>> wrappedMessage;
	private boolean willContinue;
	private List<Entity> transformDataList;
	

	private TalosMessage(ConsumerRecord<String, String> message) {
		List<ConsumerRecord<String, String>> listMessage = new ArrayList<ConsumerRecord<String, String>>(1);
		listMessage.add(message);
		this.wrappedMessage = listMessage;
	}
	
	private TalosMessage(List<ConsumerRecord<String, String>> messageList) {
		this.wrappedMessage = messageList;
	}

	public static TalosMessage create(ConsumerRecord<String, String> message) {
		return new TalosMessage(message);
	}
	
	public static TalosMessage createBatch(List<ConsumerRecord<String, String>> data){
		return new TalosMessage(data);
	}

	public List<ConsumerRecord<String, String>> getWrappedMessage() {
		return wrappedMessage;
	}

	public void setWrappedMessage(List<ConsumerRecord<String, String>> wrappedMessage) {
		this.wrappedMessage = wrappedMessage;
	}

	public boolean isWillContinue() {
		return willContinue;
	}

	public void setWillContinue(boolean willContinue) {
		this.willContinue = willContinue;
	}

	public List<Entity> getTransformDataList() {
		return transformDataList;
	}

	public void setTransformDataList(List<Entity> transformDataList) {
		this.transformDataList = transformDataList;
	}

}
