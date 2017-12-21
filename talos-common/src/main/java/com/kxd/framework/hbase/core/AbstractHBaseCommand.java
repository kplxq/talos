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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.CollectionUtils;

import com.kxd.framework.hbase.core.config.ColumnInfo;
import com.kxd.framework.hbase.core.config.HBaseTableConfig;
import com.kxd.framework.hbase.core.config.TypeInfo;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public abstract class AbstractHBaseCommand implements HBaseCommand {

    private HBaseTemplate hBaseTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends HBaseEntity> void put(T t) {
        List<T> ts = new ArrayList<>();
        ts.add(t);
        this.putList(ts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends HBaseEntity> void putList(final List<T> ts) {

        // check empty
        HBaseUtil.checkListEmpty(ts);

        final TypeInfo typeInfo = HBaseTableConfig.findTypeInfo(ts.get(0).getClass());
        String tableName = typeInfo.getTableName();

        this.hBaseTemplate.execute(tableName, new TableCallback() {

            @Override
            public Object execute(Table table) throws Exception {
                List<Put> puts = new ArrayList<>();

                for (T t : ts) {
                    Put put = new Put(t.getRowKey());

                    // 注解字段
                    for (ColumnInfo columnInfo : typeInfo.getColumnInfos()) {
                        // get value
                        byte[] value = HBaseUtil.toBytes(columnInfo.field.get(t), columnInfo.field.getType());
                        put.addColumn(columnInfo.familyBytes, columnInfo.qualifierBytes, value);
                    }

                    // 扩展字段
                    Map<String, String> row = t.getRow();
                    if (null != row && row.size() > 0) {
                        for (String qualifier : row.keySet()) {
                            put.addColumn(typeInfo.getDefaultFamilyBytes(), Bytes.toBytes(qualifier),
                                    Bytes.toBytes(row.get(qualifier)));
                        }
                    }
                    puts.add(put);
                }

                table.put(puts);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends HBaseEntity> void delete(T t) {
        List<T> ts = new ArrayList<>();
        ts.add(t);
        this.deleteList(ts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends HBaseEntity> void deleteList(final List<T> ts) {

        // check empty
        HBaseUtil.checkListEmpty(ts);

        final TypeInfo typeInfo = HBaseTableConfig.findTypeInfo(ts.get(0).getClass());
        String tableName = typeInfo.getTableName();

        this.hBaseTemplate.execute(tableName, new TableCallback() {

            @Override
            public Object execute(Table table) throws Exception {
                List<Delete> deletes = new ArrayList<>();

                for (T t : ts) {
                    Delete delete = new Delete(t.getRowKey());
                    deletes.add(delete);
                }
                table.delete(deletes);
                return null;
            }

        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends HBaseEntity> T get(final T t) {
        List<T> ts = new ArrayList<>();
        ts.add(t);
        List<T> tResults = this.getList(ts);
        if (CollectionUtils.isEmpty(tResults)) {
            return null;
        } else {
            return tResults.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends HBaseEntity> List<T> getList(final List<T> ts) {
        HBaseUtil.checkListEmpty(ts);

        final TypeInfo typeInfo = HBaseTableConfig.findTypeInfo(ts.get(0).getClass());

        Object result = this.hBaseTemplate.execute(typeInfo.getTableName(), new TableCallback() {

            @Override
            public Object execute(Table table) throws Exception {
                List<Get> gets = new ArrayList<>();
                for (T t : ts) {
                    Get get = new Get(t.getRowKey());
                    gets.add(get);
                }

                Result[] results = table.get(gets);
                List<T> tResults = new ArrayList<>();

                for (Result r : results) {
                    NavigableMap<byte[], NavigableMap<byte[], byte[]>> rMap = r.getNoVersionMap();

                    if (CollectionUtils.isEmpty(rMap)) {
                        continue;
                    }

                    T tResult = (T) typeInfo.getType().newInstance();

                    // 注解字段
                    for (ColumnInfo columnInfo : typeInfo.getColumnInfos()) {
                        NavigableMap<byte[], byte[]> qMap = rMap.get(columnInfo.familyBytes);
                        if (qMap != null) {
                            byte[] value = qMap.get(columnInfo.qualifierBytes);
                            if (null != value) {
                                columnInfo.field.set(tResult, HBaseUtil.bytesToObj(value, columnInfo.field.getType()));
                            }

                            // 避免在扩展的map中再出现这个列簇
                            qMap.remove(columnInfo.qualifierBytes);
                        }
                    }

                    // 扩展字段
                    NavigableMap<byte[], byte[]> qMap = rMap.get(typeInfo.getDefaultFamilyBytes());
                    Map<String, String> row = new HashMap<>();
                    for (Entry<byte[], byte[]> oneRow : qMap.entrySet()) {
                        row.put(HBaseUtil.bytesToString(oneRow.getKey()), HBaseUtil.bytesToString(oneRow.getValue()));
                    }

                    tResult.setRow(row);
                    tResults.add(tResult);
                }

                return tResults;
            }

        });

        return (List<T>) result;
    }

    @SuppressWarnings("unchecked")
    public <T extends HBaseEntity> List<T> getListByScan(Class<T> claz, final String startKey, final String endKey) {

        HBaseUtil.checkNull(startKey);
        HBaseUtil.checkNull(endKey);

        final TypeInfo typeInfo = HBaseTableConfig.findTypeInfo(claz);

        Object result = this.hBaseTemplate.execute(typeInfo.getTableName(), new TableCallback() {

            @Override
            public Object execute(Table table) throws Exception {

                Scan scan = new Scan();
                scan.setStartRow(startKey.getBytes());
                scan.setStopRow(endKey.getBytes());

                ResultScanner resultScanner = table.getScanner(scan);
                Iterator<Result> it = resultScanner.iterator();

                List<T> tResults = new ArrayList<>();

                while (it.hasNext()) {
                    Result r = it.next();
                    NavigableMap<byte[], NavigableMap<byte[], byte[]>> rMap = r.getNoVersionMap();

                    if (CollectionUtils.isEmpty(rMap)) {
                        continue;
                    }

                    T tResult = (T) typeInfo.getType().newInstance();

                    // 注解字段
                    for (ColumnInfo columnInfo : typeInfo.getColumnInfos()) {
                        NavigableMap<byte[], byte[]> qMap = rMap.get(columnInfo.familyBytes);
                        if (qMap != null) {
                            byte[] value = qMap.get(columnInfo.qualifierBytes);
                            if (null != value) {
                                columnInfo.field.set(tResult, HBaseUtil.bytesToObj(value, columnInfo.field.getType()));
                            }

                            // 避免在扩展的map中再出现这个列簇
                            qMap.remove(columnInfo.qualifierBytes);
                        }
                    }

                    // 扩展字段
                    NavigableMap<byte[], byte[]> qMap = rMap.get(typeInfo.getDefaultFamilyBytes());
                    Map<String, String> row = new HashMap<>();
                    for (Entry<byte[], byte[]> oneRow : qMap.entrySet()) {
                        row.put(HBaseUtil.bytesToString(oneRow.getKey()), HBaseUtil.bytesToString(oneRow.getValue()));
                    }

                    tResult.setRow(row);
                    tResults.add(tResult);
                }

                return tResults;
            }

        });

        return (List<T>) result;
    }

    /**
     * @return the hBaseTemplate
     */
    public HBaseTemplate getHBaseTemplate() {
        return hBaseTemplate;
    }

    /**
     * @param hBaseTemplate the hBaseTemplate to set
     */
    public void setHBaseTemplate(HBaseTemplate hBaseTemplate) {
        this.hBaseTemplate = hBaseTemplate;
    }
}
