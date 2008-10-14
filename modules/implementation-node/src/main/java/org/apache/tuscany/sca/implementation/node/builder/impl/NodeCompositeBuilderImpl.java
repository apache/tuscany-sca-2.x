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

package org.apache.tuscany.sca.implementation.node.builder.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that handles the configuration of composites assigned to
 * node components, from the default configuration from the node components.
 *
 * @version $Rev$ $Date$
 */
public class NodeCompositeBuilderImpl implements CompositeBuilder {
    
    private CompositeBuilder bindingConfigurationBuilder;
    
    public NodeCompositeBuilderImpl(CompositeBuilderExtensionPoint compositeBuilders, FactoryExtensionPoint factories, InterfaceContractMapper mapper) {
        bindingConfigurationBuilder = compositeBuilders.getCompositeBuilder("org.apache.tuscany.assembly.builder.CompositeBindingConfigurationBuilder");
    }
    
    @Deprecated
    public NodeCompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                    SCABindingFactory scaBindingFactory,
                                    InterfaceContractMapper interfaceContractMapper,
                                    CompositeBuilder bindingConfigurationBuilder) {
        this.bindingConfigurationBuilder = bindingConfigurationBuilder;
    }

    public NodeCompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                                  SCABindingFactory scaBindingFactory,
                                                  DocumentBuilderFactory documentBuilderFactory,
                                                  TransformerFactory transformerFactory,
                                                  InterfaceContractMapper interfaceContractMapper,
                                                  CompositeBuilder bindingConfigurationBuilder) {
        this.bindingConfigurationBuilder = bindingConfigurationBuilder;
    }

    public String getID() {
        return "org.apache.tuscany.sca.implementation.node.builder.NodeCompositeBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        
        // Process each node component in the given composite
        for (Component component: composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof NodeImplementation) {
                
                // Get the application composite assigned to the node
                NodeImplementation nodeImplementation = (NodeImplementation)implementation;
                Composite applicationComposite = nodeImplementation.getComposite();
                
                // Get the default bindings configured on the node
                List<Binding> defaultBindings = new ArrayList<Binding>();
                for (ComponentService componentService: component.getServices()) {
                    defaultBindings.addAll(componentService.getBindings());
                }

                // Configure services in the application composite assigned to
                // the node using the default bindings.
                applicationComposite.getExtensions().add(defaultBindings);
                bindingConfigurationBuilder.build(applicationComposite, definitions, monitor);
                applicationComposite.getExtensions().remove(defaultBindings);
            }
        }
        
    }
    
}
