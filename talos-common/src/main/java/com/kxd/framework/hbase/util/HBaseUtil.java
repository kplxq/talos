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
package com.kxd.framework.hbase.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.CollectionUtils;

import com.kxd.framework.hbase.exception.SkyHBaseException;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public class HBaseUtil {
    public static void checkNull(Object obj) {
        if (obj == null) {
            throw new SkyHBaseException("object is null");
        }
    }

    public static <T> void checkListEmpty(List<T> ts) {
        if (CollectionUtils.isEmpty(ts)) {
            throw new SkyHBaseException("list is null");
        }
    }

    /**
     * @param family
     */
    public static void checkEmptyString(String str) {
        if (StringUtils.isEmpty(str)) {
            throw new SkyHBaseException("string is null");
        }

    }

    public static byte[] toBytes(Object val, Class<?> type) {
        if (null == val) {
            return null;
        }
        if (type.equals(String.class)) {
            return Bytes.toBytes((String) val);
        } else if (type.equals(Date.class)) {
            long time = ((Date) val).getTime();
            return Bytes.toBytes(time);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Bytes.toBytes((Boolean) val);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Bytes.toBytes((Long) val);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Bytes.toBytes((Double) val);
        } else if (type.equals(Integer.class) || type.equals(Integer.class)) {
            return Bytes.toBytes((Integer) val);
        } else {
            throw new SkyHBaseException("unsupport type unable to use HBaseUtil.toBytes()");
        }
    }

    public static Object bytesToObj(byte[] bytes, Class<?> type) {
        if (null == bytes) {
            return null;
        }

        if (type.equals(String.class)) {
            return Bytes.toString(bytes);
        } else if (type.equals(Date.class)) {
            long time = Bytes.toLong(bytes);
            return new Date(time);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Bytes.toBoolean(bytes);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Bytes.toLong(bytes);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Bytes.toDouble(bytes);
        } else if (type.equals(Integer.class) || type.equals(Integer.class)) {
            return Bytes.toInt(bytes);
        } else {
            throw new SkyHBaseException("unsupport type unable to use HBaseUtil.bytesToObj()");
        }

    }

    public static String bytesToString(byte[] bytes) {
        return (String) bytesToObj(bytes, String.class);
    }

    public static byte[] stringToBytes(String val) {
        return toBytes(val, String.class);
    }
}
