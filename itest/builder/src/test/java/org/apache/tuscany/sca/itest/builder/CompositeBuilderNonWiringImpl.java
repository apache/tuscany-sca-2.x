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
import org.apache.tuscany.sca.definitions.SCADefinitions;
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
  //private CompositeBuilder componentReferenceWireBuilder;
  //private CompositeBuilder componentReferencePromotionWireBuilder;
  //private CompositeBuilder compositeReferenceWireBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private CompositeBuilder componentConfigurationBuilder;
    private CompositeBuilder compositeServiceConfigurationBuilder;
    private CompositeBuilder compositeReferenceConfigurationBuilder;
    private CompositeBuilder compositeBindingURIBuilder;
    private CompositeBuilder componentServicePromotionBuilder;
  //private CompositeBuilder compositeServicePromotionBuilder;
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
                                InterfaceContractMapper interfaceContractMapper,
                                Monitor monitor) {
        this(assemblyFactory, null, scaBindingFactory,  intentAttachPointTypeFactory, interfaceContractMapper, null, monitor);
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
                                InterfaceContractMapper interfaceContractMapper,
                                SCADefinitions policyDefinitions,
                                Monitor monitor) {
        
        if (endpointFactory == null){
            endpointFactory = new DefaultEndpointFactory();
        }       
        
        compositeIncludeBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeIncludeBuilderImpl(monitor); 
      //componentReferenceWireBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentReferenceWireBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper, monitor);
      //componentReferencePromotionWireBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentReferencePromotionWireBuilderImpl(assemblyFactory, endpointFactory, monitor);
      //compositeReferenceWireBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeReferenceWireBuilderImpl(assemblyFactory, endpointFactory, monitor);
        compositeCloneBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeCloneBuilderImpl(monitor);
        componentConfigurationBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, policyDefinitions, monitor);
        compositeServiceConfigurationBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeServiceConfigurationBuilderImpl(assemblyFactory);
        compositeReferenceConfigurationBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeReferenceConfigurationBuilderImpl(assemblyFactory);
        compositeBindingURIBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeBindingURIBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, policyDefinitions, monitor);
      //componentServicePromotionBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentServicePromotionBuilderImpl(assemblyFactory);
      //compositeServicePromotionBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositeServicePromotionBuilderImpl(assemblyFactory);
        compositePromotionBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositePromotionBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper, monitor);
        compositePolicyBuilder = new org.apache.tuscany.sca.assembly.builder.impl.CompositePolicyBuilderImpl(assemblyFactory, endpointFactory, interfaceContractMapper, monitor);
        componentServiceBindingBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentServiceBindingBuilderImpl(monitor);
        componentReferenceBindingBuilder = new org.apache.tuscany.sca.assembly.builder.impl.ComponentReferenceBindingBuilderImpl(monitor);
    }

    public void build(Composite composite) throws CompositeBuilderException {

        // Collect and fuse includes
        compositeIncludeBuilder.build(composite);

        // Expand nested composites
        compositeCloneBuilder.build(composite);

        // Configure all components
        componentConfigurationBuilder.build(composite);

        // Connect composite services/references to promoted services/references
        compositePromotionBuilder.build(composite);
        
        // Compute the policies across the model hierarchy
        compositePolicyBuilder.build(composite);
        
        // Configure composite services
        compositeServiceConfigurationBuilder.build(composite);
        
        // Configure composite references
        compositeReferenceConfigurationBuilder.build(composite);

        // Configure binding URIs
        compositeBindingURIBuilder.build(composite);

        // Create promoted component services
//      componentServicePromotionBuilder.build(composite);

        // Create promoted composite services
//      compositeServicePromotionBuilder.build(composite);

        // Build component service binding-related information
        componentServiceBindingBuilder.build(composite);
        
        // Wire the components
//      componentReferenceWireBuilder.build(composite);

        // Wire the promoted component references
//      componentReferencePromotionWireBuilder.build(composite);

        // Wire the composite references
//      compositeReferenceWireBuilder.build(composite);

        // Build component reference binding-related information
        componentReferenceBindingBuilder.build(composite);
        
        // Fuse nested composites
        //FIXME do this later
        //cloneBuilder.fuseCompositeImplementations(composite);
    }

}
