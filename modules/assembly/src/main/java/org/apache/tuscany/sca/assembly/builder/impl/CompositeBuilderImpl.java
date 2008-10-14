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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultEndpointFactory;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * A builder that handles the configuration of the components inside a composite
 * and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeBuilderImpl implements CompositeBuilder {
    private static final Logger logger = Logger.getLogger(CompositeBuilderImpl.class.getName());
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder componentReferenceWireBuilder;
    private CompositeBuilder componentReferencePromotionWireBuilder;
    private CompositeBuilder compositeReferenceWireBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private CompositeBuilder componentConfigurationBuilder;
    private CompositeBuilder compositeServiceConfigurationBuilder;
    private CompositeBuilder compositeReferenceConfigurationBuilder;
    private CompositeBuilder compositeBindingURIBuilder;
    private CompositeBuilder componentServicePromotionBuilder;
    private CompositeBuilder compositeServicePromotionBuilder;
    private CompositeBuilder compositePromotionBuilder;
    private CompositeBuilder compositePolicyBuilder;
    private CompositeBuilder componentServiceBindingBuilder;
    private CompositeBuilder componentReferenceBindingBuilder;
    
    public CompositeBuilderImpl(FactoryExtensionPoint factories, InterfaceContractMapper mapper) {
        this(factories.getFactory(AssemblyFactory.class),
               factories.getFactory(EndpointFactory.class),
               factories.getFactory(SCABindingFactory.class),
               factories.getFactory(IntentAttachPointTypeFactory.class),
               factories.getFactory(DocumentBuilderFactory.class),
               factories.getFactory(TransformerFactory.class),
               mapper);
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
    @Deprecated
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                EndpointFactory endpointFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper) {
        this(assemblyFactory, endpointFactory, scaBindingFactory, intentAttachPointTypeFactory,
             null, null, interfaceContractMapper);
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
    @Deprecated
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper) {
        this(assemblyFactory, null, scaBindingFactory, intentAttachPointTypeFactory,
             null, null, interfaceContractMapper);
    }
        
    /**
     * Constructs a new composite builder.
     * 
     * @param assemblyFactory
     * @param scaBindingFactory
     * @param intentAttachPointTypeFactory
     * @param documentBuilderFactory
     * @param transformerFactory
     * @param interfaceContractMapper
     * @param monitor
     */
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                DocumentBuilderFactory documentBuilderFactory,
                                TransformerFactory transformerFactory,
                                InterfaceContractMapper interfaceContractMapper) {
        this(assemblyFactory, null, scaBindingFactory,  intentAttachPointTypeFactory,
             documentBuilderFactory, transformerFactory, interfaceContractMapper);
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
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                EndpointFactory endpointFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                DocumentBuilderFactory documentBuilderFactory,
                                TransformerFactory transformerFactory,
                                InterfaceContractMapper interfaceContractMapper) {
        
        if (endpointFactory == null){
            endpointFactory = new DefaultEndpointFactory();
        }       
        
        compositeIncludeBuilder = new CompositeIncludeBuilderImpl(); 
        componentReferenceWireBuilder = new ComponentReferenceWireBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper);
        componentReferencePromotionWireBuilder = new ComponentReferencePromotionWireBuilderImpl(assemblyFactory, endpointFactory);
        compositeReferenceWireBuilder = new CompositeReferenceWireBuilderImpl(assemblyFactory, endpointFactory);
        compositeCloneBuilder = new CompositeCloneBuilderImpl();
        componentConfigurationBuilder = new ComponentConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, documentBuilderFactory, transformerFactory, interfaceContractMapper);
        compositeServiceConfigurationBuilder = new CompositeServiceConfigurationBuilderImpl(assemblyFactory);
        compositeReferenceConfigurationBuilder = new CompositeReferenceConfigurationBuilderImpl(assemblyFactory);
        compositeBindingURIBuilder = new CompositeBindingURIBuilderImpl(assemblyFactory, scaBindingFactory, documentBuilderFactory, transformerFactory, interfaceContractMapper);
        componentServicePromotionBuilder = new ComponentServicePromotionBuilderImpl(assemblyFactory);
        compositeServicePromotionBuilder = new CompositeServicePromotionBuilderImpl(assemblyFactory);
        compositePromotionBuilder = new CompositePromotionBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper);
        compositePolicyBuilder = new CompositePolicyBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper);
        componentServiceBindingBuilder = new ComponentServiceBindingBuilderImpl();
        componentReferenceBindingBuilder = new ComponentReferenceBindingBuilderImpl();
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeBuilder";
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

        // Create promoted component services
        componentServicePromotionBuilder.build(composite, definitions, monitor);

        // Create promoted composite services
        compositeServicePromotionBuilder.build(composite, definitions, monitor);

        // Build component service binding-related information
        componentServiceBindingBuilder.build(composite, definitions, monitor);

        // Wire the components
        componentReferenceWireBuilder.build(composite, definitions, monitor);

        // Wire the promoted component references
        componentReferencePromotionWireBuilder.build(composite, definitions, monitor);

        // Wire the composite references
        compositeReferenceWireBuilder.build(composite, definitions, monitor);

        // Build component reference binding-related information
        componentReferenceBindingBuilder.build(composite, definitions, monitor);

        // Fuse nested composites
        //FIXME do this later
        //cloneBuilder.fuseCompositeImplementations(composite);
    }

}
