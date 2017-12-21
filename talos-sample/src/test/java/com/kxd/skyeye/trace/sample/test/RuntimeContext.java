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
package com.kxd.talos.trace.sample.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请输入功能描述
 *
 * @author 老白 2014年7月12日
 */
public class RuntimeContext {
    private final static  RuntimeContext context = new RuntimeContext();
    private InheritableThreadLocal<ConcurrentHashMap<String,Object>> valueMap = null;
    private ThreadLocal<Integer> lastOperation = new ThreadLocal<Integer>();
    
    private static final int WRITE_OPERATION = 1;
    private static final int READ_OPERATION = 2;
      
    private RuntimeContext(){
        valueMap = new InheritableThreadLocal<ConcurrentHashMap<String,Object>>();
    }
    
    public static final void put(String key,Object value){
        context.putReally(key, value);
    }
    public static final Object get(String key){
        return context.getReally(key);
    }
    public static final void remove(String key){
        context.removeReally(key);
    }
    
    public static final void removeAll(){
        context.removeAllReally();
    }
    
    private void putReally(String key,Object value){
        ConcurrentHashMap<String,Object> oldThreadLocalMap = (ConcurrentHashMap<String,Object>) valueMap.get();
        Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);
        if(wasLastOpReadOrNull(lastOp)||oldThreadLocalMap == null){
            ConcurrentHashMap<String,Object> newThreadLocalMap = duplicateAndInsertNewMap(oldThreadLocalMap);
            newThreadLocalMap.put(key, value);
        }
        else oldThreadLocalMap.put(key, value);
    }
    
    private Object getReally(String key){
        lastOperation.set(READ_OPERATION);
        ConcurrentHashMap<String,Object> threadLocalMap = (ConcurrentHashMap<String,Object>) valueMap.get();
        if(threadLocalMap!=null && key !=null)return threadLocalMap.get(key);
        return null;
    }
    
    private void removeReally(String key){
        ConcurrentHashMap<String,Object> oldThreadLocalMap = (ConcurrentHashMap<String,Object>) valueMap.get();
        Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);
        if(oldThreadLocalMap==null) return;
        
        if(wasLastOpReadOrNull(lastOp)){
            ConcurrentHashMap<String,Object> newThreadLocalMap = duplicateAndInsertNewMap(oldThreadLocalMap);
            newThreadLocalMap.remove(key);
        }
        else oldThreadLocalMap.remove(key);
    }
    
    private void removeAllReally(){
        lastOperation.set(WRITE_OPERATION);
        valueMap.remove();
    }
    
    private Integer getAndSetLastOperation(int op) {
        Integer lastOp = lastOperation.get();
        lastOperation.set(op);
        return lastOp;
    }
    
    private boolean wasLastOpReadOrNull(Integer lastOp) {
        return lastOp == null || lastOp.intValue() == READ_OPERATION;
    }
    
    private ConcurrentHashMap<String, Object> duplicateAndInsertNewMap(
            Map<String, Object> oldMap) {
        ConcurrentHashMap<String, Object> newMap = new ConcurrentHashMap<String, Object>();
        if (oldMap != null) {
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
        }
        valueMap.set(newMap);
        return newMap;
    }
}