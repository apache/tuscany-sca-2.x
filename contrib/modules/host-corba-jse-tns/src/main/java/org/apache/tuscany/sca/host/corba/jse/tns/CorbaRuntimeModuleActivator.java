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
package org.apache.tuscany.sca.host.corba.jse.tns;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.host.corba.CorbaHostExtensionPoint;
import org.apache.tuscany.sca.host.corba.jse.DefaultCorbaHost;

/**
 * @version $Rev: 683329 $ $Date: 2008-08-06 19:06:39 +0200 (śro, 06 sie 2008) $
 */
public class CorbaRuntimeModuleActivator implements ModuleActivator {
    // private static final Logger logger =
    // Logger.getLogger(CorbaRuntimeModuleActivator.class.getName());

    private TnsDefaultCorbaHost server;

    public void start(ExtensionPointRegistry extensionPointRegistry) {       
        // Register our Corba host
        CorbaHostExtensionPoint corbaHosts = extensionPointRegistry.getExtensionPoint(CorbaHostExtensionPoint.class);
        server = new TnsDefaultCorbaHost();
        if (corbaHosts.getCorbaHosts().size() > 0 && corbaHosts.getCorbaHosts().get(0) instanceof DefaultCorbaHost) {
            // Set TnsDefaultCorbaHost as default when DefaultCorbaHost was registered before
            corbaHosts.getCorbaHosts().add(0, server);     
        } else {
            corbaHosts.getCorbaHosts().add(server);
        }
    }

    public void stop(ExtensionPointRegistry registry) {
        if (server != null) {
            server.stop();
        }
    }

}
