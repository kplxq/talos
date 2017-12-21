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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kxd.talos.storage.service.data.TalosMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.kxd.framework.core.entity.Entity;
import com.kxd.framework.hbase.core.HBaseCommand;
import com.kxd.framework.hbase.core.HBaseEntity;
import com.kxd.framework.lang.AppException;
import com.kxd.talos.storage.service.data.CountResult;
import com.kxd.talos.storage.service.data.TalosTraceHbaseTableDmo;

/**
 * 请输入功能描述
 * 
 * @author X-MAN 2016年10月11日
 */
public class HbaseStorageMessageWorker extends AbstractMessageWorker {

    private HBaseCommand hBaseCommand;

    @Override
    public void filter(TalosMessage message) {
        List<ConsumerRecord<String, String>> recordList = message.getWrappedMessage();
        if (recordList == null || recordList.size() == 0) {
            throw new AppException("");
        }
    }

    @Override
    public void format(TalosMessage message) {
        message.setWillContinue(true);
    }

    @Override
    public void transform(TalosMessage message) {
        if (message.isWillContinue()) {
            List<Entity> dmoList = new ArrayList<Entity>();

            for (ConsumerRecord<String, String> record : message.getWrappedMessage()) {
                String content = record.value();
                Map<String, String> contentMap = new HashMap<String, String>();
                String[] contentArray = StringUtils.split(content, "|");
                TalosTraceHbaseTableDmo dmo = new TalosTraceHbaseTableDmo();
                if (ArrayUtils.isEmpty(contentArray)) {
                    throw new AppException("");
                }
                for (String single : contentArray) {
                    if (single.startsWith("TD")) {
                        dmo.setId(StringUtils.split(single, ":")[1]);
                    } else if (single.startsWith("SI")) {
                        contentMap.put(StringUtils.split(single, ":")[1], content);
                    } else {
                        continue;
                    }
                }
                dmo.setRow(contentMap);
                dmoList.add(dmo);
            }

            message.setTransformDataList(dmoList);
            message.setWillContinue(true);
        }
    }

    @Override
    public CountResult deliver(TalosMessage message) {
        CountResult result = new CountResult();
        if (message.isWillContinue()) {
            List<HBaseEntity> entities = new ArrayList<>();
            for (Entity origEntity : message.getTransformDataList()) {
                entities.add((HBaseEntity) origEntity);
            }

            try {
                hBaseCommand.putList(entities);
                result.success(entities.size());
            } catch (Exception e) {
                e.printStackTrace();
                result.fail(1);
            }
        }
        return result;
    }

    public HBaseCommand gethBaseCommand() {
        return hBaseCommand;
    }

    public void sethBaseCommand(HBaseCommand hBaseCommand) {
        this.hBaseCommand = hBaseCommand;
    }
}
