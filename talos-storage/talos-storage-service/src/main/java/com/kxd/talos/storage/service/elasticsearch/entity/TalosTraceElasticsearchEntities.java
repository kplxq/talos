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
package com.kxd.talos.storage.service.elasticsearch.entity;

import java.util.List;

import com.kxd.framework.core.entity.Entity;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月13日
 */
public class TalosTraceElasticsearchEntities extends Entity {
    /**
     * 
     */
    private static final long                    serialVersionUID = -1876814903487015108L;

    /**
     * 
     */
    private List<TalosTraceElasticsearchEntity> entities;

    /**
     * @param entityList
     */
    public TalosTraceElasticsearchEntities(List<TalosTraceElasticsearchEntity> entityList) {
        this.entities = entityList;
    }

    /**
     * @return the entities
     */
    public List<TalosTraceElasticsearchEntity> getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List<TalosTraceElasticsearchEntity> entities) {
        this.entities = entities;
    }
}
