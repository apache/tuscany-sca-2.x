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

package org.apache.tuscany.sca.assembly.builder.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;

/**
 * A composite builder that handles the creation of promoted composite services.
 *
 * @version $Rev$ $Date$
 */
public class CompositeServicePromotionBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    public CompositeServicePromotionBuilderImpl(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public void build(Composite composite) throws CompositeBuilderException {

        // Process top level composite services
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;

            // Get the innermost promoted service
            ComponentService promotedService = ServiceConfigurationUtil.getPromotedComponentService(compositeService);
            if (promotedService != null) {
                Component promotedComponent = ServiceConfigurationUtil.getPromotedComponent(compositeService);

                // Create a new component service to represent this composite
                // service on the promoted component
                ComponentService newComponentService = assemblyFactory.createComponentService();
                newComponentService.setName("$promoted$." + compositeService.getName());
                promotedComponent.getServices().add(newComponentService);
                newComponentService.setService(promotedService.getService());
                newComponentService.getBindings().addAll(compositeService.getBindings());
                newComponentService.setInterfaceContract(compositeService.getInterfaceContract());
                if (compositeService.getInterfaceContract() != null &&
                    compositeService.getInterfaceContract().getCallbackInterface() != null) {
                    newComponentService.setCallback(assemblyFactory.createCallback());
                    newComponentService.getCallback().getBindings()
                            .addAll(compositeService.getCallback().getBindings());
                }

                // Change the composite service to now promote the newly
                // created component service directly
                compositeService.setPromotedComponent(promotedComponent);
                compositeService.setPromotedService(newComponentService);
            }
        }
    }

}
