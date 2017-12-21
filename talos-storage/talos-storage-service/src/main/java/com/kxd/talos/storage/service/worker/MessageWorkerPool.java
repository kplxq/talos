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
package com.kxd.talos.storage.service.worker;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kxd.talos.storage.service.data.CountResult;
import com.kxd.talos.storage.service.data.TalosMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月10日
 */
public class MessageWorkerPool {
    Logger                                    logger  = LoggerFactory.getLogger(MessageWorkerPool.class);

    private Map<String, List<IMessageWorker>> workers = null;

    public CountResult attach(TalosMessage message) {
    	CountResult result = new CountResult();
        String topic = message.getWrappedMessage().get(0).topic();
        for (Entry<String, List<IMessageWorker>> worker : workers.entrySet()) {
            List<IMessageWorker> messageWorkerList = null;
            try {
                if (StringUtils.equals(worker.getKey(), topic)) {
                    messageWorkerList = workers.get(topic);

                    if (messageWorkerList == null) {
                        logger.warn("topic message worker is empty, topic:{}",topic);
                        return result;
                    } else {
                        for (IMessageWorker messageWorker : messageWorkerList) {
                            result = messageWorker.execute(message);
                            if (!result.isSuccess()) {
                                if(!"message is null".equals(result.getErrorCode())) {
                                    logger.error(
                                            "topic execute failed, topic:{}, worker:{}, code:{}, message:{}.",
                                            new String[] { topic, messageWorker.getClass().toString(),
                                                    result.getErrorCode(), result.getMessage() });
                                }
                            }
                        }

                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }

    public Map<String, List<IMessageWorker>> getWorkers() {
        return workers;
    }

    public void setWorkers(Map<String, List<IMessageWorker>> workers) {
        this.workers = workers;
    }

}
