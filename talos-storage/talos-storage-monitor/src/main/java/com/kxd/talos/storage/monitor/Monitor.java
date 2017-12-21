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
package com.kxd.talos.storage.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kxd.talos.storage.monitor.collector.ICollector;
import com.kxd.talos.storage.monitor.collector.LoggingCollector;
import com.kxd.talos.storage.monitor.counter.ICounter;
import com.kxd.talos.storage.monitor.counter.MemoryCounter;
import com.kxd.talos.storage.monitor.heartbeat.DefaultHeartBeater;
import com.kxd.talos.storage.monitor.heartbeat.IHeartBeater;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月11日
 */
public class Monitor implements IMonitor {
	
	private final ICounter counter = new MemoryCounter();
	
	private final IHeartBeater heartBeater = new DefaultHeartBeater();
	
	private ScheduledExecutorService monitor = null;
	
	private ICollector collector = new LoggingCollector();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void count(String index, int step) {
		counter.count(index, step);
	}

	@Override
	public void start() {
		
		heartBeater.start(30);
		
		counter.start(60);
		
		monitor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
		/**
		 * 调度心跳组件
		 */
		monitor.scheduleAtFixedRate(new Runnable(){

			@Override
			public void run() {
				heartBeater.heartbeat(collector);
			}
			
		}, 5, heartBeater.period(), TimeUnit.SECONDS);
		
		/**
		 * 调度统计器组件
		 */
		monitor.scheduleAtFixedRate(new Runnable(){

			@Override
			public void run() {
				counter.collect(collector);
			}
			
		}, 5, counter.period(), TimeUnit.SECONDS);
		
	}

	@Override
	public void stop() {
		monitor.shutdown();
	}

	public ICollector getCollector() {
		return collector;
	}

	public void setCollector(ICollector collector) {
		this.collector = collector;
	}

}
