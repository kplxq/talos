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

import com.kxd.talos.trace.core.span.Span;

/**
 * {@link Runnable} implementation that wraps another Runnable and makes sure the wrapped Runnable will be executed in the
 * same Span/Trace context as the thread from which the Runnable was executed.
 * <p/>
 * Is used by {@link TalosExecutorService}.
 * 
 * @author kristof
 * @see TalosExecutorService
 */
public  class TalosRunnable implements Runnable {
	
	private Runnable wrappedRunnable;
	
	private LocalSpanThreadBinder localSpanThreadBinder;
	
	private Span currentLocalSpan;

    public Runnable wrappedRunnable(){
    	return wrappedRunnable;
    }
    
    public LocalSpanThreadBinder localSpanThreadBinder(){
    	return localSpanThreadBinder;
    }
    
    public Span currentLocalSpan(){
    	return currentLocalSpan;
    }
    
    public TalosRunnable(Runnable runnable, LocalSpanThreadBinder localSpanThreadBinder){
    	this.wrappedRunnable = runnable;
    	this.localSpanThreadBinder = localSpanThreadBinder;
    	this.currentLocalSpan = localSpanThreadBinder.getCurrentLocalSpan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
    	localSpanThreadBinder().setCurrentSpan(currentLocalSpan());
        try {
			wrappedRunnable().run();
		} catch (Exception e) {
			throw e;
		} finally{
			localSpanThreadBinder().setCurrentSpan(null);
		}
    }
}
