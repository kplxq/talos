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
package com.kxd.talos.storage.service.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.kxd.framework.utils.DateUtil;
import com.kxd.talos.storage.service.data.TalosMessage;
import com.kxd.talos.storage.service.elasticsearch.entity.TalosTraceElasticsearchEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.CollectionUtils;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.lang.AppException;
import com.kxd.framework.utils.HttpUtil;
import com.kxd.talos.storage.service.data.CountResult;
import com.kxd.talos.storage.service.elasticsearch.intf.TalosTraceElasticsearchService;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月13日
 */
public class ElasticsearchStorageMessageWorker extends AbstractMessageWorker {

    private TalosTraceElasticsearchService elasticsearchService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(TalosMessage message) {
        List<ConsumerRecord<String, String>> record = message.getWrappedMessage();
        if (record == null || record.size() == 0) {
            throw new AppException("");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void format(TalosMessage message) {
        message.setWillContinue(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(TalosMessage message) {
        if (message.isWillContinue()) {
            List<Entity> entityList = new ArrayList<>();
            List<ConsumerRecord<String, String>> records = message.getWrappedMessage();
            for (ConsumerRecord<String, String> record : records) {
                if (StringUtils.isEmpty(record.value())) {
                    continue;
                }
                String[] contentArray = record.value().split("\\|");
                String traceId = "";

                boolean isParentNode = false;
                if (record.value().contains("SI:0.1|")) {
                    isParentNode = true;
                }

                for (String content : contentArray) {
                    if (content.startsWith("TD:")) {
                        traceId = content.split("TD:")[1];
                    } else if (content.startsWith("CT:")) {
                        String[] bizzDataArr = content.split("CT:");
                        if (2 != bizzDataArr.length) {
                            continue;
                        }

                        String bizzDatas = bizzDataArr[1];
                        if (StringUtils.isBlank(bizzDatas)) {
                            continue;
                        }
                        for (String bizzData : bizzDatas.split("&@&@&")) {
                            if (StringUtils.isBlank(bizzData)) {
                                continue;
                            }

                            String[] contents = bizzData.split("=");
                            if (contents.length != 2) {
                                continue;
                            }

                            Entity entity = new TalosTraceElasticsearchEntity(traceId, contents[1]);
                            entityList.add(entity);

                        }
                    } else if (content.startsWith("SN")) {
                        if (isParentNode) {
                            // 保存根节点的入口方法
                            Entity pEntity = new TalosTraceElasticsearchEntity(traceId, "method="
                                    + content.split(":")[1]);
                            entityList.add(pEntity);
                        }
                    } else if (content.startsWith("ST")) {
                        // 保存根节点的创建时间
                        if (isParentNode) {
                            Entity pEntity = new TalosTraceElasticsearchEntity(traceId, "startTime="
                                    + content.split(":")[1]);
                            entityList.add(pEntity);
                        }
                    } else if (content.startsWith("HT")) {
                        // 保存根节点的创建时间
                        if (isParentNode) {
                            Entity pEntity = new TalosTraceElasticsearchEntity(traceId, "ip="
                                    + HttpUtil.toIp(content.split(":")[1]));
                            entityList.add(pEntity);
                        }
                    }
                }

            }

            if (CollectionUtils.isEmpty(entityList)) {
                message.setWillContinue(false);
            } else {
                message.setWillContinue(true);
                message.setTransformDataList(entityList);
            }

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountResult deliver(TalosMessage message) {
        CountResult result = new CountResult();
        if (message.isWillContinue()) {

            ConcurrentMap<String, List<TalosTraceElasticsearchEntity>> map = new ConcurrentHashMap<>();
            try {
                for (Entity entity : message.getTransformDataList()) {
                    TalosTraceElasticsearchEntity realEntity = (TalosTraceElasticsearchEntity) entity;
                    if (map.get(realEntity.getTraceid()) != null) {
                        map.get(realEntity.getTraceid()).add(realEntity);
                    } else {
                        List<TalosTraceElasticsearchEntity> entities = new ArrayList<>();
                        entities.add(realEntity);
                        map.putIfAbsent(realEntity.getTraceid(), entities);
                    }
                }

                List<TalosTraceElasticsearchEntity> entities = new ArrayList<>(map.size() * 4 / 3 + 1);
                for (String traceid : map.keySet()) {
                    List<TalosTraceElasticsearchEntity> tmpEntities = map.get(traceid);
                    TalosTraceElasticsearchEntity tmpEntity = new TalosTraceElasticsearchEntity();
                    StringBuffer sb = new StringBuffer();
                    for (TalosTraceElasticsearchEntity entity : tmpEntities) {
                        sb.append(entity.contentToString());
                    }
                    tmpEntity.setTraceid(traceid);
                    tmpEntity.setContents(new String[] { sb.toString() });
                    tmpEntity.setCreateTime(DateUtil.getDate());
                    entities.add(tmpEntity);
                }
                elasticsearchService.upsert(entities);
                result.success(message.getTransformDataList().size());
            } catch (Exception e) {
                e.printStackTrace();
                result.fail("elasticsearch save entities error");
                result.fail(message.getTransformDataList().size());
            }

        }
        return result;
    }

    /**
     * @return the elasticsearchService
     */
    public TalosTraceElasticsearchService getElasticsearchService() {
        return elasticsearchService;
    }

    /**
     * @param elasticsearchService the elasticsearchService to set
     */
    public void setElasticsearchService(TalosTraceElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    public static void main(String[] args) {
        String a = "CT:url=http://www.baidu.com&code=200&";
        System.out.println(a.split("CT:")[1]);
    }
}
