/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class TestHBaseDmoFactory {

    static Random randomGenerator = new Random();

    public static List<TestHBaseDmo> contructTestDmoList() {
        List<TestHBaseDmo> dmoList = new ArrayList<>();
        dmoList.add(contructTestDmo(1));
        dmoList.add(contructTestDmo(2));
        return dmoList;
    }

    public static List<TestHBaseDmo> contructTestDmoList(int length) {
        List<TestHBaseDmo> dmoList = new ArrayList<>(length * 4 / 3);
        for (int i = 0; i < length; i++) {
            dmoList.add(contructTestDmo(i));
        }
        return dmoList;
    }

    public static TestHBaseDmo contructTestDmo() {
        return contructTestDmo(1);
    }

    private static TestHBaseDmo contructTestDmo(int i) {
        TestHBaseDmo dmo = new TestHBaseDmo();
        Long longValue = randomGenerator.nextLong();
        dmo.setId("id-00" + longValue);
        dmo.setName("name-00" + i);
        dmo.setBooleanValue(true);
        dmo.setDateValue(new Date());
        dmo.setIntValue(i);
        dmo.setLongValue(longValue);
        dmo.setRow(contructRowMap(i));
        return dmo;
    }

    private static Map<String, String> contructRowMap(int i) {
        Map<String, String> row = new HashMap<>();
        row.put("qualifier-00" + i, "qlf-value-00" + i);
        row.put("qualifier-01" + i, "qlf-value-01" + i);
        return row;
    }
}
