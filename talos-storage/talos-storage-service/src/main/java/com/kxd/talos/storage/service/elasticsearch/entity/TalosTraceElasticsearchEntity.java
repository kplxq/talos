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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.kxd.framework.core.entity.Entity;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月13日
 */
@Document(indexName = "skyeye", type = "trace")
public class TalosTraceElasticsearchEntity extends Entity {

    /**
     * 
     */
    private static final long serialVersionUID = -5013547565454151884L;

    @Id
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String            traceid;

    private String[]          contents;

    private Date createTime;

    public TalosTraceElasticsearchEntity() {

    }

    public TalosTraceElasticsearchEntity(String traceid, String content) {
        this.traceid = traceid;
        this.contents = new String[] { content };
    }

    /**
     * @return the contents
     */
    public String[] getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents(String[] contents) {
        this.contents = contents;
    }

    /**
     * @return the traceid
     */
    public String getTraceid() {
        return traceid;
    }

    /**
     * @param traceid the traceid to set
     */
    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public Map<String, Object> toUpsertMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("contents", contents);
        map.put("createTime", createTime);
        return map;
    }

    public Map<String, Object> toScriptMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("content", contentToString());
        map.put("createTime", createTime);
        return map;
    }

    public String contentToString() {
        if (contents != null || contents.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (String c : contents) {
                sb.append(c).append(" ");
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
