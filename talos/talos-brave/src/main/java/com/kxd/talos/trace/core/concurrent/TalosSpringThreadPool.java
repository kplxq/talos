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
package com.kxd.talos.trace.core.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.kxd.talos.trace.core.Talos;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class TalosSpringThreadPool extends ThreadPoolTaskExecutor {

	private static final long serialVersionUID = -456877804439987895L;

	private final ThreadPoolTaskExecutor wrappedThreadPool;
	private final LocalSpanThreadBinder threadBinder;

	public TalosSpringThreadPool(ThreadPoolTaskExecutor wrappedThreadPool, Talos talos) {
		this.wrappedThreadPool = wrappedThreadPool;
		this.threadBinder = talos.localSpanThreadBinder();
	}

	public void execute(Runnable task) {
		wrappedThreadPool.execute(task);
	}

	public void execute(Runnable task, long startTimeout) {
		final TalosRunnable braveRunnable = new TalosRunnable(task,threadBinder);
		wrappedThreadPool.submit(braveRunnable);
	}

	public Future<?> submit(Runnable task) {
		final TalosRunnable braveRunnable = new TalosRunnable(task,threadBinder);
		return wrappedThreadPool.submit(braveRunnable);
	}

	public <T> Future<T> submit(Callable<T> task) {
		final TalosCallable<T> braveCallable = new TalosCallable<T>(task,
				threadBinder);
		return wrappedThreadPool.submit(braveCallable);
	}

}
