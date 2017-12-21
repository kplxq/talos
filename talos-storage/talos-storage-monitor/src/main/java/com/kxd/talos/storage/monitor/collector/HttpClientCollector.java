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
package com.kxd.talos.storage.monitor.collector;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.utils.HttpClient;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月26日
 */
public class HttpClientCollector implements ICollector {
	
	private String reportUrl;
	private String appId;
	private static final String DEFAUTL_CHARSET = "UTF-8";
	private static int   DEFAULT_TIMEOUT = 6000;
	private static final HashMap<String,String> HEADERS = new HashMap<String,String>();
	private static Logger logger = LoggerFactory.getLogger(HttpClientCollector.class);
	static{
		HEADERS.put("Content-Type", "text/plain");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collect(String content) {
		try {
			System.out.println(" content is "+content+";"+getAppId()+";");
			HttpClient.sendByPostOfString(reportUrl, content+";"+getAppId()+";", HEADERS,DEFAULT_TIMEOUT, DEFAUTL_CHARSET);
		} catch (Exception e) {
            logger.error("error happens in HttpClient collector.collect",e);
		}
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

    /**
     * @return the appId
     */
    public String getAppId() {
        return StringUtils.isEmpty(appId)?"":appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

}
