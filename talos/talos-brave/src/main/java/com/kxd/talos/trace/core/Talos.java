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
package com.kxd.talos.trace.core;

import java.net.UnknownHostException;
import java.util.Random;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.collector.LoggingSpanCollector;
import com.kxd.talos.trace.core.collector.SpanCollector;
import com.kxd.talos.trace.core.concurrent.ClientSpanThreadBinder;
import com.kxd.talos.trace.core.concurrent.LocalSpanThreadBinder;
import com.kxd.talos.trace.core.endpoint.SpanAndEndpoint;
import com.kxd.talos.trace.core.interceptor.ClientRequestInterceptor;
import com.kxd.talos.trace.core.interceptor.ClientResponseInterceptor;
import com.kxd.talos.trace.core.interceptor.ServerRequestInterceptor;
import com.kxd.talos.trace.core.interceptor.ServerResponseInterceptor;
import com.kxd.talos.trace.core.sampler.Sampler;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.state.ServerClientAndLocalSpanState;
import com.kxd.talos.trace.core.state.ThreadLocalServerClientAndLocalSpanState;
import com.kxd.talos.trace.core.tracer.AnnotationSubmitter;
import com.kxd.talos.trace.core.tracer.ClientTracer;
import com.kxd.talos.trace.core.tracer.LocalTracer;
import com.kxd.talos.trace.core.tracer.ServerTracer;
import com.kxd.talos.trace.core.utils.Constants;
import com.kxd.talos.trace.core.utils.InetAddressUtilities;
import com.kxd.talos.trace.core.utils.JdkRuntimeUtilities;
import com.kxd.talos.trace.core.utils.Util;

public class Talos {

    private final ServerTracer serverTracer;
    private final ClientTracer clientTracer;
    private final LocalTracer localTracer;
    private final ServerRequestInterceptor serverRequestInterceptor;
    private final ServerResponseInterceptor serverResponseInterceptor;
    private final ClientRequestInterceptor clientRequestInterceptor;
    private final ClientResponseInterceptor clientResponseInterceptor;
    private final AnnotationSubmitter serverSpanAnnotationSubmitter;
    private final LocalSpanThreadBinder localSpanThreadBinder;
    private final ClientSpanThreadBinder clientSpanThreadBinder;
    private ServerClientAndLocalSpanState state;
    private SpanCollector spanCollector = new LoggingSpanCollector();
    private Random random = new Random();
    // default added so callers don't need to check null.
    private Sampler sampler = null;
    
    private String processId = "0";
    
    private volatile boolean init = false;

    /**
     * Builds Brave api objects with following defaults if not overridden:
     * <p>
     * <ul>
     * <li>ThreadLocalServerClientAndLocalSpanState which binds trace/span state to current thread.</li>
     * <li>LoggingSpanCollector</li>
     * <li>Sampler that samples all traces</li>
     * </ul>
     */
    
    
    
    public Talos(String serviceName){
        try {
            int ip = InetAddressUtilities.toInt(InetAddressUtilities.getLocalHostLANAddress());
            processId = JdkRuntimeUtilities.getProcessId();
            state = new ThreadLocalServerClientAndLocalSpanState(ip, 0, processId,serviceName);
            sampler = Sampler.create(1.0f);
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Unable to get Inet address", e);
        }
        
        serverTracer = ServerTracer.builder()
                .randomGenerator(random)
                .spanCollector(spanCollector)
                .state(state)
                .traceSampler(sampler).build();

        clientTracer = ClientTracer.builder()
                .randomGenerator(random)
                .spanCollector(spanCollector)
                .state(state)
                .traceSampler(sampler).build();

        localTracer = LocalTracer.builder()
                .randomGenerator(random)
                .spanCollector(spanCollector)
                .spanAndEndpoint(SpanAndEndpoint.LocalSpanAndEndpoint.create(state))
                .traceSampler(sampler).build();
        
        serverRequestInterceptor = new ServerRequestInterceptor(serverTracer);
        serverResponseInterceptor = new ServerResponseInterceptor(serverTracer);
        clientRequestInterceptor = new ClientRequestInterceptor(clientTracer);
        clientResponseInterceptor = new ClientResponseInterceptor(clientTracer);
        serverSpanAnnotationSubmitter = AnnotationSubmitter.create(SpanAndEndpoint.ServerSpanAndEndpoint.create(state));
        localSpanThreadBinder = new LocalSpanThreadBinder(state);
        clientSpanThreadBinder = new ClientSpanThreadBinder(state);
        
        init = true;
    }
    
