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
package com.kxd.talos.trace.interceptor.server.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.interceptor.ServerRequestInterceptor;
import com.kxd.talos.trace.core.interceptor.ServerResponseInterceptor;

/**
 * Servlet filter that will extract trace headers from the request and send sr
 * (server received) and ss (server sent) annotations.
 */
public class TalosServletFilter implements Filter {

	private ServerRequestInterceptor requestInterceptor;
	private ServerResponseInterceptor responseInterceptor;
	private SpanNameProvider spanNameProvider = new DefaultSpanNameProvider();
	private Talos talos = null;
	private String patterns;
	private String[] pathPatternArray;
    private final PathMatcher   pathMatcher       = new AntPathMatcher();
    /** 逗号 空格 分号 换行 */
    private final String        URL_SPLIT_PATTERN = "[, ;\r\n]";
    
    private final String[]      NULL_STRING_ARRAY = new String[0];

	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		
		pathPatternArray   = strToArray(patterns);
		requestInterceptor = talos.serverRequestInterceptor();
		responseInterceptor= talos.serverResponseInterceptor();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

        String currentURL = ((HttpServletRequest) request).getPathInfo().replaceAll("//", "/");
		boolean needFilter = isNeedFilter(pathPatternArray,currentURL);
		
		String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
		boolean hasAlreadyFilteredAttribute = request
				.getAttribute(alreadyFilteredAttributeName) != null;
		
		

		if (!needFilter || hasAlreadyFilteredAttribute) {
			// Proceed without invoking this filter...
			filterChain.doFilter(request, response);
		} else {

			final StatusExposingServletResponse statusExposingServletResponse = new StatusExposingServletResponse(
					(HttpServletResponse) response);
			requestInterceptor.handle(new HttpServerRequestAdapter(
					new ServletHttpServerRequest((HttpServletRequest) request),
					spanNameProvider));

			try {
				filterChain.doFilter(request, statusExposingServletResponse);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				responseInterceptor.handle(new HttpServerResponseAdapter(
						new HttpResponse() {
							@Override
							public int getHttpStatusCode() {
								return statusExposingServletResponse
										.getStatus();
							}
						}));
			}
		}
	}

	@Override
	public void destroy() {

	}

	private String getAlreadyFilteredAttributeName() {
		String name = getFilterName();
		if (name == null) {
			name = getClass().getName();
		}
		return name + ".FILTERED";
	}

	private final String getFilterName() {
		return (this.filterConfig != null ? this.filterConfig.getFilterName()
				: null);
	}

	private static class StatusExposingServletResponse extends
			HttpServletResponseWrapper {
		// The Servlet spec says: calling setStatus is optional, if no status is
		// set, the default is OK.
		private int httpStatus = HttpServletResponse.SC_OK;

		public StatusExposingServletResponse(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void sendError(int sc) throws IOException {
			httpStatus = sc;
			super.sendError(sc);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			httpStatus = sc;
			super.sendError(sc, msg);
		}

		@Override
		public void setStatus(int sc) {
			httpStatus = sc;
			super.setStatus(sc);
		}

		public int getStatus() {
			return httpStatus;
		}
	}
	
	private String[] strToArray(String urlStr) {
		if (urlStr == null) {
			return NULL_STRING_ARRAY;
		}
		String[] urlArray = urlStr.split(URL_SPLIT_PATTERN);

		List<String> urlList = new ArrayList<String>();

		for (String url : urlArray) {
			url = url.trim();
			if (url.length() == 0) {
				continue;
			}
			urlList.add(url);
		}

		return urlList.toArray(NULL_STRING_ARRAY);
	}
    
	private boolean isNeedFilter(String[] patternArray, String url) {
		for (String whiteURL : patternArray) {
			if (pathMatcher.match(whiteURL, url)) {
				return true;
			}
		}
		return false;
	}

	public Talos getTalos() {
		return talos;
	}

	public void setTalos(Talos talos) {
		this.talos = talos;
	}

	public String getPatterns() {
		return patterns;
	}

	public void setPatterns(String patterns) {
		this.patterns = patterns;
	}

}
