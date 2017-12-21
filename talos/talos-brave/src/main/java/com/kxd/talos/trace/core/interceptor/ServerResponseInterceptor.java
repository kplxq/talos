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
package com.kxd.talos.trace.core.interceptor;

import java.util.logging.Logger;

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.tracer.ServerTracer;
import com.kxd.talos.trace.core.utils.Util;

/**
 * Contains logic for dealing with response being returned at server side.
 *
 * - Add custom annotations if adapter provides them.
 * - Will submit server send annotation.
 *
 */
public class ServerResponseInterceptor {

    private final static Logger LOGGER = Logger.getLogger(ServerResponseInterceptor.class.getName());

    private final ServerTracer serverTracer;

    public ServerResponseInterceptor(ServerTracer serverTracer) {
        this.serverTracer = Util.checkNotNull(serverTracer, "Null serverTracer");
    }

    public void handle(ServerResponseAdapter adapter) {
        // We can submit this in any case. When server state is not set or
        // we should not trace this request nothing will happen.
        LOGGER.fine("Sending server send.");
        try {
            for(KeyValueAnnotation annotation : adapter.responseAnnotations())
            {
                serverTracer.submitKeyValueAnnotation(annotation.getKey(), annotation.getValue());
            }
            serverTracer.setServerSend(adapter.responseStatus());
        } finally {
            serverTracer.clearCurrentSpan();
        }
    }
}
