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
package com.kxd.framework.hbase.exception;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public class SkyHBaseException extends RuntimeException {


    /**
     * 
     */
    private static final long serialVersionUID = 7506303800894114428L;

    public SkyHBaseException(String message) {
        super(message);
    }

    public SkyHBaseException(String message, Throwable t) {
        super(message, t);
    }
}
