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
package com.kxd.framework.monitor.consts;

public  class MonitorConstants{

	/**
	 * 心跳监控数据KPI编码
	 */
	public static final String MONITOR_HB_KPI_CODE = "TALOS-MONITOR-HB";

	/**
	 * 计数监控KPI指标
	 */
	public static final String MONITOR_COUNTER_KPI_CODE = "TALOS-MONITOR-COUNTER";

	/**
	 * 卡夫卡总共接受成功数目
	 */
	public static final String KAFKA_RECIEVE_SUCCESS_COUNTER = "kafka.recieve.success";

	/**
	 * 卡夫卡总共接受失败数目
	 */
	public static final String KAFKA_RECIEVE_FAILED_COUNTER  = "kafka.recieve.failed";
}