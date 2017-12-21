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
package com.kxd.talos.storage.service.elasticsearch;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.util.Assert;

import com.kxd.framework.lang.AppException;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月25日
 */
public class CustomElasticsearchTemplate extends ElasticsearchTemplate {

    Logger      logger    = LoggerFactory.getLogger(CustomElasticsearchTemplate.class);

    private int retryTime = 0;

    /**
     * @param client
     */
    public CustomElasticsearchTemplate(Client client) {
        this(client, 0);
    }

    public CustomElasticsearchTemplate(Client client, int retryTime) {
        super(client);
        this.retryTime = retryTime;
    }

    public void bulkUpsert(List<UpdateQuery> queries) {
        bulkUpsert(queries, 0);
    }

    public void bulkUpsert(List<UpdateQuery> queries, int retryTimes) {
        BulkRequestBuilder bulkRequest = getClient().prepareBulk();
        for (UpdateQuery query : queries) {
            bulkRequest.add(prepareUpdate(query));
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            Map<String, String> failedDocuments = new HashMap<String, String>();
            List<UpdateQuery> newQueries = new ArrayList<>();
            for (BulkItemResponse item : bulkResponse.getItems()) {
                if (item.isFailed()) {
                    failedDocuments.put(item.getId(), item.getFailureMessage());
                    newQueries.add(wrapAndGetQuery(queries, item.getId()));
                }

            }

            if (logger.isDebugEnabled()) {
                logger.debug("es upsert occur exception.");
                logger.debug("all doc:");
                for (UpdateQuery query : queries) {
                    logger.debug(query.getId() + "---" + query.getUpdateRequest().upsertRequest().sourceAsMap());
                }

                logger.debug("success id:");
                for (BulkItemResponse item : bulkResponse.getItems()) {
                    if (!item.isFailed()) {
                        logger.debug(item.getId());
                    }
                }

                logger.debug("fail info:");
                logger.debug("Bulk indexing has failures. Use ElasticsearchException.getFailedDocuments() for detailed messages ["
                        + failedDocuments + "]");
            }

            if (retryTimes < getRetryTime()) {
                // 在重试次数内，再做相同参数的upsert.
                bulkUpsert(newQueries, ++retryTimes);
            } else {
                throw new ElasticsearchException(
                        "Bulk indexing has failures. Use ElasticsearchException.getFailedDocuments() for detailed messages ["
                                + failedDocuments + "]", failedDocuments);
            }

        }
    }

    private UpdateRequestBuilder prepareUpdate(UpdateQuery query) {
        String indexName = isNotBlank(query.getIndexName()) ? query.getIndexName() : getPersistentEntityFor(
                query.getClazz()).getIndexName();
        String type = isNotBlank(query.getType()) ? query.getType() : getPersistentEntityFor(query.getClazz())
                .getIndexType();
        Assert.notNull(indexName, "No index defined for Query");
        Assert.notNull(type, "No type define for Query");
        Assert.notNull(query.getId(), "No Id define for Query");
        Assert.notNull(query.getUpdateRequest(), "No IndexRequest define for Query");
        UpdateRequestBuilder updateRequestBuilder = getClient().prepareUpdate(indexName, type, query.getId());
        updateRequestBuilder.setRouting(query.getUpdateRequest().routing());

        if (query.getUpdateRequest().script() == null) {
            // doc
            if (query.DoUpsert()) {
                updateRequestBuilder.setDocAsUpsert(true).setDoc(query.getUpdateRequest().doc());
            } else {
                updateRequestBuilder.setDoc(query.getUpdateRequest().doc());
            }
        } else {
            // or script
            updateRequestBuilder.setScript(query.getUpdateRequest().script());

            // add the upsert request
            updateRequestBuilder.setUpsert(query.getUpdateRequest().upsertRequest());
        }

        return updateRequestBuilder;
    }

    UpdateQuery wrapAndGetQuery(List<UpdateQuery> queries, String traceId) {
        for (UpdateQuery query : queries) {
            if (query.getId().equals(traceId)) {
                return query;
            }
        }
        throw new AppException("");
    }

    /**
     * @return the retryTime
     */
    public int getRetryTime() {
        return retryTime;
    }

    /**
     * @param retryTime the retryTime to set
     */
    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

}
