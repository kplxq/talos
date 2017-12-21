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
package com.kxd.talos.storage.monitor.counter;

import com.kxd.framework.utils.DateUtil;
import com.kxd.framework.utils.NumberUtil;
import com.kxd.talos.storage.monitor.collector.ICollector;
import com.kxd.framework.monitor.consts.MonitorConstants;
import com.kxd.talos.storage.monitor.utils.InetAddressUtilities;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.kxd.talos.storage.monitor.utils.JdkRuntimeUtilities.getProcessId;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月11日
 */
public class MemoryCounter implements ICounter {

	public ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<String, AtomicLong>();
	
	private volatile boolean open;
	
	private static  String host = null;
	
	private static  String processId ;

	private int period = 0;

	private Object openLock = new Object();
	
	private long lastSuccess,lastFailed,startTime = System.currentTimeMillis();

	/**
	 * {@inheritDoc}
	 */
	
	static{
		try {processId = getProcessId();} catch (Exception e) {processId="0";}
		try {InetAddress inetAddress      = InetAddressUtilities.getLocalHostLANAddress(); host=inetAddress.getHostAddress();} catch (UnknownHostException e) {host = "unknown";}
	}
	
	@Override
	public void count(String idx, long step) {
		AtomicLong currentCount = counters.get(idx);
		if (currentCount == null) {
			AtomicLong newCounter = new AtomicLong();
			AtomicLong temp = counters.putIfAbsent(idx, newCounter);
			if (temp == null) {
				currentCount = newCounter;
			}
		}
		currentCount.getAndAdd(step);
	}

	@Override
	public void collect(ICollector collector) {
		/**
		 * 监控数据格式 TALOS-MONITOR-COUNTER;指标数据,以,分割;数据上报时间;过去period(秒)处理成功率; 过去period(秒)处理失败率;总成功率;总失败率;采样周期(单位秒);主机IP;进程号
		 */
		long currentSuccess = 0,currentFailed = 0;
		StringBuffer sb = new StringBuffer(MonitorConstants.MONITOR_COUNTER_KPI_CODE).append(";");
		for (Entry<String, AtomicLong> counter : counters.entrySet()) {
			sb.append(counter.getKey()).append(":").append(counter.getValue()).append(",");
			if(StringUtils.equals(counter.getKey(),MonitorConstants.KAFKA_RECIEVE_SUCCESS_COUNTER)){
				currentSuccess = counter.getValue().get();
			}
			
			else if(StringUtils.equals(counter.getKey(),MonitorConstants.KAFKA_RECIEVE_FAILED_COUNTER)){
				currentFailed  = counter.getValue().get();
			}
			
			else{}
		}
		sb.append(";").append(DateUtil.format(new Date(), DateUtil.yyyyMMddHHmmss));
		sb.append(";").append(NumberUtil.div(currentSuccess - lastSuccess, period(),1));
		sb.append(";").append(NumberUtil.div(currentFailed - lastFailed, period(),1));
		sb.append(";").append(NumberUtil.div(currentSuccess, (System.currentTimeMillis()-startTime)/1000,1));
		sb.append(";").append(NumberUtil.div(currentFailed,(System.currentTimeMillis()-startTime)/1000,1));
		sb.append(";").append(period());
		sb.append(";").append(host);
		sb.append(";").append(processId);

		lastSuccess = currentSuccess; lastFailed = currentFailed;
		
		try {collector.collect(sb.toString());} catch (Exception e) {e.printStackTrace();}
	}

	@Override
	public void start(int period) {
		synchronized(openLock){
			if(period <=30){period = 30;} 
			this.period = period;
			open = true;
		}
	}

	@Override
	public int period() {
		return this.period;
	}
}
