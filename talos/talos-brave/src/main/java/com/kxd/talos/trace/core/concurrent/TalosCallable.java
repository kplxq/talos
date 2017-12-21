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

import java.util.concurrent.Callable;

import com.kxd.talos.trace.core.span.Span;

/**
 * Callable implementation that wraps another Callable and makes sure the
 * wrapped Callable will be executed in the same Span/Trace context as the
 * thread from which the Callable was executed.
 * <p/>
 * Is used by {@link TalosExecutorService}.
 * 
 * @author kristof
 * @param <T>
 *            Return type.
 * @see TalosExecutorService
 */
public  class TalosCallable<T> implements Callable<T> {
	private Span currentLocalSpan;
	private Callable<T> wrappedCallable;
	private LocalSpanThreadBinder localSpanThreadBinder;

	public Callable<T> wrappedCallable(){
    	return wrappedCallable;
    }

	public LocalSpanThreadBinder localSpanThreadBinder(){
		return localSpanThreadBinder;
	}

	public Span currentLocalSpan(){
		return currentLocalSpan;
	}
	
	public TalosCallable(Callable<T> wrappedCallable, LocalSpanThreadBinder localSpanThreadBinder){
		this.currentLocalSpan = localSpanThreadBinder.getCurrentLocalSpan();
		this.wrappedCallable  = wrappedCallable;
		this.localSpanThreadBinder = localSpanThreadBinder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T call() throws Exception {
		localSpanThreadBinder().setCurrentSpan(currentLocalSpan());
		try {
			return wrappedCallable().call();
		} catch (Exception e) {
			throw e;
		} finally{
			localSpanThreadBinder.setCurrentSpan(null);
		}
	}
}