    public Talos(String serviceName, float sampleRate){
    	this(serviceName);
    	sampler = Sampler.create(sampleRate);
    }
    
    public Talos(String serviceName, float sampleRate, SpanCollector collector){
    	this(serviceName,sampleRate);
    	spanCollector = Util.checkNotNullDefault(collector, spanCollector);
    }
    
    public Talos(int ip, int port, String serviceName, float sampleRate, SpanCollector collector){
    	this(serviceName,sampleRate,collector);
        state = new ThreadLocalServerClientAndLocalSpanState(ip, port, serviceName);
        sampler = Sampler.create(1.0f);
    }

	/**
     * Client Tracer.
     * <p>
     * It is advised that you use ClientRequestInterceptor and ClientResponseInterceptor instead.
     * Those api's build upon ClientTracer and have a higher level api.
     * </p>
     *
     * @return ClientTracer implementation.
     */
    public ClientTracer clientTracer() {
        return clientTracer;
    }

    /**
     * Returns a tracer used to log in-process activity.
     *
     * @since 3.2
     */
    public LocalTracer localTracer() {
        return localTracer;
    }

    /**
     * Server Tracer.
     * <p>
     * It is advised that you use ServerRequestInterceptor and ServerResponseInterceptor instead.
     * Those api's build upon ServerTracer and have a higher level api.
     * </p>
     *
     * @return ClientTracer implementation.
     */
    public ServerTracer serverTracer() {
        return serverTracer;
    }

    public ClientRequestInterceptor clientRequestInterceptor() {
        return clientRequestInterceptor;
    }

    public ClientResponseInterceptor clientResponseInterceptor() {
        return clientResponseInterceptor;
    }
//
    public ServerRequestInterceptor serverRequestInterceptor() {
        return serverRequestInterceptor;
    }

    public ServerResponseInterceptor serverResponseInterceptor() {
        return serverResponseInterceptor;
    }

    /**
     * Helper object that can be used to propagate local trace state. Typically over different
     * threads.
     *
     * @return {@link LocalSpanThreadBinder}.
     * @see LocalSpanThreadBinder
     */
    public LocalSpanThreadBinder localSpanThreadBinder() {
        return localSpanThreadBinder;
    }
    
    public ClientSpanThreadBinder clientSpanThreadBinder(){
    	return clientSpanThreadBinder;
    }

    /**
     * Can be used to submit application specific annotations to the current server span.
     *
     * @return Server span {@link AnnotationSubmitter}.
     */
    public AnnotationSubmitter serverSpanAnnotationSubmitter() {
        return serverSpanAnnotationSubmitter;
    }

    public static void collect(String key, String value) {
        KeyValueAnnotation kv = KeyValueAnnotation.create(key, value);
        Span currentLocalTracerSpan = ThreadLocalServerClientAndLocalSpanState.getThreadLocalCurrentLocalSpan();
        if(null != currentLocalTracerSpan) {
            synchronized (currentLocalTracerSpan) {
                currentLocalTracerSpan.addToBinary_annotations(kv);
            }
        }
    }
    
    public static void collect(String key, int value) {
        collect(key,String.valueOf(value));
    }
    
    public static void collect(String key, long value) {
        collect(key, String.valueOf(value));
    }
    
    /**
     * 手动调用本地方法的跟踪(trace)
     * @param methodName
     * @return
     */
    public  final Span start(String methodName){
		Span span = null;
    	if(init){
    		span = localTracer.startSpan(Constants.SPAN_NAME_LOCAL, methodName);
    	}
    	return span;
    }
    
    /**
     * 手动结束本地方法的跟踪(trace)
     * @param start
     */
    public final void finish(Span start){
    	if(init){
    		localTracer.finishSpan(start);
    	}
    }
}
