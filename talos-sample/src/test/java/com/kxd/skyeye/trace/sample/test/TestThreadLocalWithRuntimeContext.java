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

public class TestThreadLocalWithRuntimeContext {
    public static void main(String[] args) throws InterruptedException {
        RuntimeContext.put("final", "parentValue");
        RuntimeContext.put("unfinal",new StringBuffer("ParentBuffer"));
                                                                                                                                                                                                     
        System.out.println(Thread.currentThread().getName() + " first get stringItl : " + RuntimeContext.get("final"));
        System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + RuntimeContext.get("unfinal").toString());
                                                                                                                                                                                                     
        for(int i=0; i<2; i++){
            new Thread(){
                public void run(){               
                    System.out.println(Thread.currentThread().getName() + " first get stringItl : " + RuntimeContext.get("final"));
                    RuntimeContext.put("final", Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " first get after set stringItl : " + RuntimeContext.get("final"));
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + RuntimeContext.get("final2"));
                    RuntimeContext.put("final2", Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " first get after set stringItl2 : " + RuntimeContext.get("final2"));
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + RuntimeContext.get("unfinal").toString());
                    ((StringBuffer)RuntimeContext.get("unfinal")).append(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " first get after set stringBufferItl : " + RuntimeContext.get("unfinal").toString());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferIt2 : " + RuntimeContext.get("unfinal2"));
                    if(RuntimeContext.get("unfinal2")!=null){
                    	((StringBuffer)RuntimeContext.get("unfinal")).append(Thread.currentThread().getName());
                    }
                    else{
                        RuntimeContext.put("unfinal2", Thread.currentThread().getName());
                    }
                    System.out.println(Thread.currentThread().getName() + " first get after set stringBufferItl2 : " + RuntimeContext.get("unfinal2").toString());
                }
                                                                                                                                                                                                             
            }.start();
        }
                                                                                                                                                                                                     
        for(int i=0; i<2; i++){
            new Thread(){
                public void run(){               
                    System.out.println(Thread.currentThread().getName() + " first get stringItl : " + RuntimeContext.get("final"));
                    RuntimeContext.put("final", Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl : " + RuntimeContext.get("final"));
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + RuntimeContext.get("final2"));
                    RuntimeContext.put("final2", Thread.currentThread().getName() + "Child");
                    System.out.println(Thread.currentThread().getName() + " get after set stringItl2 : " + RuntimeContext.get("final2"));
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferItl : " + RuntimeContext.get("unfinal").toString());
                    RuntimeContext.put("unfinal",Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl : " + RuntimeContext.get("unfinal").toString());
                                                                                                                                                                                                                 
                    System.out.println(Thread.currentThread().getName() + " first get stringBufferIt2 : " + RuntimeContext.get("unfinal2"));
                    if(RuntimeContext.get("unfinal2")!=null){
                    	((StringBuffer)RuntimeContext.get("unfinal2")).append(Thread.currentThread().getName());
                    }
                    else{
                        RuntimeContext.put("unfinal2", Thread.currentThread().getName());
                    }
                    System.out.println(Thread.currentThread().getName() + " get after set stringBufferItl2 : " + RuntimeContext.get("unfinal2").toString());
                }
                                                                                                                                                                                                             
            }.start();
        }
                                                                                                                                                                                                     
        TimeUnit.SECONDS.sleep(2);//let children threads run first
        System.out.println(Thread.currentThread().getName() + " second get stringItl : " + RuntimeContext.get("final"));
        System.out.println(Thread.currentThread().getName() + " first get stringItl2 : " + RuntimeContext.get("final2"));
        System.out.println(Thread.currentThread().getName() + " second get stringBufferItl : " + RuntimeContext.get("unfinal"));
        System.out.println(Thread.currentThread().getName() + " first get stringBufferItl2 : " + RuntimeContext.get("unfinal2"));
    }
}
