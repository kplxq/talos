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

import java.util.concurrent.TimeUnit;

public class TestThreadLocal {
    private static ThreadLocal<String> stringItl = new InheritableThreadLocal<String>(){
        protected String initialValue() {
            System.out.println(Thread.currentThread().getName() + " initialize stringItl variable.");
            return "String init";
        }
    };
                                                                                                                                                                                                 
    private static ThreadLocal<String> stringItl2 = new InheritableThreadLocal<String>(){
        protected String initialValue() {
            System.out.println(Thread.currentThread().getName() + " initialize stringItl2 variable.");
            return "String2 init";
        }
    };
                                                                                                                                                                                                 
    private static ThreadLocal<StringBuffer> stringBufferItl = new InheritableThreadLocal<StringBuffer>(){
        protected StringBuffer initialValue() {
            System.out.println(Thread.currentThread().getName() + " initialize stringBufferItl variable.");
            return new StringBuffer("StringBuffer init");
        }
    };
                                                                                                                                                                                                 
    private static ThreadLocal<StringBuffer> stringBufferItl2 = new InheritableThreadLocal<StringBuffer>(){
        protected StringBuffer initialValue() {
            System.out.println(Thread.currentThread().getName() + " initialize stringBufferItl2 variable.");
            return new StringBuffer("StringBuffer2 init");
        }
    };
    public static void main(String[] args) throws InterruptedException {
        stringItl.set("Parent");
        stringBufferItl.set(new StringBuffer("ParentBuffer"));
                                                                                                                                                                                                     
        System.out.println(Thread.currentThread().getName() + " first get stringItl : " + stringItl.get());
        System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + stringBufferItl.get().toString());
        
        //直接get后进行可变对象的
/*        for(int i=0; i<2; i++){
            new Thread(){
                public void run(){               
                    System.out.println(Thread.currentThread().getName() + " first get stringItl : " + stringItl.get());
                    stringItl.set(Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl : " + stringItl.get());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + stringItl2.get());
                    stringItl2.set(Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl2 : " + stringItl2.get());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + stringBufferItl.get().toString());
                    stringBufferItl.get().append(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl : " + stringBufferItl.get().toString());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferIt2 : " + stringBufferItl2.get().toString());
                    stringBufferItl2.get().append(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl2 : " + stringBufferItl2.get().toString());
                }
                                                                                                                                                                                                             
            }.start();
        }*/
        
        for(int i=0; i<2; i++){
            new Thread(){
                public void run(){               
                    System.out.println(Thread.currentThread().getName() + " first get stringItl : " + stringItl.get());
                    stringItl.set(Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl : " + stringItl.get());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + stringItl2.get());
                    stringItl2.set(Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl2 : " + stringItl2.get());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + stringBufferItl.get().toString());
                    StringBuffer strBuffer1 = new StringBuffer(stringBufferItl.get());
                    
                    strBuffer1.append(Thread.currentThread().getName());
                    stringBufferItl.set(strBuffer1);
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl : " + stringBufferItl.get().toString());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferIt2 : " + stringBufferItl2.get().toString());
                    stringBufferItl2.get().append(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl2 : " + stringBufferItl2.get().toString());
                }
                                                                                                                                                                                                             
            }.start();
        }
        
        for(int i=0; i<2; i++){
            new Thread(){
                public void run(){               
                    System.out.println(Thread.currentThread().getName() + " first get stringItl : " + stringItl.get());
                    stringItl.set(Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl : " + stringItl.get());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + stringItl2.get());
                    stringItl2.set(Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl2 : " + stringItl2.get());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + stringBufferItl.get().toString());
                    stringBufferItl.set(new StringBuffer(Thread.currentThread().getName() + "Buffer"));
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl : " + stringBufferItl.get().toString());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferIt2 : " + stringBufferItl2.get().toString());
                    stringBufferItl2.get().append(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl2 : " + stringBufferItl2.get().toString());
                }
                                                                                                                                                                                                             
            }.start();
        }
                                                                                                                                                                                                     
        TimeUnit.SECONDS.sleep(2);//let children threads run first
        System.out.println(Thread.currentThread().getName() + " second get stringItl : " + stringItl.get());
        System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + stringItl2.get());
        System.out.println(Thread.currentThread().getName() + " second get stringBufferItl : " + stringBufferItl.get().toString());
        System.out.println(Thread.currentThread().getName() + " first get stringBufferItl2 : " + stringBufferItl2.get().toString());
    }
}
