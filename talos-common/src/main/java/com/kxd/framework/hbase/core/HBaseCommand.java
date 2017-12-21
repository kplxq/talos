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
package com.kxd.framework.hbase.core;

import java.util.List;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
public interface HBaseCommand {
    /**
     * 
     * @param t
     */
    public <T extends HBaseEntity> void put(T t);

    /**
     * 
     * @param t
     */
    public <T extends HBaseEntity> void putList(List<T> ts);

    /**
     * 
     * @param t
     */
    public <T extends HBaseEntity> void delete(T t);

    /**
     * 
     * @param t
     */
    public <T extends HBaseEntity> void deleteList(List<T> ts);

    /**
     * 
     * @param rowKey
     * @return
     */
    public <T extends HBaseEntity> T get(T t);

    /**
     * 
     * @param t
     * @return
     */
    public <T extends HBaseEntity> List<T> getList(List<T> ts);

    /**
     * 
     * @param claz
     * @param startKey
     * @param endKey
     * @return
     */
    public <T extends HBaseEntity> List<T> getListByScan(Class<T> claz, String startKey, String endKey);
}
