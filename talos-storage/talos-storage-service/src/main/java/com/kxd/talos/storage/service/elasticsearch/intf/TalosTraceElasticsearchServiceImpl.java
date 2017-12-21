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
package com.kxd.talos.storage.service.elasticsearch.intf;

import java.util.ArrayList;
import java.util.List;

import com.kxd.talos.storage.service.elasticsearch.CustomElasticsearchTemplate;
import com.kxd.talos.storage.service.elasticsearch.entity.TalosTraceElasticsearchEntity;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月13日
 */
@Service("elasticsearchService")
public class TalosTraceElasticsearchServiceImpl implements TalosTraceElasticsearchService {

    @Autowired
    private CustomElasticsearchTemplate template;

    private static final String         SCRIPT      = "ctx._source.contents = (ctx._source.contents) ? ctx._source.contents + content  : [content]";

    private static final String         SCRIPT_LANG = "groovy";

    @Value("${es.upsert.retryOnConflict}")
    private int                         retryOnConflict = 3;

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsert(List<TalosTraceElasticsearchEntity> entities) {
        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for (TalosTraceElasticsearchEntity entity : entities) {
            UpdateQuery updateQuery = new UpdateQuery();
            UpdateRequest updateRequest = new UpdateRequest();
            Script script = new Script(SCRIPT, ScriptType.INLINE, SCRIPT_LANG, entity.toScriptMap());
            updateRequest.script(script).upsert(entity.toUpsertMap()).retryOnConflict(retryOnConflict);
            updateQuery.setUpdateRequest(updateRequest);
            updateQuery.setClazz(entity.getClass());
            updateQuery.setId(entity.getTraceid());
            queries.add(updateQuery);
        }

        template.bulkUpsert(queries);

    }

    /**
     * @return the template
     */
    public CustomElasticsearchTemplate getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(CustomElasticsearchTemplate template) {
        this.template = template;
    }

}
