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
package com.kxd.framework.result;

/**
 * Json 返回值
 * @author qiaojs
 *
 */
public class JsonResult {
    
    private Boolean success;

    /** 提示信息.msg只可以存放前台展示用的提示信息，错误类型可使用下面的code字段。 */
    private String  message;

    /** 当前端需要根据不同的类型进行处理时，可给code赋值 */
    private String  code;

    /** 返回前端的数据 */
    private Object  data;
    
    public JsonResult(){
    }
    
    public JsonResult(Boolean success){
        this.success = success;
    }
    
    public void fail(){
        this.success = false;
    }
    
    public void fail(String message){
        this.success = false;
        this.message = message;
    }
    
    public void fail(Object data){
        this.success = false;
        this.data = data;
    }
    
    public void fail(String message,Object data){
        this.success = false;
        this.message = message;
        this.data = data;
    }
    
    public void success(){
        this.success = true;
    }
    
    public void success(String message){
        this.success = true;
        this.message = message;
    }
    
    public void success(Object data){
        this.success = true;
        this.data = data;
    }
    
    public void success(String message,Object data){
        this.success = true;
        this.message = message;
        this.data = data;
    }
    
    public void result(String code,Object data){
        this.code = code;
        this.data = data;
    }
    
    public void result(String code,String message){
        this.code = code;
        this.message = message;
    }
    
    public void result(String code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * @return the success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
 
}
