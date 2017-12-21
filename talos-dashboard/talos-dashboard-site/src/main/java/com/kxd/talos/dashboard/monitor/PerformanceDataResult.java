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

import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.kxd.framework.lang.Result;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月26日
 */
public class PerformanceDataResult extends Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = -550338041909171727L;

	private String host;
	private String processId;
	private Date lastReportTime;
	private long tps60;
	private long tpsTotal;
	private String appId;
	private SortedSet<PerformanceHisRecord> hisRecord = new TreeSet<PerformanceHisRecord>(new Comparator<PerformanceHisRecord>() {
        @Override
        public int compare(PerformanceHisRecord o1, PerformanceHisRecord o2) {
            return o1.getLastReportTime().compareTo(o2.getLastReportTime());
        }
    });

	public static class PerformanceHisRecord {
		private Date lastReportTime;
		private long tps60;
		private long tpsTotal;
		
		public PerformanceHisRecord(Date lastReportTime,long tps60,long tpsTotal){
			this.lastReportTime = lastReportTime;
			this.tps60 = tps60;
			this.tpsTotal = tpsTotal;
		}

		public Date getLastReportTime() {
			return lastReportTime;
		}

		public void setLastReportTime(Date lastReportTime) {
			this.lastReportTime = lastReportTime;
		}

		public long getTps60() {
			return tps60;
		}

		public void setTps60(long tps60) {
			this.tps60 = tps60;
		}

		public long getTpsTotal() {
			return tpsTotal;
		}

		public void setTpsTotal(long tpsTotal) {
			this.tpsTotal = tpsTotal;
		}
	}

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

	public long getTps60() {
		return tps60;
	}

	public void setTps60(long tps60) {
		this.tps60 = tps60;
	}

	public long getTpsTotal() {
		return tpsTotal;
	}

	public void setTpsTotal(long tpsTotal) {
		this.tpsTotal = tpsTotal;
	}

	public SortedSet<PerformanceHisRecord> getHisRecord() {
		return hisRecord;
	}

	public void setHisRecord(SortedSet<PerformanceHisRecord> hisRecord) {
		this.hisRecord = hisRecord;
	}
	
	public void addHisRecord(PerformanceHisRecord record){
		if(hisRecord.size()>=1440){
			hisRecord.remove(hisRecord.last());
		}
		hisRecord.add(record);
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
