/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.demo;

import java.util.List;

import com.kxd.framework.hbase.core.AbstractHBaseCommand;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public class TestHBaseCommand extends AbstractHBaseCommand {
    public void testPutList(List<TestHBaseDmo> dmoList) {
        super.putList(dmoList);
    }

    public List<TestHBaseDmo> testGetList(List<TestHBaseDmo> dmoList) {
        return super.getList(dmoList);
    }

    public void testDeleteList(List<TestHBaseDmo> dmoList) {
        super.deleteList(dmoList);
    }

    public void testPut(TestHBaseDmo dmo) {
        super.put(dmo);
    }

    public void testGet(TestHBaseDmo dmo) {
        super.get(dmo);
    }

    public void testDelete(TestHBaseDmo dmo) {
        super.delete(dmo);
    }
}
