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

package org.apache.tuscany.sca.host.webapp;

import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;

/**
 * Activates the webapp host by registering the webapp ServletHost impl
 *
 * @version $Rev$ $Date$
 */
public class WebAppModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry extensionPointRegistry) {

        ServletHostExtensionPoint servletHosts =
            extensionPointRegistry.getExtensionPoint(ServletHostExtensionPoint.class);

        List<ServletHost> hosts = servletHosts.getServletHosts();
        ServletHost host = null;
        try {
           host = TuscanyServletFilter.getServletHost();
        } catch (NoClassDefFoundError e) {
        	// ignore 
        }
        if (host != null) {
            hosts.clear();
            hosts.add(host);
        }
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
