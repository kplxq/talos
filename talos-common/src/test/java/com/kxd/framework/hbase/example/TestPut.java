/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.kxd.framework.hbase.base.SkyHBaseTestBase;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public class TestPut extends SkyHBaseTestBase {

    static long count = 100L;

    public static void main(String[] args) {
        putOneByOneNotCloseTable();
        // putOneByOne();
        // putByBatch();
    }

    /**
     * 不关闭table，一个一个put
     */
    public static void putOneByOneNotCloseTable() {

        Table table = null;
        Connection conn = null;
        try {
            conn = ConnectionFactory.createConnection(initConfig());
            table = conn.getTable(TableName.valueOf(tableName));
            long start = System.currentTimeMillis();
            long count = 1000L;
            for (long i = 0; i < count; i++) {
                Put put = new Put(Bytes.toBytes("row1" + i));
                put.addColumn(family.getBytes(), "id".getBytes(), "ID002251".getBytes());
                table.put(put);
            }

            long end = System.currentTimeMillis();

            System.out.println("finish");
            System.out.println("time:" + (end - start) + "ms");
            System.out.println("time average:" + (end - start) / count + "ms");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 一次put操作，批量put
     */
    public static void putByBatch() {

        Table table = null;
        Connection conn = null;
        try {
            conn = ConnectionFactory.createConnection(initConfig());
            table = conn.getTable(TableName.valueOf(tableName));
            long start = System.currentTimeMillis();
            long count = 1000L;
            List<Put> puts = new ArrayList<Put>();
            for (long i = 0; i < count; i++) {
                Put put = new Put(Bytes.toBytes("row1" + i));
                put.addColumn(family.getBytes(), "id".getBytes(), "ID002251".getBytes());
                puts.add(put);
            }

            table.put(puts);

            long end = System.currentTimeMillis();

            System.out.println("finish");
            System.out.println("time:" + (end - start) + "ms");
            System.out.println("time average:" + (end - start) / count + "ms");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 一个一个put，每次都关闭table
     */
    public static void putOneByOne() {

        Table table = null;
        Connection conn = null;
        long start = System.currentTimeMillis();
        for (long i = 0; i < count; i++) {
            try {
                conn = ConnectionFactory.createConnection(initConfig());
                table = conn.getTable(TableName.valueOf(tableName));

                Put put = new Put(Bytes.toBytes("row1" + i));
                put.addColumn("default".getBytes(), "id".getBytes(), "ID002251".getBytes());
                table.put(put);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (table != null) {
                    try {
                        table.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        long end = System.currentTimeMillis();

        System.out.println("finish");
        System.out.println("time:" + (end - start) + "ms");
        System.out.println("time average:" + (end - start) / count + "ms");

    }
}
