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
package com.kxd.talos.dashboard.service.core.dmo;

import com.kxd.framework.hbase.anno.HBaseColumn;
import com.kxd.framework.hbase.anno.HBaseTable;
import com.kxd.framework.hbase.core.HBaseEntity;

@HBaseTable(name = "trace", defaultFamily = "span")
public class TalosTraceHbaseTableDmo extends HBaseEntity {
    private static final long serialVersionUID = 6275930306045857214L;

    @HBaseColumn(family = "id", qualifier = "id")
    private String            id;

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getRowKey() {
        return id.getBytes();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
