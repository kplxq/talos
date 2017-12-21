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
package com.kxd.talos.storage.monitor.heartbeat;

import com.kxd.talos.storage.monitor.collector.ICollector;
import com.kxd.framework.monitor.consts.MonitorConstants;
import com.kxd.talos.storage.monitor.utils.InetAddressUtilities;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import static com.kxd.talos.storage.monitor.utils.JdkRuntimeUtilities.getProcessId;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月11日
 */
public class DefaultHeartBeater implements IHeartBeater{
	
	private static  String host = null;
	
	private static  String processId ;
	
	private volatile boolean open;
	
	private int period = 0;
	
	private Object openLock = new Object();
	
	private static Long lastTimepoint = System.currentTimeMillis();
	
	static{
		try {processId = getProcessId();} catch (Exception e) {processId="0";}
		try {InetAddress inetAddress      = InetAddressUtilities.getLocalHostLANAddress(); host=inetAddress.getHostAddress();} catch (UnknownHostException e) {host = "unknown";}
	}

	@Override
	public void heartbeat(ICollector collector) {
		if(open){
			Long current = System.currentTimeMillis();
			//心跳数据格式  TALOS-MONITOR-HB;数据上报时间;ip地址;进程号;累计运行时间(自应用最近启动,单位:毫秒)
			StringBuffer heartbeat = new StringBuffer(MonitorConstants.MONITOR_HB_KPI_CODE);
			heartbeat.append(";");
			heartbeat.append(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
			heartbeat.append(";").append(host);
			heartbeat.append(";").append(processId);
			heartbeat.append(";").append(current-lastTimepoint);
			try {collector.collect(heartbeat.toString());} catch (Exception e) {e.printStackTrace();}
		}
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
	public void stop() {
		synchronized(openLock){
			open = false;
		}
	}

	@Override
	public int period() {
		return this.period;
	}
}
