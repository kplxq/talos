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

import java.util.Map;

import com.kxd.framework.lang.Result;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月26日
 */
public interface IMonitorDataHolder {

    /***
     * 接收数据,含心跳及性能数据
     * @param content
     * @return
     */
    public Result recieve(String content);

    /**
     * 查询单条心跳数据
     * @param ip
     * @param appId
     * @return
     */
    public HeartBeatDataResult queryHBDetail(String ip, String appId);

    /**
     * 查询所有机器+进程号的活跃度数据
     * @return
     */
    public Map<String, HeartBeatDataResult> queryHBList();

    /**
     * 查询单机器+应用ID的性能数据(含轨迹)
     * @param ip
     * @param appId
     * @return
     */
    public PerformanceDataResult queryPFDetail(String ip, String appId);

    /**
     * 查询所有机器+进程号的性能数据
     * @return
     */
    public Map<String, PerformanceDataResult> queryPFList();
}
