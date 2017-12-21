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
package com.kxd.framework.hbase.core.pool;

import org.apache.hadoop.hbase.client.Table;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月11日
 */
public interface HBasePoolService {
    /**
     * get table interface.
     * @param tableName
     * @return
     */
    public Table getTable(String tableName);

    /**
     * colse the table.
     * @param table
     */
    public void colseTable(Table table);

    public enum PoolType {
        LRUPOOL, TABLEPOOL, COMMONPOOL
    }
}
