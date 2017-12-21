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
package com.kxd.talos.trace.core.collector;

import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple {@link SpanCollector} implementation which logs the span through jul at INFO level.
 * <p/>
 * Can be used for testing and debugging.
 * 
 * @author kristof
 */
public class LoggingSpanCollector implements SpanCollector {

    private final Logger logger ;

    public LoggingSpanCollector() {
        logger = LoggerFactory.getLogger(LoggingSpanCollector.class);
    }

    public LoggingSpanCollector(String loggerName) {
        Util.checkNotBlank(loggerName, "Null or blank loggerName");
        logger = LoggerFactory.getLogger(loggerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collect(final Span span) {
        Util.checkNotNull(span, "Null span");

        if (getLogger().isErrorEnabled()) {
            getLogger().info(span.toString());
        }
    }

   Logger getLogger() {
        return logger;
    }

}
