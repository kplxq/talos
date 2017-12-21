/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.demo;

import java.util.List;

import org.junit.Test;

import com.kxd.framework.hbase.base.SkyHBaseTestBase;
import com.kxd.framework.hbase.core.pool.HBasePoolService;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class TestHBasePerformance extends SkyHBaseTestBase {
    public final static int count = 100;

    @Test
    public void testBatchPut() {
        testBatchPut(HBasePoolService.PoolType.TABLEPOOL);
    }

    @Test
    public void testBatchPutLRUPool() {
        testBatchPut(HBasePoolService.PoolType.LRUPOOL);
    }

    @Test
    public void testBatchPutCommonPool() {
        testBatchPut(HBasePoolService.PoolType.COMMONPOOL);
    }

    public void testBatchPut(Enum<?> en) {
        List<TestHBaseDmo> dmoList = TestHBaseDmoFactory.contructTestDmoList(count);
        String action = "insert one by one use " + en.toString();
        actionStart(action);
        TestHBaseCommand command = operation(en);
        for (TestHBaseDmo dmo : dmoList) {
            command.put(dmo);
        }
        actionEnd(action, count);
    }

}
