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

package org.apache.tuscany.sca.stripes;

import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.Interceptor;

import javax.servlet.ServletContext;

/**
 * <p>Base class for developing Interceptors with dependencies on SCA component references. <b>Not</b>
 * to be confused with {@link TuscanyInterceptor} which injects SCA reference proxies into
 * ActionBeans.  For example, you may wish to subclass this class in order to write an
 * interceptor with access to Tuscany ???.</p>
 * 
 * TODO: does Tuscany really need this?
 *
 * <p>Since Interceptors are long-lived objects that are instantiated at application startup
 * time, and not per-request, the Tuscany wiring takes place in the init() method and happens
 * only once when the interceptor is first created and initialized.</p>
 *
 * Created for Tuscany from the Stripes SpringInterceptorSupport written by Tim Fennell
 */
public abstract class TuscanyInterceptorSupport implements Interceptor, ConfigurableComponent {

    /**
     * Fetches the ServletContext and invokes TuscanyHelper.injectBeans() to auto-wire any
     * Tuscany dependencies prior to being placed into service.
     *
     * @param configuration the Stripes Configuration
     * @throws Exception if there are problems with the Tuscany configuration/wiring
     */
    public void init(Configuration configuration) throws Exception {
        ServletContext ctx = configuration.getBootstrapPropertyResolver()
                                    .getFilterConfig().getServletContext();

        TuscanyHelper.injectBeans(this, ctx);
    }
}
