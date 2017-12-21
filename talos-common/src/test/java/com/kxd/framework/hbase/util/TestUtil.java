/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月9日
 */
public class TestUtil {
    public static void printToFile(String string) {

        OutputStreamWriter buff = null;
        try {
            FileOutputStream fos = new FileOutputStream(new File("testResult.log"), true);
            fos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            buff = new OutputStreamWriter(fos);
            buff.write((string + "\r\n"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buff.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    
    }
}
