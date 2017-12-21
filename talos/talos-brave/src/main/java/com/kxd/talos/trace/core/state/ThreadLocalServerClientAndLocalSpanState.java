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
package com.kxd.talos.trace.core.state;

import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.span.ServerSpan;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Util;

/**
 * {@link ServerClientAndLocalSpanState} implementation that keeps trace state using a ThreadLocal variable.
 * 
 * @author kristof
 */
public final class ThreadLocalServerClientAndLocalSpanState implements ServerClientAndLocalSpanState {

    private final static ThreadLocal<ServerSpan> currentServerSpan = new ThreadLocal<ServerSpan>() {

        @Override
        protected ServerSpan initialValue() {
            return ServerSpan.EMPTY;
        }
    };
    private final static ThreadLocal<Span> currentClientSpan = new ThreadLocal<Span>();

    private final static ThreadLocal<Span> currentLocalSpan = new ThreadLocal<Span>();
    
    private final static ThreadLocal<Span> prefixLocalSpan  = new ThreadLocal<Span>();

    private final Endpoint endpoint;
    
    /**
     * Constructor
     *
     * @param ip Int representation of ipv4 address.
     * @param port port on which current process is listening.
     * @param serviceName Name of the local service being traced. Should be lowercase and not <code>null</code> or empty.
     */
    public ThreadLocalServerClientAndLocalSpanState(int ip, int port, String serviceName) {
        Util.checkNotBlank(serviceName, "Service name must be specified.");
        endpoint = Endpoint.create(serviceName, ip, port);
    }
    
    public ThreadLocalServerClientAndLocalSpanState(int ip,int port,String processId,String serviceName){
        Util.checkNotBlank(serviceName, "Service name must be specified.");
        endpoint = Endpoint.create(serviceName, ip, port,processId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerSpan getCurrentServerSpan() {
        return currentServerSpan.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentServerSpan(final ServerSpan span) {
        if (span == null) {
            currentServerSpan.remove();
        } else {
            currentServerSpan.set(span);
        }
    }
    
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Endpoint endpoint() {
        return endpoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Span getCurrentClientSpan() {
        return currentClientSpan.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentClientSpan(final Span span) {
        currentClientSpan.set(span);
    }

    @Override
    public Boolean sample() {
        return currentServerSpan.get().getSample();
    }

    @Override
    public Span getCurrentLocalSpan() {
        return currentLocalSpan.get();
    }
    
    public Span getPrefixLocalSpan() {
    	return prefixLocalSpan.get();
    }
    

    @Override
    public void setCurrentLocalSpan(Span span) {
        if (span == null) {
            currentLocalSpan.remove();
        } else {
            currentLocalSpan.set(span);
        }
    }
    
    public void setPrefixLocalSpan(Span span){
    	if(span == null){
    		prefixLocalSpan.remove();
    	} else{
    		prefixLocalSpan.set(span);
    	}
    }

    public static Span getThreadLocalCurrentLocalSpan() {
        return currentLocalSpan.get();
    }
}
