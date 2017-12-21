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

import com.kxd.talos.trace.core.annotation.KeyValueAnnotation;
import com.kxd.talos.trace.core.tracer.ClientTracer;
import com.kxd.talos.trace.core.utils.Util;

/**
 * Contains logic for dealing with response from client request.
 * This means it will:
 *
 * - Submit potential annotations
 * - Submit client received annotation
 *
 * You will have to implement ClientResponseAdapter.
 *
 * @see ClientResponseAdapter
 */
public class ClientResponseInterceptor {

    private final ClientTracer clientTracer;

    public ClientResponseInterceptor(ClientTracer clientTracer) {
        this.clientTracer = Util.checkNotNull(clientTracer, "Null clientTracer");
    }

    /**
     * Handle a client response.
     *
     * @param adapter Adapter that hides implementation details.
     */
    public void handle(ClientResponseAdapter adapter) {
        try {
            for (KeyValueAnnotation annotation : adapter.responseAnnotations()) {
                clientTracer.submitKeyValueAnnotation(annotation.getKey(), annotation.getValue());
            }
        }
        finally
        {
            clientTracer.setClientReceived(adapter.responseStatus());
        }
    }
}
