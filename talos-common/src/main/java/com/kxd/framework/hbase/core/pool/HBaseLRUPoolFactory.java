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

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Table;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月11日
 */
public class HBaseLRUPoolFactory extends AbstractHBasePoolService {

    public HBaseLRUPoolFactory(Properties properties) {
        super(properties);
    }

    private static final int                        cacheLength = 1;

    private String[]                                tableNames  = new String[cacheLength];

    private static ConcurrentHashMap<String, Table> tableCache  = new ConcurrentHashMap<>();

    synchronized private void cacheLRU(String tableName, Table table) {

        // 如果该表已缓存，则不再缓存
        for (String tn : tableNames) {
            if (tableName.equals(tn)) {
                return;
            }
        }

        // 即将清除的表名
        String removeTableName = tableNames[cacheLength - 1];

        for (int i = cacheLength - 1; i > 0; i++) {
            tableNames[i] = tableNames[i--];
        }

        tableNames[0] = tableName;

        if (StringUtils.isNotBlank(removeTableName)) {
            Table removeTable = tableCache.remove(removeTableName);

            try {
                removeTable.close();
            } catch (IOException e) {
                logger.error("close table exception, tableName:", removeTable, e);
            }
        }

        tableCache.putIfAbsent(tableName, table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable(String tableName) {
        Table table = tableCache.get(tableName);
        if (table == null) {
            table = newTableInterface(tableName);
            cacheLRU(tableName, table);
        }

        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void colseTable(Table table) {
    }
}
