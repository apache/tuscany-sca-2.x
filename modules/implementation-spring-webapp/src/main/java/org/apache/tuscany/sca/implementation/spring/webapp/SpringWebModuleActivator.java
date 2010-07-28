/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.implementation.spring.webapp;

import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.host.http.ExtensibleServletHost;
import org.apache.tuscany.sca.implementation.spring.invocation.SpringApplicationContextHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringWebModuleActivator implements ModuleActivator {
    private static Logger log = Logger.getLogger(SpringWebModuleActivator.class.getName());
    private ExtensionPointRegistry registry;

    public SpringWebModuleActivator(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    @Override
    public void start() {
        ExtensibleServletHost servletHost = ExtensibleServletHost.getInstance(registry);
        SpringApplicationContextHelper contextHelper = SpringApplicationContextHelper.getInstance(registry);

        ServletContext servletContext = servletHost.getServletContext();
        if (servletContext != null) {
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            if (context != null) {
                log.info("Spring WebApplicationContext is now injected on Tuscany");
                contextHelper.setParentApplicationContext(context);
            }
        }
    }

    @Override
    public void stop() {
        // NO-OP
    }

}
