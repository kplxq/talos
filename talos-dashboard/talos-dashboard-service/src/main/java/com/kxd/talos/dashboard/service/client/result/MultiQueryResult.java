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
package com.kxd.talos.dashboard.service.client.result;

import java.util.List;
import java.util.Map;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.lang.Result;
import com.kxd.framework.page.Page;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年9月29日
 */
public class MultiQueryResult<T extends Entity> extends Result {
    /**
     * 
     */
    private static final long  serialVersionUID = 2799834131388634273L;

    public List<T>             realResultList;

    public Map<String, String> parameters;

    private Page               page;

    public List<T> getRealResultList() {
        return realResultList;
    }

    public void setRealResultList(List<T> realResultList) {
        this.realResultList = realResultList;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Page page) {
        this.page = page;
    }
}
