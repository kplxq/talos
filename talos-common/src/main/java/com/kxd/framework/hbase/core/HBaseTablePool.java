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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月10日
 */
@Deprecated
public class HBaseTablePool {

    /**
     * use custom LRU pool
     */
    private boolean LRUEnable = false;

    private Enum    poolType  = PoolType.HTABLEPOOL;

    Logger          logger    = LoggerFactory.getLogger(HBaseTablePool.class);

    public HBaseTablePool(Connection connection) {
        this.connection = connection;
    }

    public HBaseTablePool(Connection connection, boolean useCustomPool) {
        this.connection = connection;
        this.LRUEnable = useCustomPool;
    }

    public HBaseTablePool(Connection connection, Enum<PoolType> poolType) {
        this.connection = connection;
        this.poolType = poolType;
    }

    private Connection                              connection;

    private static final int                        cacheLength = 1;

    private String[]                                tableNames  = new String[cacheLength];

    private static ConcurrentHashMap<String, Table> tableCache  = new ConcurrentHashMap<>();

    public Table getTable(String tableName) {

        if (PoolType.LRUPOOL.equals(poolType)) {
            Table table = tableCache.get(tableName);
            if (table == null) {
                table = newTableInterface(tableName);
                cacheLRU(tableName, table);
            }

            return table;
        } else {
            return newTableInterface(tableName);
        }

    }

    /**
     * 
     * @param tableName
     * @return
     */
    public Table newTableInterface(String tableName) {
        Table table = null;
        try {
            Admin admin = connection.getAdmin();
            if (admin.isTableAvailable(TableName.valueOf(tableName))) {
                logger.warn("hbase table is unavailable, tableName:", tableName);
                // throw new
                // SkyHBaseException("hbase table is unavailable, tableName:"
                // + tableName);
            }
            table = connection.getTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            throw new SkyHBaseException("get hbase table obj exception, tableName:" + tableName, e);
        }

        HBaseUtil.checkNull(table);
        return table;
    }

    public void cacheLRU(String tableName, Table table) {

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

    public enum PoolType {

        HTABLEPOOL(0), LRUPOOL(1), COMMONPOOL(2);

        int type;

        PoolType(int type) {
            this.type = type;
        }

        int type() {
            return type;
        }

    }
}
