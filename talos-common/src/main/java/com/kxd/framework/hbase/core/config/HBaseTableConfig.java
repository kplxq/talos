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
package com.kxd.framework.hbase.core.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.kxd.framework.hbase.anno.HBaseTable;
import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public class HBaseTableConfig {

    /**
     * type info mapping of class.
     */
    private static ConcurrentMap<Class<?>, TypeInfo> mappingTypes = new ConcurrentHashMap<Class<?>, TypeInfo>();

    public static TypeInfo findTypeInfo(Class<?> type) {
        HBaseUtil.checkNull(type);
        TypeInfo result = mappingTypes.get(type);

        if (result != null) {
            return result;
        }

        HBaseTable hbaseTable = type.getAnnotation(HBaseTable.class);
        if (hbaseTable != null) {
            result = TypeInfo.parse(type);
            addTypeInfo(result);
            return result;
        }

        throw new SkyHBaseException("can't find type info. type=" + type);
    }

    private static void addTypeInfo(TypeInfo typeInfo) {
        mappingTypes.putIfAbsent(typeInfo.getType(), typeInfo);
    }

}
