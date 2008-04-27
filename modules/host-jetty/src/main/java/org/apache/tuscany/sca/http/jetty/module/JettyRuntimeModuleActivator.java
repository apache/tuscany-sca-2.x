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

package org.apache.tuscany.sca.http.jetty.module;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class JettyRuntimeModuleActivator implements ModuleActivator {

    private JettyServer server;

    public void start(ExtensionPointRegistry extensionPointRegistry) {

        // Register a Jetty Servlet host
        ServletHostExtensionPoint servletHosts =
            extensionPointRegistry.getExtensionPoint(ServletHostExtensionPoint.class);
        
        if (servletHosts.getServletHosts().size() < 1) {
            final WorkScheduler workScheduler = extensionPointRegistry.getExtensionPoint(WorkScheduler.class);
            // Allow privileged access to start MBeans. Requires MBeanPermission in security policy.
            server = AccessController.doPrivileged(new PrivilegedAction<JettyServer>() {
                public JettyServer run() {
                    return new JettyServer(workScheduler);
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
