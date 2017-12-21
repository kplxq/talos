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
 * @author lhldyf 2016年10月20日
 */
public class TalosSpanDetailDto extends Entity {

    /**
     * 
     */
    private static final long   serialVersionUID = 1576133060995381635L;

    private String              traceId;

    private String              spanId;

    private String              parentSpanId;

    private String              method;

    private String              host;

    private String              type;

    private String              exType;

    private String              errorCode;

    private String              startTime;

    private String              duration;

    private String              threadName;

    private String              appName;

    private String              processId;

    private String              errorMessage;
    
    private Map<String, String> content;

    /**
     * @return the traceId
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * @param traceId the traceId to set
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * @return the spanId
     */
    public String getSpanId() {
        return spanId;
    }

    /**
     * @param spanId the spanId to set
     */
    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    /**
     * @return the parentSpanId
     */
    public String getParentSpanId() {
        return parentSpanId;
    }

    /**
     * @param parentSpanId the parentSpanId to set
     */
    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the exType
     */
    public String getExType() {
        return exType;
    }

    /**
     * @param exType the exType to set
     */
    public void setExType(String exType) {
        this.exType = exType;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * @return the threadName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @param threadName the threadName to set
     */
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the processId
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * @return the content
     */
    public Map<String, String> getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Map<String, String> content) {
        this.content = content;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
