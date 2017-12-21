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
package com.kxd.talos.dashboard.common.vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.kxd.framework.page.Page;

/**
 * 请输入功能描述
 * 
 * @author qiandongbo 2014年3月5日
 */
public class PageVo {

    private String requestUrl;

    private int    currentPage = 1;

    /** 每页记录数，默认10条 */
    private int    pageSize    = 10;

    /** 记录总数 */
    private long   count;

    /** 页数 */
    private int    pages;

    /** 当前页的索引 */
    private int    index;

    /**
     * 生成唯一ID,用于一个页面存在多个page域的时候可以区分彼此
     */
    private String pageId;

    /**
     * @return the requestUrl
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * @param requestUrl the requestUrl to set
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     * @return the pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * @param pages the pages to set
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the pageId
     */
    public String getPageId() {
        return pageId;
    }

    /**
     * @param pageId the pageId to set
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public static PageVo createPageVo(HttpServletRequest request, Page page) {
        return createPageVo(request, page, new HashMap<String, Object>());
    }

    public static PageVo createPageVo(HttpServletRequest request, Page page, Map<String, Object> params) {
        PageVo pageVo = new PageVo();
        pageVo.setPageId(UUID.randomUUID().toString().replace("-", ""));
        pageVo.setCurrentPage(page.getCurrentPage());
        pageVo.setPageSize(page.getPageSize());
        pageVo.setRequestUrl(createRequestUrl(request, params));
        pageVo.setCount(page.getCount());
        pageVo.setPages(page.getPages());
        pageVo.setIndex(page.getIndex());
        return pageVo;
    }

    private static String createRequestUrl(HttpServletRequest request, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getRequestURI());
        String queryString = request.getQueryString();
        String result = null;
        if (queryString != null && queryString.trim().length() > 0) {
            sb.append("?").append(queryString);
            result = recoverPageParam(request, sb.toString());
        } else {
            sb.append("?");
            sb.append("currentPage=#currentPage&pageSize=#pageSize");
            result = sb.toString();
        }
        // 如果请求以//开头,那么去掉一个(本地没有//开头的情况，但跟apache交互后出现了该问题，具体原因还需要分析)
        if (result.startsWith("//")) {
            result = result.substring(1);
        }
        return addParams(result, params);
    }

    private static String recoverPageParam(HttpServletRequest request, String requestUrl) {
        String currentPage = request.getParameter("currentPage");
        if (currentPage != null) {
            requestUrl = requestUrl.replaceFirst("currentPage=" + currentPage, "currentPage=#currentPage");
        } else {
            requestUrl += "&currentPage=#currentPage";
        }
        String pageSize = request.getParameter("pageSize");
        if (pageSize != null) {
            requestUrl = requestUrl.replaceFirst("pageSize=" + pageSize, "pageSize=#pageSize");
        } else {
            requestUrl += "&pageSize=#pageSize";
        }
        return requestUrl;
    }

    private static String addParams(String requestUrl, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer(requestUrl);
        Iterator<Entry<String, Object>> entryIterator = params.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Entry<String, Object> entry = entryIterator.next();
            String param = entry.getKey();
            Object paramValue = entry.getValue();
            if (paramValue == null) {
                continue;
            }
            addParam(sb, param, paramValue.toString());
        }
        return sb.toString();
    }

    private static void addParam(StringBuffer sb, String param, String paramValue) {
        int position = sb.indexOf(param);
        if (position < 0) {
            sb.append("&").append(param).append("=").append(paramValue);
        } else {
            Pattern p = Pattern.compile(param + "=" + "(.*?)(&|$)");
            Matcher m = p.matcher(sb);
            while (m.find()) {
                String orgValue = m.group(1);
                int start = position + param.length() + "=".length();
                int end = start + orgValue.length();
                sb.replace(start, end, paramValue);
            }
        }
    }

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append("/a=b&c=d");
        addParam(sb, "a", "z");
        addParam(sb, "c", "b");
        addParam(sb, "d", "w");
        System.out.println(sb);
    }

}
