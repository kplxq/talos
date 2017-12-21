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
package com.kxd.framework.hbase.core;

import org.apache.hadoop.hbase.client.Table;

import com.kxd.framework.hbase.core.pool.HBasePoolService;
import com.kxd.framework.hbase.exception.SkyHBaseException;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public class HBaseTemplate {

    private HBasePoolService hBasePoolService;

    public Object execute(String tableName, TableCallback callback) {
        Object result = null;
        Table table = null;
        try {
            table = hBasePoolService.getTable(tableName);
            result = callback.execute(table);
        } catch (Exception e) {
            throw new SkyHBaseException("Exception occured in execute method:", e);
        } finally {
            hBasePoolService.colseTable(table);
        }
        return result;
    }

    /**
     * @return the hBasePoolService
     */
    public HBasePoolService gethBasePoolService() {
        return hBasePoolService;
    }

    /**
     * @param hBasePoolService the hBasePoolService to set
     */
    public void sethBasePoolService(HBasePoolService hBasePoolService) {
        this.hBasePoolService = hBasePoolService;
    }

}
