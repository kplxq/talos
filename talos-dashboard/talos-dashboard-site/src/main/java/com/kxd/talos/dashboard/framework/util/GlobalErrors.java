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
package com.kxd.talos.dashboard.framework.util;

import com.kxd.framework.lang.Description;

/**
 * 请输入功能描述
 * 
 * @author 老白 2014年7月12日
 */
public class GlobalErrors {
    public static final String UNKNOWN_EXCEPTION        = "UNKNOWN-EXCEPTION";

    @Description("系统异常")
    public static final String SYSTEM_ERROR             = "SYSTEM_ERROR";

    @Description("请求参数错误")
    public static final String WRONG_PARAMETER          = "S99_001";

    public static final String WRONG_PATTERN_ANNOTATION = "S99_002";

    @Description("Verify data hash error.It's time to call the caps.")
    public static final String VERIFY_DATA_HASH_ERROR   = "S99_003";

}
