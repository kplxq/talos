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

import com.kxd.framework.lang.AppException;
import com.kxd.framework.lang.ErrorCode;
import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.span.Span;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年11月2日
 */
public class TalosCallbackTemplate {

    private Talos talos;

    public Object execute(Object req, TalosCallback callback) {
        Span startSpan = null;
        Object result = null;
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            startSpan = talos.start(getMethodName(stackTrace));
            result = callback.execute(req);
        } catch (AppException ae) {
            if (null != startSpan) {
                startSpan.setExType("A");
                startSpan.setErrorCode(ae.getErrorCode());
            }

            throw ae;
        } catch (Throwable t) {
            if (null != startSpan) {
                startSpan.setExType("T");
                startSpan.setErrorCode(ErrorCode.ERROR_SERVICE_INTERCEPTOR_INVOKE);
            }

            throw t;
        } finally {
            talos.finish(startSpan);
        }

        return result;
    }

    private String getMethodName(StackTraceElement[] stackTrace) {
        String methodName = "ERROR.lhldyf";
        try {
            methodName = stackTrace[2].toString();
            methodName = methodName.substring(0, methodName.indexOf("("));
            methodName = methodName.substring(methodName.lastIndexOf(".", methodName.lastIndexOf(".") - 1) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return methodName;
    }

    /**
     * @return the talos
     */
    public Talos getTalos() {
        return talos;
    }

    /**
     * @param talos the talos to set
     */
    public void setTalos(Talos talos) {
        this.talos = talos;
    }
}
