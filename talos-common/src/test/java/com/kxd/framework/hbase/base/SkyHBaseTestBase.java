/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.kxd.framework.hbase.core.HBaseTemplate;
import com.kxd.framework.hbase.core.pool.HBaseCommonPoolFactory;
import com.kxd.framework.hbase.core.pool.HBaseLRUPoolFactory;
import com.kxd.framework.hbase.core.pool.HBasePoolService;
import com.kxd.framework.hbase.core.pool.HBaseTablePoolFactory;
import com.kxd.framework.hbase.demo.TestHBaseCommand;
import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.TestUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class SkyHBaseTestBase {

    protected static final String tableName    = "testlh1";

    protected static final String family       = "default";

    private Map<String, Long>     startTimeMap = new HashMap<>();

    protected static Properties initProperties() {
        Properties hbaseProperties = new Properties();
        hbaseProperties.setProperty("hbase.zookeeper.quorum", "hbase1");
        return hbaseProperties;
    }

    private static Configuration initConfig(Properties hbaseProperties) {
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

    /**
     * @return
     */
    protected static Configuration initConfig() {
        return initConfig(initProperties());
    }

    private static HBaseTemplate initTemplate() {
        HBaseTemplate template = new HBaseTemplate();
        return template;
    }

    public static TestHBaseCommand operation(Enum<?> poolType) {
        TestHBaseCommand operation = new TestHBaseCommand();

        HBasePoolService poolService = null;
        if (HBasePoolService.PoolType.LRUPOOL.equals(poolType)) {
            poolService = new HBaseLRUPoolFactory(initProperties());
        } else if (HBasePoolService.PoolType.COMMONPOOL.equals(poolType)) {
            poolService = new HBaseCommonPoolFactory(initProperties());
        } else if (HBasePoolService.PoolType.TABLEPOOL.equals(poolType)) {
            poolService = new HBaseTablePoolFactory(initProperties());
        }

        HBaseTemplate template = initTemplate();
        template.sethBasePoolService(poolService);
        operation.setHBaseTemplate(template);

        return operation;
    }

    public static TestHBaseCommand operation() {
        return operation(HBasePoolService.PoolType.TABLEPOOL);
    }

    public static TestHBaseCommand operationLRUPool() {
        return operation(HBasePoolService.PoolType.LRUPOOL);
    }

    public static TestHBaseCommand operationCommonPool() {
        return operation(HBasePoolService.PoolType.COMMONPOOL);
    }

    public void print(String str) {
        System.out.println(str);
        TestUtil.printToFile(str);
    }

    public void actionStart(String action) {
        long startTime = System.currentTimeMillis();
        startTimeMap.put(action, startTime);
        print(action + " start, currentTime:" + startTime + "ms.");
    }

    public void actionEnd(String action, int totalCount) {
        long endTime = System.currentTimeMillis();
        print(action + " end, currentTime:" + endTime + "ms.");

        Long startTime = startTimeMap.get(action);
        if (null != startTime) {
            long takeTime = endTime - startTime;
            print(action + " takeTime:" + takeTime + "ms.");
            print(action + " everage:" + takeTime / totalCount + "ms.");
        }
    }

    public void actionEnd(String action) {
        actionEnd(action, 1);
    }
}
