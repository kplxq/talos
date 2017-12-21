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
package com.kxd.talos.dashboard.vo;

import com.kxd.talos.dashboard.monitor.HeartBeatDataResult;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月26日
 */
public class HeartBeatDataVo extends HeartBeatDataResult {
    public static final String STATUS_ONLINE  = "online";

    public static final String STATUS_OFFLINE = "offline";

    private String             status;

    private String             lastReportTimeStr;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the lastReportTimeStr
     */
    public String getLastReportTimeStr() {
        return lastReportTimeStr;
    }

    /**
     * @param lastReportTimeStr the lastReportTimeStr to set
     */
    public void setLastReportTimeStr(String lastReportTimeStr) {
        this.lastReportTimeStr = lastReportTimeStr;
    }
}
