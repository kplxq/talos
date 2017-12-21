/**
 *  Copyright 2013 <kristofa@github.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kxd.talos.trace.core.concurrent;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.tracer.ServerTracer;
import com.kxd.talos.trace.core.utils.Util;

/**
 * {@link ExecutorService} that wraps around an existing {@link ExecutorService} and that makes sure the threads are executed
 * in the same Span/Trace context as the the thread that invoked execution of the threads.
 * <p/>
 * It uses {@link ServerTracer} and {@link ServerSpanThreadBinder} to accomplish this in a transparent way for the user.
 * <p/>
 * It also implements {@link Closeable}, calling {@link TalosExecutorService#shutdown()}, so the executor service is
 * shut down properly when for example using Spring.
 * 
 * @author kristof
 * @see TalosCallable
 * @see TalosRunnable
 */
public class TalosExecutorService implements ExecutorService, Closeable {

    private final ExecutorService wrappedExecutor;
    private final LocalSpanThreadBinder threadBinder;

    /**
     * Creates a new instance.
     * 
     * @param wrappedExecutor Wrapped ExecutorService to which execution will be delegated.
     * @param threadBinder Thread binder.
     */
    public TalosExecutorService(final ExecutorService wrappedExecutor, final Talos talos) {
        this.wrappedExecutor = Util.checkNotNull(wrappedExecutor, "Null wrappedExecutor");
        this.threadBinder = Util.checkNotNull(talos.localSpanThreadBinder(), "Null threadBinder");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Runnable arg0) {
        final TalosRunnable braveRunnable = new TalosRunnable(arg0, threadBinder);
        wrappedExecutor.execute(braveRunnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return wrappedExecutor.awaitTermination(timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> arg0) throws InterruptedException {

        return wrappedExecutor.invokeAll(buildBraveCollection(arg0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> arg0, final long arg1, final TimeUnit arg2)
        throws InterruptedException {
        return wrappedExecutor.invokeAll(buildBraveCollection(arg0), arg1, arg2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> arg0) throws InterruptedException, ExecutionException {
        return wrappedExecutor.invokeAny(buildBraveCollection(arg0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> arg0, final long arg1, final TimeUnit arg2)
        throws InterruptedException, ExecutionException, TimeoutException {
        return wrappedExecutor.invokeAny(buildBraveCollection(arg0), arg1, arg2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return wrappedExecutor.isShutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated() {
        return wrappedExecutor.isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        wrappedExecutor.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Runnable> shutdownNow() {
        return wrappedExecutor.shutdownNow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(final Callable<T> arg0) {
        final TalosCallable<T> braveCallable = new TalosCallable<T>(arg0, threadBinder);
        return wrappedExecutor.submit(braveCallable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<?> submit(final Runnable arg0) {
        final TalosRunnable braveRunnable = new TalosRunnable(arg0, threadBinder);
        return wrappedExecutor.submit(braveRunnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(final Runnable arg0, final T arg1) {
        final TalosRunnable braveRunnable = new TalosRunnable(arg0, threadBinder);
        return wrappedExecutor.submit(braveRunnable, arg1);
    }

    private <T> Collection<? extends Callable<T>> buildBraveCollection(
        final Collection<? extends Callable<T>> originalCollection) {
        final Collection<Callable<T>> collection = new ArrayList<Callable<T>>();
        for (final Callable<T> t : originalCollection) {
            collection.add(new TalosCallable<T>(t, threadBinder));
        }
        return collection;
    }

    /**
     * Convenience for try-with-resources, or frameworks such as Spring that automatically process this.
     **/
    @Override
    public void close() {
        shutdown();
    }
}
