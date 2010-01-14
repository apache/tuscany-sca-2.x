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

package org.apache.tuscany.sca.client.impl;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.oasisopen.sca.client.SCAClientFactory;

public class SCAClientModuleActivator implements ModuleActivator {
    
    private ExtensionPointRegistry registry;

    public SCAClientModuleActivator(ExtensionPointRegistry registry) {
        this.registry = registry;
    }
    
    public void start() { 
        // look up the SCAClientFactory. We could instantiate one directly here but 
        // looking it up brings the META-INF/services mechanism into play
        SCAClientFactory clientFactory = registry.getExtensionPoint(SCAClientFactory.class);  
        
        // inject this client factory as the default client factory
        // so that the SCAClientFactory interface doesn't have to use the
        // finder to look for it.
        // TODO - handle multiple domains
        SCAClientFactoryImpl.setDefaultClientFactory(clientFactory);
    }

    public void stop() {

    }
}
