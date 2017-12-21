/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.demo;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.kxd.framework.hbase.base.SkyHBaseTestBase;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class TestHBaseOperation extends SkyHBaseTestBase {

    @Test
    public void testBatch() {
        List<TestHBaseDmo> dmoList = TestHBaseDmoFactory.contructTestDmoList();

        // 删除不存在的列
        operation().deleteList(dmoList);

        // 插入
        operation().putList(dmoList);

        // 查询
        Assert.assertEquals(dmoList.size(), operation().getList(dmoList).size());

        // 删除存在的列
        operation().deleteList(dmoList);

        Assert.assertEquals(0, operation().getList(dmoList).size());
    }

    @Test
    public void testOneRow() {
        TestHBaseDmo dmo = TestHBaseDmoFactory.contructTestDmo();
        operationCommonPool().delete(dmo);
        operationCommonPool().put(dmo);
        Assert.assertTrue(dmo.equals(operation().get(dmo)));
        operationCommonPool().delete(dmo);
        Assert.assertEquals(null, operation().get(dmo));
    }

    @Test
    public void testPutList() {
        operation().putList(TestHBaseDmoFactory.contructTestDmoList());
    }

    @Test
    public void testGetList() {
        operation().getList(TestHBaseDmoFactory.contructTestDmoList());
    }

    @Test
    public void testDeleteList() {
        operation().deleteList(TestHBaseDmoFactory.contructTestDmoList());
    }

    @Test
    public void testPut() {
        operation().put(TestHBaseDmoFactory.contructTestDmo());
    }

    @Test
    public void testGet() {
        TestHBaseDmo dmo = operation().get(TestHBaseDmoFactory.contructTestDmo());
        System.out.println(dmo == null ? null : dmo.toString());
    }

    @Test
    public void testDelete() {
        operation().delete(TestHBaseDmoFactory.contructTestDmo());
    }

}
