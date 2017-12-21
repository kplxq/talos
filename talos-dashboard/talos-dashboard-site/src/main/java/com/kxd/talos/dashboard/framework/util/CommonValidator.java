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
package com.kxd.talos.dashboard.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.lang.AppException;
import com.kxd.framework.lang.Result;

/**
 * 请输入功能描述
 * 
 * @author Ranger Chen 2014年7月18日
 */
public class CommonValidator {

    private static Logger logger = LoggerFactory.getLogger(CommonValidator.class);

    /**
     * 验证一般参数
     * 
     * @param method
     * @return
     */
    public static Result validateParameter(Method method, Object args[]) {
        // TODO
        return null;
    }

    /**
     * 验证传参dto
     * 
     * @param object
     * @return
     */
    public static <T> Result validate(T object) {
        Result validated = new Result();
        validated.setSuccess(true);

        Class<? extends Object> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            /* String Validator */

            if (field.isAnnotationPresent(StringValidator.class)) {
                Object valueObj = invokeGetter(field, object);

                StringValidator annotationObj = field.getAnnotation(StringValidator.class);
                boolean nullable = annotationObj.nullable();
                if (!nullable && valueObj == null) {
                    validated.fail();
                    break;
                } else if (valueObj == null) {
                    validated.setSuccess(true);
                } else {
                    String value = valueObj.toString();
                    String pattern = annotationObj.pattern();
                    if ("".equals(pattern)) {
                        continue;
                        // pattern = makePattern(annotationObj.minLength(),
                        // annotationObj.maxLength(),
                        // annotationObj.chinese(), annotationObj.letter(),
                        // annotationObj.number());
                    }
                    boolean match = false;
                    try {
                        match = Pattern.matches(pattern, value);
                        if (!match) {
                            logger.error("CommonValidator:Pattern doesn't match.");
                            logger.error("CommonValidator:Pattern is (" + pattern + ").");
                            logger.error("CommonValidator:Value is (" + value + ").");
                        }
                    } catch (Exception ex) {
                        throwWrongFormatException();
                    }
                    if (match) {
                        validated.setSuccess(true);
                    } else {
                        validated.fail();
                        validated.setErrorCode(GlobalErrors.WRONG_PARAMETER);
                        break;
                    }
                }
            }
        }
        return validated;
    }

    /**
     * 执行getter
     * 
     * @param field
     * @return
     */
    private static <T> Object invokeGetter(Field field, T object) {
        Object value = null;

        String fieldName = field.getName();
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Method method = object.getClass().getMethod(methodName);
            Object valueObj = method.invoke(object);
            value = valueObj;
        } catch (Exception ex) {
            throwWrongFormatException();
        }
        return value;
    }

    /**
     * 格式异常
     */
    private static void throwWrongFormatException() {
        Result result = new Result();
        result.setErrorCode(GlobalErrors.WRONG_PATTERN_ANNOTATION);
        result.setMessage("Error in validating.Suggest to be give a wrong formatted object getter-method.");
        throw new AppException(result);
    }

    /**
     * 创建正则表达式
     * 
     * @param minLength
     * @param maxLength
     * @param chinese
     * @param letter
     * @param number
     * @return
     */
    @SuppressWarnings("unused")
    private static String makePattern(long minLength, long maxLength, boolean chinese, boolean letter, boolean number) {
        String pattern = "";
        if (chinese) {
            pattern += (pattern.length() != 0) ? "|" : "";
            pattern += "\\u4e00-\\u9fa5";
        }
        if (letter) {
            pattern += (pattern.length() != 0) ? "|" : "";
            pattern += "a-z|A-Z";
        }
        if (number) {
            pattern += (pattern.length() != 0) ? "|" : "";
            pattern += "0-9";
        }
        pattern = "[" + pattern + "]";
        pattern += "{" + minLength + "," + maxLength + "}";

        return pattern;
    }

}
