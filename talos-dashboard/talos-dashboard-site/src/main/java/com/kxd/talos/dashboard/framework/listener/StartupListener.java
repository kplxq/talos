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
package com.kxd.talos.dashboard.framework.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import com.kxd.talos.dashboard.common.ApplicationKey;

/**
 * StartupListener
 * 
 * @author qiaojs  2014-04-14
 */
public class StartupListener extends ContextLoaderListener implements ServletContextListener{
    
    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    public void contextInitialized(ServletContextEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("initializing context...");
        }
        // call Spring's context ContextLoaderListener to initialize
        super.contextInitialized(event);
        ServletContext application = event.getServletContext();
        //设置系统启动时间
        application.setAttribute(ApplicationKey.SYSTEM_STARTUP_TIME, System.currentTimeMillis());
        //在线用户人数（已登录，未登录）。
        //application.setAttribute(ApplicationKey.ONLINE_USER_COUNT, 0);
        //已登录用户
        //application.setAttribute(ApplicationKey.LOGIN_USER_MAP, new HashMap<String,String>());
        //getStartServiceImple(context).processAppInit(context);
    }
}