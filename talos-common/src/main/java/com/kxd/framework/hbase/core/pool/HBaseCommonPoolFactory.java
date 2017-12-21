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

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;

import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月11日
 */
public class HBaseCommonPoolFactory extends AbstractHBasePoolService {

    private GenericKeyedObjectPoolConfig config;

    /**
     * @param properties
     */
    public HBaseCommonPoolFactory(Properties properties) {
        super(properties);

        if (null == config) {
            config = new GenericKeyedObjectPoolConfig();
            config.setTestOnBorrow(true);
        }

        ConnectionFactory factory = new ConnectionFactory();
        pool = new GenericKeyedObjectPool<String, Table>(factory, config);
    }

    private GenericKeyedObjectPool<String, Table> pool;

    class ConnectionFactory extends BaseKeyedPooledObjectFactory<String, Table> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Table create(String key) throws Exception {
            return newTableInterface(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PooledObject<Table> wrap(Table value) {
            return new DefaultPooledObject<Table>(value);
        }

        @Override
        public boolean validateObject(String key, PooledObject<Table> p) {
            Table table = p.getObject();
            boolean valid = true;
            try {
                // 2017-11-27 测试发现这段代码执行会block
                // table.exists(new Get(HBaseUtil.stringToBytes("test")));
            } catch (Exception e) {
                logger.info("table interface invalid, will create a new table.");
                valid = false;
            }
            return valid;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable(String tableName) {
        Table table = null;
        try {
            table = pool.borrowObject(tableName);
        } catch (Exception e) {
            throw new SkyHBaseException("get Table exception", e);
        }
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void colseTable(Table table) {
        if (null != table) {
            pool.returnObject(table.getName().getNameAsString(), table);
        } else {
            logger.error("table is null when try to close table.");
        }

    }

    /**
     * @return the config
     */
    public GenericKeyedObjectPoolConfig getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(GenericKeyedObjectPoolConfig config) {
        this.config = config;
    }
}
