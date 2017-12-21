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
package com.kxd.talos.core.trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.kxd.framework.lang.AppException;
import com.kxd.framework.lang.ErrorCode;
import com.kxd.framework.lang.Result;
import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.collector.SpanCollector;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.tracer.LocalTracer;
import com.kxd.talos.trace.core.utils.Constants;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 服务跟踪拦截器
 * 
 * @author X-MAN 2010-7-23
 */
public class TalosTraceInterceptor implements MethodInterceptor,InitializingBean {

    /** logger */
    private static final Logger logger     = LoggerFactory.getLogger(TalosTraceInterceptor.class);

    /** 调用时间阈值 */
    private long                threshold  = 1000L;

    /** 需要过滤的参数 */
    private String[]            filterArgs;

    /** 过滤的后缀符 */
    private String[]            filterSuffix;
    
    private Map<String, String> loggerConfigs = new LinkedHashMap<String, String>();

    private Map<String, Logger> cachedLoggers = new LinkedHashMap<String, Logger>();

    private SpanCollector spanCollector ;
    
    private Talos talos ;
    
    private LocalTracer tracer;
    
    private String serviceName;
    
    /**
     * tracer采样率,默认为100%采集
     */
    private float sampleRate = 1.0f;

    /** 实现一个FastJSON过滤器 */
    private ValueFilter         nameFilter = new ValueFilter() {
                                                @Override
                                               public Object process(Object source, String name, Object value) {
                                                   Object result = value;

                                                   if (filterArgs == null || value == null) {
                                                       return result;
                                                   }

                                                   name = name.toUpperCase();
                                                   try {
                                                       for (String filterArg : filterSuffix) {
                                                           if (name.endsWith(filterArg)
                                                                   && ClassUtils.isAssignableValue(String.class, value)) {
                                                               return ((String) value).length() + "L";
                                                           }
                                                       }

                                                       for (String filterArg : filterArgs) {
                                                           if (name.equals(filterArg)
                                                                   && ClassUtils.isAssignableValue(String.class, value)) {
                                                               result = ((String) value).length() + "L";
                                                               break;
                                                           }
                                                       }
                                                   } catch (Throwable t) {
                                                          logger.error("FastJSON过滤异常", t);
                                                   }

                                                   return result;
                                               }
                                           };

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Object result = null;
        Logger logger = this.getLogger(target.getClass());

        String methodName = target.getClass().getSimpleName() + "." + method.getName();
        Span traceLog = tracer.startSpan(Constants.SPAN_NAME_LOCAL, methodName);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ME:{}|Begin|ARGS:{}", methodName,
                    JSON.toJSONString(invocation.getArguments(), this.nameFilter));
        }

        try {
            result = invocation.proceed();

            // 返回结果异常判断
            if (ClassUtils.isAssignable(Result.class, method.getReturnType())) {
                Result newResult = (Result) result;
                if (!newResult.isSuccess()) {
                    traceLog.setExType("A");
                    traceLog.setErrorCode(newResult.getErrorCode());
                }
            }
        } catch (AppException appEx) {
            traceLog.setExType("A");
            traceLog.setErrorCode(appEx.getErrorCode());
            traceLog.setErrorMessage(appEx.getMessage());
            result = this.handleException(method, appEx, appEx.getErrorCode(), appEx.getMessage(), appEx.getArgs());
        } catch (Throwable t) {
            traceLog.setExType("T");
            traceLog.setErrorCode(ErrorCode.ERROR_SERVICE_INTERCEPTOR_INVOKE);
            traceLog.setErrorMessage(getExceptionMsg(t));
            logger.error("服务拦截器调用" + methodName + "异常！", t);
            result = this.handleException(method, t, ErrorCode.ERROR_SERVICE_INTERCEPTOR_INVOKE, "", null);
        } finally {
        	tracer.finishSpan(traceLog);

            if (logger.isDebugEnabled()) {
                logger.debug("ME:{}|End|Result:{}", methodName, JSON.toJSONString(result, this.nameFilter));
            }

        }

        return result;
    }

    /**
     * 处理异常
     * @param method 方法
     * @param t 异常
     * @param errorCode 错误码
     * @param message 错误信息
     * @return 调用结果
     */
    private Object handleException(Method method, Throwable t, String errorCode, String message, String[] args) {
        Object result = null;

        if (ClassUtils.isAssignable(Result.class, method.getReturnType())) {
            result = BeanUtils.instantiateClass(method.getReturnType());
            ((Result) result).fail(errorCode, message, args);

            return result;
        } else {
            throw new AppException(errorCode, message);
        }
    }

    private Logger getLogger(Class targetClass) {
        if (loggerConfigs.isEmpty()) {
            return logger;
        }
        String targetClassPackage = targetClass.getPackage().getName();
        if (cachedLoggers.containsKey(targetClassPackage)) {
            return cachedLoggers.get(targetClassPackage);
        }
        int position = -1;
        String selectedLoggerName = null;

        Iterator<Entry<String, String>> iterator = this.loggerConfigs.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            String packageName = entry.getKey();
            String loggerName = entry.getValue();
            if (!targetClassPackage.startsWith(packageName)) {
                continue;
            }
            int currentPosition = packageName.length();
            if (position < currentPosition) {
                position = currentPosition;
                selectedLoggerName = loggerName;
            }
        }
        Logger selectedLogger = null;
        if (StringUtils.isEmpty(selectedLoggerName)) {
            selectedLogger = logger;
        } else {
            try {
                selectedLogger = LoggerFactory.getLogger(selectedLoggerName);
            } catch (Exception e) {
                logger.warn(e.toString());
                selectedLogger = logger;
            }
        }
        cachedLoggers.put(targetClassPackage, selectedLogger);
        return selectedLogger;
    }

    /**
     * 设置调用时间阈值
     * @param threshold 调用时间阈值
     */
    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    /**
     * @param filterArgs the filterArgs to set
     */
    public void setFilterArgs(String[] filterArgs) {
        List<String> filterArgList = new ArrayList<String>();
        List<String> filterSuffixList = new ArrayList<String>();
        for (String filterArg : filterArgs) {
            if (!StringUtils.isEmpty(filterArg)) {
                if (filterArg.indexOf("*") == 0) {
                    filterSuffixList.add(filterArg.toUpperCase().substring(1));
                } else {
                    filterArgList.add(filterArg.toUpperCase());
                }
            }
        }
        this.filterArgs = filterArgList.toArray(new String[0]);
        this.filterSuffix = filterSuffixList.toArray(new String[0]);
    }

    /**
     * 
     * @param t
     * @return
     */
    private String getExceptionMsg(Throwable t){
        if(null == t) {
            return "";
        }
        //打印的最大行数
        int maxLine = 50;
        StringBuffer msgBuffer = new StringBuffer();
        StackTraceElement[]  ele = t.getStackTrace();
        int printLineNum = ele.length>maxLine?maxLine:ele.length;
        msgBuffer.append(t.toString());
        for(int i = 0 ; i < printLineNum; i++) {
            //talos收集异常的的时候是按行收集的，所以不能\r\n换行，用@wrap#替换
            msgBuffer.append("@wrap#  at "+ele[i].toString());
        }
        return msgBuffer.toString();
    }

	public void setSpanCollector(SpanCollector spanCollector) {
		this.spanCollector = spanCollector;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		tracer  = talos.localTracer();
	}

	public Talos getTalos() {
		return talos;
	}

	public void setTalos(Talos talos) {
		this.talos = talos;
	}

    public void setLoggerConfigs(Map<String, String> loggerConfigs) {
        this.loggerConfigs = loggerConfigs;
    }

}