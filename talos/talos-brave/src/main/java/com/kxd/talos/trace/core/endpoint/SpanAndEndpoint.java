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
package com.kxd.talos.trace.core.endpoint;

import com.kxd.talos.trace.core.span.Span;
import com.kxd.talos.trace.core.state.ServerClientAndLocalSpanState;

public interface SpanAndEndpoint {

	/**
	 * Gets the span to which to add annotations.
	 * 
	 * @return Span to which to add annotations. Can be <code>null</code>. In
	 *         that case the different submit methods will not do anything.
	 */
	Span span();

	/**
	 * Indicates the network context of the local service being traced.
	 * 
	 * @return Endpoint to add to annotations. Cannot be <code>null</code>.
	 */
	Endpoint endpoint();

	/**
	 * Span and endpoint never change reference.
	 */
	public static class StaticSpanAndEndpoint implements SpanAndEndpoint {
		private final Span span;
		private final Endpoint endpoint;
		public static StaticSpanAndEndpoint create(Span span, Endpoint endpoint) {
			return new StaticSpanAndEndpoint(span,endpoint);
		}

		@Override
		public Span span() {
			return span;
		}

		@Override
		public Endpoint endpoint() {
			return endpoint;
		}
		
		private StaticSpanAndEndpoint(Span span, Endpoint endpoint){
			this.span = span;
			this.endpoint = endpoint;
		}
	}

	public static class ServerSpanAndEndpoint implements SpanAndEndpoint {
		private ServerClientAndLocalSpanState state;
		
		public ServerClientAndLocalSpanState state(){
			return state;
		}

		public static ServerSpanAndEndpoint create(ServerClientAndLocalSpanState state) {
			return new ServerSpanAndEndpoint(state);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Span span() {
			return state().getCurrentLocalSpan();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Endpoint endpoint() {
			return state().endpoint();
		}
		
		private ServerSpanAndEndpoint(ServerClientAndLocalSpanState state){
			this.state = state;
		}
	}

	public static class ClientSpanAndEndpoint implements SpanAndEndpoint {
		private ServerClientAndLocalSpanState state;
		
		public ServerClientAndLocalSpanState state(){
			return state;
		}

		public static ClientSpanAndEndpoint create(ServerClientAndLocalSpanState state) {
			return new ClientSpanAndEndpoint(state);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Span span() {
			return state().getCurrentLocalSpan();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Endpoint endpoint() {
			return state().endpoint();
		}
		
		private ClientSpanAndEndpoint(ServerClientAndLocalSpanState state){
			this.state = state;
		}
	}

	public static class LocalSpanAndEndpoint implements SpanAndEndpoint {
		private ServerClientAndLocalSpanState state;
		public ServerClientAndLocalSpanState state(){
			return state;
		}

		public static LocalSpanAndEndpoint create(ServerClientAndLocalSpanState state) {
			return new LocalSpanAndEndpoint(state);
		}

		@Override
		public Span span() {
			return state().getCurrentLocalSpan();
		}

		@Override
		public Endpoint endpoint() {
			return state().endpoint();
		}
		
		private LocalSpanAndEndpoint(ServerClientAndLocalSpanState state){
			this.state = state;
		}
	}
}
