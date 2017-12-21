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
package com.kxd.framework.lang;

/**
 * 错误码
 * 
 * @author X-MAN 2011-11-8
 */
public class ErrorCode {


    /** 成功 */
    public static final String SUCCESS             = "success";

    /** 失败 */
    public static final String FAIL                = "fail";

    /** 系统异常 */
    @Description("系统内部异常")
    public static final String ERROR_SYSTEM        = "error.system";

    /** 远程调用异常 */
    @Description("系统内部异常")
    public static final String ERROR_REMOTE_INVOKE = "error.remote.invoke";

    @Description("网络超时异常")
    public static final String ERROR_NET_TIMEOUT   = "error.net.timeout";


    /** 查询模板不存在 */
    public static final String ERROR_QUERY_TEMPLATE_NOT_EXIST          = "error.query.template.not.exist";

    /** 生成查询语句出错 */
    public static final String ERROR_BUILD_QUERY_STRING                = "error.build.query.string";

    /** 无法创建Hibernate模板 */
    public static final String ERROR_CANT_CREATE_HIBERNATE_TEMPLATE    = "error.cant.create.hibernate.template";

    /** 服务模板执行出现异常 */
    public static final String ERROR_SERVICE_TEMPLATE_EXECUTE          = "error.service.template.execute";

    /** 服务拦截器调用异常 */
    @Description("系统内部异常")
    public static final String ERROR_SERVICE_INTERCEPTOR_INVOKE        = "error.service.interceptor.invoke";

    /** 服务拦截器事务执行异常 */
    public static final String ERROR_SERVICE_INTERCEPTOR_TRANS_EXECUTE = "error.service.interceptor.trans.execute";

    /** 事件主题不存在 */
    public static final String ERROR_EVENT_TOPIC_NOT_EXIST             = "error.event.topic.not.exist";

    /** 一个接一个业务处理模版执行异常 */
    public static final String ERROR_ONE_BY_ONE_TEMPLATE_EXECUTE       = "error.one.by.one.tempatale.execute";

    /** 参数空异常 */
    public static final String ERROR_PARAM_NULL                        = "error.param.null";

    /** 业务正在处理中 */
    @Description("系统内部异常")
    public static final String ERROR_BIZ_PROCESSING                    = "error.biz.processing";

    
    @Description("系统内部异常")
    public static final String ERROR_MONGO_CREATE                    = "error.mongo.create";
}
