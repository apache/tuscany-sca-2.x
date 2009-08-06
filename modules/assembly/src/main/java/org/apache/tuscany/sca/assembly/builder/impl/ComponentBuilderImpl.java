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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderTmp;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * 
 * @version $Rev$ $Date$
 */
public class ComponentBuilderImpl {
    private static final Logger logger = Logger.getLogger(ComponentBuilderImpl.class.getName());

    private ComponentTypeBuilderImpl componentTypeBuilder;
    private Monitor monitor;
    
    public ComponentBuilderImpl(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public void setComponentTypeBuilder(ComponentTypeBuilderImpl componentTypeBuilder){
        this.componentTypeBuilder = componentTypeBuilder;
    }

    public void configureComponentFromComponentType(Component component){
        ComponentType componentType = createComponentType(component);
        
        // services
        configureServices(component, componentType);
        
        // references
        //configureReferences(component, componentType);
        
        // properties
        //configureProperties(component, componentType);
        
    }
       
    private ComponentType createComponentType(Component component){
        Implementation implementation = component.getImplementation();
        ComponentType componentType = (ComponentType)implementation;
        if (implementation instanceof Composite) {
            componentType = componentTypeBuilder.createComponentType((Composite)implementation);
        }
        return componentType;
    }
    
    private void configureServices(Component component, ComponentType componentType){
        for (ComponentService componentService : component.getServices()) {
            if (componentService.getService() != null || componentService.isForCallback()) {
                continue;
            }
            
            Service service = componentType.getService(componentService.getName());
            
            if (service != null) {
                componentService.setService(service);
            } else {
                Monitor.error(monitor, 
                              this, 
                              "assembly-validation-messages", 
                              "ServiceNotFoundForComponentService",
                              component.getName(),
                              componentService.getName());
            }
            
            // need to propagate
            //   bindings
            //   interface contracts
            //   intents
            //   policy sets
            // based on OASIS rules
        }
    }
    
    // etc.

} //end class
