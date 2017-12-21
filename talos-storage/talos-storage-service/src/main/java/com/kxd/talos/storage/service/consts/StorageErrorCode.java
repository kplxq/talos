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
package com.kxd.talos.storage.service.consts;

import com.kxd.framework.lang.Description;

/**
 * 请输入功能描述
 *
 * @author X-MAN 2016年10月11日
 */
public final class StorageErrorCode {
	
	@Description("kafka配置数据为空")
	public static final String STG_001 = "STG_001";
	
	@Description("kafka主题<topic>配置唯恐")
	public static final String STG_002 = "STG_002";
	
	@Description("kafka监听器配置为空")
	public static final String STG_003 = "STG_003";

}
