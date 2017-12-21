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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月20日
 */
public class TestFatherAndChildThreadLocal {
	
	public static void main(String[] args) throws InterruptedException{
	final InheritableThreadLocal<Span> inheritableThreadLocal = new InheritableThreadLocal<Span>();
    inheritableThreadLocal.set(new Span("xiexiexie"));
    //输出 xiexiexie
    Object o = inheritableThreadLocal.get();
    
    System.out.println("parent first span "+o);
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("========");
            inheritableThreadLocal.get();
            Object o1 = inheritableThreadLocal.get();
            System.out.println(Thread.currentThread().getName()+" runnable before span is "+o1);
            inheritableThreadLocal.set(new Span("zhangzhangzhang"));
            Object o = inheritableThreadLocal.get();
            System.out.println(Thread.currentThread().getName()+" runnable span is "+o);
        }
    };

    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.submit(runnable);
    TimeUnit.SECONDS.sleep(1);
    executorService.submit(runnable);
    TimeUnit.SECONDS.sleep(1);
    System.out.println("========");
    Span span = inheritableThreadLocal.get();
    System.out.println("parent first span "+span);

}

	static class Span {
		public String name;
		public int age;

		public Span(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Span [name=" + name + ", age=" + age + "]";
		}
	}
}
