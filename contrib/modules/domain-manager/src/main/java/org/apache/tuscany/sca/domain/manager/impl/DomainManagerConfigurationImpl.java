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

package org.apache.tuscany.sca.domain.manager.impl;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a domain manager configuration component.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(DomainManagerConfiguration.class)
public class DomainManagerConfigurationImpl implements DomainManagerConfiguration {

    private String rootDirectory = ".";
    private ExtensionPointRegistry extensionPoints;
    
    @Init
    public void initialize() {
        
        // Create extension point registry
        extensionPoints = new DefaultExtensionPointRegistry();

        // Initialize module activators
        ModuleActivatorExtensionPoint moduleActivators = extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator activator: moduleActivators.getModuleActivators()) {
            try {
                activator.start(extensionPoints);
            } catch (Exception e) {
                //FIXME fix the module activators that have cross module dependencies
                // and currently fail when the whole runtime is not present 
            }
        }
    }
    
    @Destroy
    public void destroy() {
        ModuleActivatorExtensionPoint moduleActivators = extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator activator: moduleActivators.getModuleActivators()) {
            try {
                activator.stop(extensionPoints);
            } catch (Exception e) {
                //FIXME fix the module activators that have cross module dependencies
                // and currently fail when the whole runtime is not present 
            }
        }
    }
    
    public String getRootDirectory() {
        return rootDirectory;
    }
    
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
    
    public ExtensionPointRegistry getExtensionPoints() {
        return extensionPoints;
    }
}
