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

import org.apache.hadoop.hbase.client.Table;

import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月11日
 */
public class HBaseTablePoolFactory extends AbstractHBasePoolService implements HBasePoolService {

    public HBaseTablePoolFactory(Properties properties) {
        super(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable(String tableName) {
        return newTableInterface(tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void colseTable(Table table) {
        HBaseUtil.checkNull(table);
        try {
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
