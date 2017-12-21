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
package com.kxd.talos.dashboard.service.core.service.intf.elasticsearch;

import java.util.List;

import com.kxd.talos.dashboard.service.core.dmo.TalosTraceElasticsearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月13日
 */
public interface TalosTraceRepository extends ElasticsearchRepository<TalosTraceElasticsearchEntity, String> {

    /**
     * @param content
     * @return
     */
    List<TalosTraceElasticsearchEntity> findByContents(String contents);

    /**
     * @param content
     * @param page
     * @return
     */
    Page<TalosTraceElasticsearchEntity> findByContents(String contents, Pageable page);

}
