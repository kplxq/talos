/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.demo;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class DemoMain {

    public static void main(String[] args) {
        // TestHBaseOperation testOpt = new TestHBaseOperation();
        // testOpt.testPut();

        TestHBasePerformance testPf = new TestHBasePerformance();
        for (int i = 0; i++ < 10;) {
            testPf.testBatchPutLRUPool();
            // testPf.testBatchPut();
            // testPf.testBatchPutCommonPool();
        }

    }
}
