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

import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月11日
 */
public abstract class AbstractHBasePoolService implements HBasePoolService {
    Logger logger = LoggerFactory.getLogger(AbstractHBasePoolService.class);

    public AbstractHBasePoolService(Properties properties) {
        init(properties);
    }

    private void init(Properties hbaseProperties) {
        Connection connection = null;
        Configuration config = initConfig(hbaseProperties);

        try {
            connection = ConnectionFactory.createConnection(config);
        } catch (Exception e) {
            throw new SkyHBaseException("hbase connection exception", e);
        }

        HBaseUtil.checkNull(connection);

        this.connection = connection;
    }

    /**
     * @param hbaseProperties
     * @return
     */
    private Configuration initConfig(Properties hbaseProperties) {
        if (hbaseProperties != null) {
            Configuration config = HBaseConfiguration.create();
            Set<String> propertyNames = hbaseProperties.stringPropertyNames();
            for (String propertyName : propertyNames) {
                config.set(propertyName, hbaseProperties.getProperty(propertyName));
            }
            return config;
        } else {
            throw new SkyHBaseException("hbase properties cannot be null");
        }
    }

    private Connection connection;

    /**
     * 
     * @param tableName
     * @return
     */
    public Table newTableInterface(String tableName) {
        Table table = null;
        try {
            Admin admin = connection.getAdmin();
            if (!admin.isTableAvailable(TableName.valueOf(tableName))) {
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

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
