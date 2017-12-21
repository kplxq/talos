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
package com.kxd.talos.trace.sample.dubbo;

import com.kxd.talos.core.trace.TalosCallback;
import com.kxd.talos.core.trace.TalosCallbackTemplate;
import com.kxd.talos.core.trace.TalosTrace;
import com.kxd.talos.trace.sample.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kxd.talos.trace.core.Talos;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年9月7日
 */
@TalosTrace
@Service("dubboService")
public class DubboServiceImpl implements DubboService {


    @Autowired
    private TalosCallbackTemplate template;

    /**
     * {@inheritDoc}
     */
    @Override
    public String echoService(String param) {
        step1(param);
        step2();
        return "OK";
    }

    void step1(final String param) {
        template.execute(null, new TalosCallback() {
            @Override
            public Object execute(Object request) {
                System.out.println(param);
                return null;
            }
        });
    }

    void step2() {
        template.execute(null, new TalosCallback() {
            @Override
            public Object execute(Object request) {
                Talos.collect("step2-context", Utils.uuid());
                return null;
            }
        });
    }

}
