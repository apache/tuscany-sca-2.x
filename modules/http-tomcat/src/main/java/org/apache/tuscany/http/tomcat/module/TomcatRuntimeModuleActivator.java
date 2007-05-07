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

package org.apache.tuscany.http.tomcat.module;

import java.util.Map;

import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.http.ServletHostExtensionPoint;
import org.apache.tuscany.http.tomcat.TomcatServer;
import org.apache.tuscany.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class TomcatRuntimeModuleActivator implements ModuleActivator {

    private TomcatServer server;

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry extensionPointRegistry) {

        // Register a Tomcat servlet host
        ServletHostExtensionPoint servletHosts =
            extensionPointRegistry.getExtensionPoint(ServletHostExtensionPoint.class);
        WorkScheduler workScheduler = extensionPointRegistry.getExtensionPoint(WorkScheduler.class);
        server = new TomcatServer(workScheduler);
        server.init();
        servletHosts.addServletHost(server);
    }

    public void stop(ExtensionPointRegistry registry) {
        server.destroy();
    }

}
