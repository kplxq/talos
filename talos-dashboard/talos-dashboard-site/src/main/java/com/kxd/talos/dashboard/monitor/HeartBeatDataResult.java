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
package com.kxd.talos.dashboard.monitor;

import java.util.Date;

import com.kxd.framework.lang.Result;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月26日
 */
public class HeartBeatDataResult extends Result {
	private String host;
	private String processId;
	private Date lastReportTime;
	private long totalRunningTimeInmills;
    private String appId;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Date getLastReportTime() {
		return lastReportTime;
	}

	public void setLastReportTime(Date lastReportTime) {
		this.lastReportTime = lastReportTime;
	}

	public long getTotalRunningTimeInmills() {
		return totalRunningTimeInmills;
	}

	public void setTotalRunningTimeInmills(long totalRunningTimeInmills) {
		this.totalRunningTimeInmills = totalRunningTimeInmills;
	}

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
