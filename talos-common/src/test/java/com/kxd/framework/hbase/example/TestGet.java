/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.kxd.framework.hbase.base.SkyHBaseTestBase;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class TestGet extends SkyHBaseTestBase {

    public static void main(String[] args) {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hbase");
        Table table = null;
        Connection conn = null;
        try {
            System.out.println("gogogo");
            HBaseAdmin hAdmin = new HBaseAdmin(conf);
            System.out.println(hAdmin.tableExists("trace"));
            System.out.println("end 1 step");
            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf("trace"));
            Get get = new Get(Bytes.toBytes("row1"));
            Result result = table.get(get);
            System.out.println("result:" + result);
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

}
