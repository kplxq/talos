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

package com.kxd.talos.storage.service.broker;

import java.util.List;
import java.util.Map;

import com.kxd.talos.storage.service.consts.StorageErrorCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;

import com.kxd.framework.lang.AppException;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月10日
 */
public abstract class AbstractMessageBroker implements InitializingBean{
	
	private ConcurrentMessageListenerContainer<String, String> container = null;
	
	private Map<String,Object> properties = null;
	
	private List<String> topics;
	
	private MessageListener<String, String> listener;
	
	private BatchMessageListener<String, String> batchListener;
	
    private int threadNumber ;
    
    /**
     * 是否批量listener;
     */
    private boolean batch = false;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(properties==null || properties.size() ==0){
			throw new AppException(StorageErrorCode.STG_001);
		}
		
		if(topics==null || topics.size()==0){
			throw new AppException(StorageErrorCode.STG_002);
		}
		
		if(listener == null && batchListener == null){
			throw new AppException(StorageErrorCode.STG_003);
		}
		
		if(isBatch()&&batchListener == null){
			throw new AppException(StorageErrorCode.STG_003);
		}
		
		if(threadNumber <=1){
			threadNumber = Runtime.getRuntime().availableProcessors();
		}
		
		DefaultKafkaConsumerFactory<String,String> defaultConsumerFactory = new DefaultKafkaConsumerFactory<String, String>(properties);
		ContainerProperties containerProperties = new ContainerProperties(topics.toArray(new String[topics.size()]));
		container              = new ConcurrentMessageListenerContainer<String, String>(defaultConsumerFactory,containerProperties);
		if(isBatch()){container.setupMessageListener(batchListener);}
		else{container.setupMessageListener(listener);}
		container.setConcurrency(threadNumber);
		container.start();
	}
	
	public void start(){
		doStart();
		container.start();
	}
	
	public void stop(){
		doStop();
		container.stop();
	}
	
	public abstract void doStart();

	public abstract void doStop();

	public Map<String, Object> getProperties() {
		return properties;
	}


	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}


	public List<String> getTopics() {
		return topics;
	}


	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public MessageListener<String, String> getListener() {
		return listener;
	}

	public void setListener(MessageListener<String,String> listener) {
		this.listener = listener;
	}

	public int getThreadNumber() {
		return threadNumber;
	}

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	public ConcurrentMessageListenerContainer<String, String> getContainer() {
		return container;
	}

	public void setContainer(
			ConcurrentMessageListenerContainer<String, String> container) {
		this.container = container;
	}

	public BatchMessageListener<String, String> getBatchListener() {
		return batchListener;
	}

	public void setBatchListener(BatchMessageListener<String, String> batchListener) {
		this.batchListener = batchListener;
	}

	public boolean isBatch() {
		return batch;
	}

	public void setBatch(boolean batch) {
		this.batch = batch;
	}

	
}
