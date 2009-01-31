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

package org.apache.tuscany.sca.http.tomcat.module;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.http.tomcat.TomcatServer;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class TomcatRuntimeModuleActivator implements ModuleActivator {

    private TomcatServer server;

    public void start(ExtensionPointRegistry extensionPointRegistry) {

        // Register a Tomcat Servlet host
        ServletHostExtensionPoint servletHosts =
            extensionPointRegistry.getExtensionPoint(ServletHostExtensionPoint.class);
        
        if (servletHosts.getServletHosts().size() < 1) {
            UtilityExtensionPoint utilities = extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
            final WorkScheduler workScheduler = utilities.getUtility(WorkScheduler.class);
            // Allow privileged access to start MBeans. Requires MBeanPermission in security policy.
            server = AccessController.doPrivileged(new PrivilegedAction<TomcatServer>() {
                public TomcatServer run() {
                    return new TomcatServer(workScheduler);
                 }
            });        
            servletHosts.addServletHost(server);
        }
    }

    public void stop(ExtensionPointRegistry registry) {
        // Allow privileged access to stop MBeans. Requires MBeanPermission in security policy.
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                if (server != null) {
                    server.stop();
                }
                return null;
            }
        });            
    }
}
