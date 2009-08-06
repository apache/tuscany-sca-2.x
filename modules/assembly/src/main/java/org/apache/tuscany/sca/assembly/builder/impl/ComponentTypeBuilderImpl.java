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

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.ComponentPreProcessor;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeBuilderImpl {
    private static final Logger logger = Logger.getLogger(ComponentTypeBuilderImpl.class.getName());
    
    private ComponentBuilderImpl componentBuilder;
    private Monitor monitor;

    public ComponentTypeBuilderImpl(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public void setComponentBuilder(ComponentBuilderImpl componentBuilder){
        this.componentBuilder = componentBuilder;
    }

    public ComponentType createComponentType(Implementation implementation){
        if (!(implementation instanceof Composite)){
            // component type will have been calculated at resolve time
            return implementation;
        }
        
        // create the composite component type as this was not
        // calculated at resolve time
        Composite composite = (Composite)implementation;
        
        // make sure that the component has been properly configured based
        // on its component type
        for (Component component : composite.getComponents()) {
            
            // Check for duplicate component names
            if (composite.getComponent(component.getName()) == null) {
                Monitor.error(monitor, 
                              this, 
                              "assembly-validation-messages", 
                              "DuplicateComponentName", 
                              composite.getName().toString(),
                              component.getName());
            } 
            
            // Propagate the autowire flag from the composite to components
            // Should this be later?
            if (component.getAutowire() == null) {
                component.setAutowire(composite.getAutowire());
            }
            
            // do any require pre-processing on the implementation
            // what does this do?
            if (component.getImplementation() instanceof ComponentPreProcessor) {
                ((ComponentPreProcessor)component.getImplementation()).preProcess(component);
            }
            
            // services
            calculateServices(composite, component);
            
            // references
            //calculateReferences(composite, component);
            
            // properties
            //calculateProperties(composite, component);
        }
        
        // create the composite component type based on the promoted artifacts
        // from the components that it contains
        
        return composite;
    }
    
    private void calculateServices(ComponentType componentType, Component component){
        for (ComponentService componentService : component.getServices()) {
            // need to propagate
            //   bindings
            //   interface contracts
            //   intents
            //   policy sets
            // based on OASIS rules
        }
    }

} //end class
