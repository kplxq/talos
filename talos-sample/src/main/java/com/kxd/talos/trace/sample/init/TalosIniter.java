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
package com.kxd.talos.trace.sample.init;

import com.kxd.talos.core.trace.TalosCallbackTemplate;
import com.kxd.talos.trace.core.Talos;
import com.kxd.talos.trace.core.collector.LoggingSpanCollector;

public class TalosIniter {
	static Talos talos;

	static TalosCallbackTemplate template;

	private static void init() {
		if(null == talos) {
			talos = new Talos("init-sample", 1.0f, new LoggingSpanCollector());
			template = new TalosCallbackTemplate();
			template.setTalos(talos);
		}

	}

	public static Talos getTalos() {
		init();
		return talos;
	}

	public static void setTalos(Talos talos) {
		TalosIniter.talos = talos;
	}

	public static TalosCallbackTemplate getTemplate() {
		init();
		return template;
	}

	public static void setTemplate(TalosCallbackTemplate template) {
		TalosIniter.template = template;
	}
}
