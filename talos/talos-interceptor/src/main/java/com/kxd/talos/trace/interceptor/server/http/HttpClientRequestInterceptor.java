package com.kxd.talos.trace.interceptor.server.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.concurrent.ClientSpanThreadBinder;
import com.kxd.talos.trace.core.interceptor.ClientRequestInterceptor;
import com.kxd.talos.trace.core.utils.Util;


/**
 * Apache http client request interceptor.
 *
 */
public class HttpClientRequestInterceptor implements HttpRequestInterceptor {

    static final String SPAN_ATTRIBUTE = HttpClientResponseInterceptor.class.getName() + ".span";

    /** Creates a tracing interceptor with defaults. */
    public static HttpClientRequestInterceptor create(Talos talos) {
        return new Builder(talos).build();
    }

    public static Builder builder(Talos talos) {
        return new Builder(talos);
    }

    public static final class Builder {
        final Talos talos;
        SpanNameProvider spanNameProvider = new DefaultSpanNameProvider();

        Builder(Talos talos) {
            this.talos = Util.checkNotNull(talos, "Null talos");
        }

        public Builder spanNameProvider(SpanNameProvider spanNameProvider) {
            this.spanNameProvider = Util.checkNotNull(spanNameProvider, "Null spanNameProvider");
            return this;
        }

        public HttpClientRequestInterceptor build() {
            return new HttpClientRequestInterceptor(this);
        }
    }

    private final ClientRequestInterceptor requestInterceptor;
    private final SpanNameProvider spanNameProvider;
    private final ClientSpanThreadBinder spanThreadBinder;

    HttpClientRequestInterceptor(Builder b) { // intentionally hidden
        this.requestInterceptor = b.talos.clientRequestInterceptor();
        this.spanNameProvider = b.spanNameProvider;
        this.spanThreadBinder = b.talos.clientSpanThreadBinder();
    }

    /**
     * Creates a new instance.
     *
     * @param requestInterceptor
     * @param spanNameProvider Provides span name for request.
     * @deprecated please use {@link #create(Brave)} or {@link #builder(Brave)}; using this constructor means that
     * a "client received" event will not be sent when using {@code HttpAsyncClient}
     */
    public HttpClientRequestInterceptor(ClientRequestInterceptor requestInterceptor, SpanNameProvider spanNameProvider) {
        this.requestInterceptor = requestInterceptor;
        this.spanNameProvider = spanNameProvider;
        this.spanThreadBinder = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        HttpClientRequestAdapter adapter = new HttpClientRequestAdapter(new HttpClientClientRequest(request), spanNameProvider);
        requestInterceptor.handle(adapter);
        if (spanThreadBinder != null) {
            // When using HttpAsyncClient, the response interceptor is not run on the same thread, so for it the client
            // span would just not be set. By putting it into the HttpContext, we can keep track of the client span and
            // retrieve it later in the response interceptor.
            context.setAttribute(SPAN_ATTRIBUTE, spanThreadBinder.getCurrentClientSpan());
        }
    }
}
