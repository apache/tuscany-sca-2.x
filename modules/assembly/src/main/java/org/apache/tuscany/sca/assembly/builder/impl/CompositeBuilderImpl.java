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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.DeployedCompositeBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A builder that handles the configuration of the components inside a composite
 * and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeBuilderImpl implements CompositeBuilder, DeployedCompositeBuilder {
    private static final Logger logger = Logger.getLogger(CompositeBuilderImpl.class.getName());
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder componentReferenceWireBuilder;
    //private CompositeBuilder componentReferencePromotionWireBuilder;
    private CompositeBuilder componentReferencePromotionBuilder;
    //private CompositeBuilder compositeReferenceWireBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private CompositeBuilder componentConfigurationBuilder;
    private CompositeBuilder compositeServiceConfigurationBuilder;
    private CompositeBuilder compositeReferenceConfigurationBuilder;
    private CompositeBuilder compositeBindingURIBuilder;
    //private CompositeBuilder componentServicePromotionBuilder;
    //private CompositeBuilder compositeServicePromotionBuilder;
    private CompositeBuilder compositePromotionBuilder;
    private CompositeBuilder compositePolicyBuilder;
    private CompositeBuilder componentServiceBindingBuilder;
    private CompositeBuilder componentReferenceBindingBuilder;

    private CompositeBuilder componentReferenceEndpointReferenceBuilder;
    private CompositeBuilder componentServiceEndpointBuilder;

    private BuilderExtensionPoint builders;
    

    /**
     * Constructs a new composite builder.
     *
     *  @param registry
     */
    public CompositeBuilderImpl(ExtensionPointRegistry registry) {
        
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
 
        compositeIncludeBuilder = new CompositeIncludeBuilderImpl();
        componentReferenceWireBuilder = new ComponentReferenceWireBuilderImpl(registry);
        //componentReferencePromotionWireBuilder = new ComponentReferencePromotionWireBuilderImpl(assemblyFactory, endpointFactory);
        componentReferencePromotionBuilder = new ComponentReferencePromotionBuilderImpl(registry);
        //compositeReferenceWireBuilder = new CompositeReferenceWireBuilderImpl(assemblyFactory, endpointFactory);
        compositeCloneBuilder = new CompositeCloneBuilderImpl();
        componentConfigurationBuilder =
            new ComponentConfigurationBuilderImpl(registry);
        compositeServiceConfigurationBuilder = new CompositeServiceConfigurationBuilderImpl(registry);
        compositeReferenceConfigurationBuilder = new CompositeReferenceConfigurationBuilderImpl(registry);
        compositeBindingURIBuilder =
            new CompositeBindingURIBuilderImpl(registry);
        //componentServicePromotionBuilder = new ComponentServicePromotionBuilderImpl(assemblyFactory);
        //compositeServicePromotionBuilder = new CompositeServicePromotionBuilderImpl(assemblyFactory);
        compositePromotionBuilder = new CompositePromotionBuilderImpl(registry);
        compositePolicyBuilder = new CompositePolicyBuilderImpl(registry);
        componentServiceBindingBuilder = new ComponentServiceBindingBuilderImpl(registry);
        componentReferenceBindingBuilder = new ComponentReferenceBindingBuilderImpl(registry);

        componentReferenceEndpointReferenceBuilder =
            new ComponentReferenceEndpointReferenceBuilderImpl(registry);
        componentServiceEndpointBuilder = new ComponentServiceEndpointBuilderImpl(assemblyFactory);
        //endpointReferenceBuilder = new EndpointReference2BuilderImpl(assemblyFactory, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        build(composite, definitions, null, monitor);
    }

    public void build(Composite composite,
                      Definitions definitions,
                      Map<QName, List<String>> bindingBaseURIs,
                      Monitor monitor) throws CompositeBuilderException {

        try {
    		
            // Collect and fuse includes
            compositeIncludeBuilder.build(composite, definitions, monitor);

            // Expand nested composites
            compositeCloneBuilder.build(composite, definitions, monitor);

            // Configure all components
            componentConfigurationBuilder.build(composite, definitions, monitor);

            // Connect composite services/references to promoted services/references
            compositePromotionBuilder.build(composite, definitions, monitor);

            // Configure composite services by copying bindings up the promotion
            // hierarchy overwriting automatic bindings with those added manually
            compositeServiceConfigurationBuilder.build(composite, definitions, monitor);

            // Configure composite references by copying bindings down promotion
            // hierarchy overwriting automatic bindings with those added manually
            compositeReferenceConfigurationBuilder.build(composite, definitions, monitor);

            // Configure service binding URIs and names. Creates an SCA defined URI based
            // on the scheme base URI, the component name and the binding name
            ((DeployedCompositeBuilder)compositeBindingURIBuilder).build(composite, definitions, bindingBaseURIs, monitor);

            // Create $promoted$ component services on bottom level components
            // to represent promoted services
            // TODO - EPR replaced by endpoints on the promoted services
            //componentServicePromotionBuilder.build(composite, definitions, monitor);

            // Create $promoted$ component services on bottom level components
            // to represent promoted composite services
            // TODO - EPR OASIS doesn't deploy top level composite services
            //        if it did it would be replaced by endpoints
            //compositeServicePromotionBuilder.build(composite, definitions, monitor);

            // Perform and service binding related build activities. The binding
            // will provide the builder. 
            componentServiceBindingBuilder.build(composite, definitions, monitor);

            // create endpoints on component services. 
            componentServiceEndpointBuilder.build(composite, definitions, monitor);

            // Apply any wires in the composite to create new component reference targets
            componentReferenceWireBuilder.build(composite, definitions, monitor);

            // create reference endpoint reference models
            componentReferenceEndpointReferenceBuilder.build(composite, definitions, monitor);

            // Push down configuration from promoted references to the 
            // references they promote
            componentReferencePromotionBuilder.build(composite, definitions, monitor);

            // Push down configuration from promoted references to the 
            // references they promote
            // TODO - EPR Seems to be a repeat of compositeReferenceConfigurationBuilder
            // componentReferencePromotionWireBuilder.build(composite, definitions, monitor);

            // Wire the composite references
            // TODO - EPR OASIS doesn't deploy top level composite references
            // compositeReferenceWireBuilder.build(composite, definitions, monitor);

            // Perform and reference binding related build activities. The binding
            // will provide the builder.
            componentReferenceBindingBuilder.build(composite, definitions, monitor);

            // Compute the policies across the model hierarchy
            compositePolicyBuilder.build(composite, definitions, monitor);
        } catch (Exception e) {
            throw new CompositeBuilderException("Exception while building composite " + composite.getName(), e);
    	} // end try

    } // end method build

} //end class
