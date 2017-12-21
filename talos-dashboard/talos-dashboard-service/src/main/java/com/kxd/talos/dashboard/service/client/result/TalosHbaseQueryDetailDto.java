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
package com.kxd.talos.dashboard.service.client.result;

import java.util.Map;

import com.kxd.framework.core.entity.Entity;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月14日
 */
public class TalosHbaseQueryDetailDto extends Entity {
    /**
	 * 
	 */
    private static final long                serialVersionUID = 2242731118521477531L;

    private String                           traceId;

    private Map<String, String>              columns;

    private Map<String, TalosSpanDetailDto> spanInfos;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "TalosHbaseQueryDetailDto [traceId=" + traceId + ", columns=" + columns + "]";
    }

    /**
     * @return the spanInfos
     */
    public Map<String, TalosSpanDetailDto> getSpanInfos() {
        return spanInfos;
    }

    /**
     * @param spanInfos the spanInfos to set
     */
    public void setSpanInfos(Map<String, TalosSpanDetailDto> spanInfos) {
        this.spanInfos = spanInfos;
    }

}
