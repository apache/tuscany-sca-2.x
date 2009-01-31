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
package org.apache.tuscany.sca.itest.builder;

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultEndpointFactory;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * Test version of org.apache.tuscany.sca.assembly.builder.impl.builder.CompositeBuilderImpl
 *
 * This class should be identical to CompositeBuilderImpl except for omitting the
 * following builders:
 *  componentWireBuilder
 *  compositeReferenceWireBuilder
 *  compositePromotedServiceBuilder
 * It omits the component wiring step and the special processing that's performed
 * for composite service and reference promotion.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeBuilderNonWiringImpl implements CompositeBuilder {
    private static final Logger logger = Logger.getLogger(CompositeBuilderNonWiringImpl.class.getName());
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private CompositeBuilder componentConfigurationBuilder;
    private CompositeBuilder compositeServiceConfigurationBuilder;
    private CompositeBuilder compositeReferenceConfigurationBuilder;
    private CompositeBuilder compositeBindingURIBuilder;
    private CompositeBuilder componentServicePromotionBuilder;
    private CompositeBuilder compositePromotionBuilder;
    private CompositeBuilder compositePolicyBuilder;
    private CompositeBuilder componentServiceBindingBuilder;
    private CompositeBuilder componentReferenceBindingBuilder;
    
    /**
     * Constructs a new composite builder.
     * 
     * @param assemblyFactory
     * @param scaBindingFactory
     * @param intentAttachPointTypeFactory
     * @param interfaceContractMapper
     * @param monitor
     */
    public CompositeBuilderNonWiringImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper) {
        this(assemblyFactory, null, scaBindingFactory,  intentAttachPointTypeFactory, interfaceContractMapper);
    }
    
    /**
     * Constructs a new composite builder.
     * 
     * @param assemblyFactory
     * @param scaBindingFactory
     * @param endpointFactory
     * @param intentAttachPointTypeFactory
     * @param interfaceContractMapper
     * @param policyDefinitions
     * @param monitor
     */
    public CompositeBuilderNonWiringImpl(AssemblyFactory assemblyFactory,
                                EndpointFactory endpointFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper) {
        
        if (endpointFactory == null){
            endpointFactory = new DefaultEndpointFactory();
        }       
        
        compositeIncludeBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeIncludeBuilderImpl(); 
        compositeCloneBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeCloneBuilderImpl();
        componentConfigurationBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper);
        compositeServiceConfigurationBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeServiceConfigurationBuilderImpl(assemblyFactory);
        compositeReferenceConfigurationBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeReferenceConfigurationBuilderImpl(assemblyFactory);
        compositeBindingURIBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeBindingURIBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper);
        compositePromotionBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositePromotionBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper);
        compositePolicyBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositePolicyBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper);
        componentServiceBindingBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentServiceBindingBuilderImpl();
        componentReferenceBindingBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentReferenceBindingBuilderImpl();
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeBuilderNonWiring";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {

        // Collect and fuse includes
        compositeIncludeBuilder.build(composite, definitions, monitor);

        // Expand nested composites
        compositeCloneBuilder.build(composite, definitions, monitor);

        // Configure all components
        componentConfigurationBuilder.build(composite, definitions, monitor);

        // Connect composite services/references to promoted services/references
        compositePromotionBuilder.build(composite, definitions, monitor);
        
        // Compute the policies across the model hierarchy
        compositePolicyBuilder.build(composite, definitions, monitor);
        
        // Configure composite services
        compositeServiceConfigurationBuilder.build(composite, definitions, monitor);
        
        // Configure composite references
        compositeReferenceConfigurationBuilder.build(composite, definitions, monitor);

        // Configure binding URIs
        compositeBindingURIBuilder.build(composite, definitions, monitor);

        // Build component service binding-related information
        componentServiceBindingBuilder.build(composite, definitions, monitor);
        
        // Build component reference binding-related information
        componentReferenceBindingBuilder.build(composite, definitions, monitor);
        
        // Fuse nested composites
        //FIXME do this later
        //cloneBuilder.fuseCompositeImplementations(composite);
    }

}
