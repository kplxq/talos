package com.kxd.talos.trace.interceptor.server.http;


import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.concurrent.ClientSpanThreadBinder;
import com.kxd.talos.trace.core.interceptor.ClientResponseInterceptor;
import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.utils.Util;

/**
 * Apache http client response interceptor.
 *
 */
public class HttpClientResponseInterceptor implements HttpResponseInterceptor {

    /** Creates a tracing interceptor with defaults.*/
    public static HttpClientResponseInterceptor create(Talos talos) {
        return new Builder(talos).build();
    }

    public static Builder builder(Talos talos) {
        return new Builder(talos);
    }

    public static final class Builder {
        final Talos talos;

        Builder(Talos talos) {
           this.talos = Util.checkNotNull(talos, "Null talos");
        }

        public HttpClientResponseInterceptor build() {
            return new HttpClientResponseInterceptor(this);
        }
    }

    private final ClientResponseInterceptor responseInterceptor;
    private final ClientSpanThreadBinder spanThreadBinder;

    HttpClientResponseInterceptor(Builder b) { // intentionally hidden
        this.responseInterceptor = b.talos.clientResponseInterceptor();
        this.spanThreadBinder = b.talos.clientSpanThreadBinder();
    }

    /**
     * @deprecated please use {@link #create(Brave)} or {@link #builder(Brave)}; using this constructor means that
     * a "client received" event will not be sent when using {@code HttpAsyncClient}
     */
    public HttpClientResponseInterceptor(final ClientResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
        this.spanThreadBinder = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        // When using HttpAsyncClient, this interceptor does not run on the same thread as the request interceptor.
        // So in that case, the current client span will be null. To still be able to submit "client received", we get
        // the span from the HttpContext (where we put it in the request interceptor).
    	if (spanThreadBinder.getCurrentClientSpan() == null) {
            Object span = context.getAttribute(HttpClientRequestInterceptor.SPAN_ATTRIBUTE);
            if (span instanceof Span) {
                spanThreadBinder.setCurrentSpan((Span) span);
            }
        }
    	
        final HttpClientClientResponse httpClientResponse = new HttpClientClientResponse(response);
        responseInterceptor.handle(new HttpClientResponseAdapter(httpClientResponse));
    }

}
