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
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderTmp;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;

public class ModelBuilderImpl implements CompositeBuilder, CompositeBuilderTmp {
    private static final Logger logger = Logger.getLogger(ModelBuilderImpl.class.getName());
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private ComponentTypeBuilderImpl componentTypeBuilder;
    private ComponentBuilderImpl componentBuilder;

/*    
    private CompositeBuilder componentConfigurationBuilder;
    private CompositeBuilder compositePromotionBuilder;
    private CompositeBuilder componentReferenceWireBuilder;
    private CompositeBuilder componentReferencePromotionBuilder;
    private CompositeBuilder compositeServiceConfigurationBuilder;
    private CompositeBuilder compositeReferenceConfigurationBuilder;
    private CompositeBuilder compositeBindingURIBuilder;
    private CompositeBuilder compositePolicyBuilder;
    private CompositeBuilder componentServiceBindingBuilder;
    private CompositeBuilder componentReferenceBindingBuilder;
    private CompositeBuilder componentReferenceEndpointReferenceBuilder;
    private CompositeBuilder componentServiceEndpointBuilder;
*/    



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
    public ModelBuilderImpl(ExtensionPointRegistry registry) {
        
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        
        compositeIncludeBuilder = new CompositeIncludeBuilderImpl(assemblyFactory);
        compositeCloneBuilder = new CompositeCloneBuilderImpl();
        
        componentTypeBuilder = new ComponentTypeBuilderImpl(registry);
        componentBuilder = new ComponentBuilderImpl(registry);
        
        componentTypeBuilder.setComponentBuilder(componentBuilder);
        componentBuilder.setComponentTypeBuilder(componentTypeBuilder);
        
        
/*        
        compositePromotionBuilder = new CompositePromotionBuilderImpl(assemblyFactory, interfaceContractMapper);
        componentConfigurationBuilder =
            new ComponentConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, documentBuilderFactory,
                                                  transformerFactory, interfaceContractMapper);  
        componentReferenceWireBuilder = new ComponentReferenceWireBuilderImpl(assemblyFactory, interfaceContractMapper);
        componentReferencePromotionBuilder = new ComponentReferencePromotionBuilderImpl(assemblyFactory);
       
        compositeServiceConfigurationBuilder = new CompositeServiceConfigurationBuilderImpl(assemblyFactory);
        compositeReferenceConfigurationBuilder = new CompositeReferenceConfigurationBuilderImpl(assemblyFactory);
        compositeBindingURIBuilder =
            new CompositeBindingURIBuilderImpl(assemblyFactory, scaBindingFactory, documentBuilderFactory,
                                               transformerFactory, interfaceContractMapper);

        compositePolicyBuilder = new CompositePolicyBuilderImpl(assemblyFactory, interfaceContractMapper);
        componentServiceBindingBuilder = new ComponentServiceBindingBuilderImpl();
        componentReferenceBindingBuilder = new ComponentReferenceBindingBuilderImpl();

        componentReferenceEndpointReferenceBuilder =
            new ComponentReferenceEndpointReferenceBuilderImpl(assemblyFactory, interfaceContractMapper);
        componentServiceEndpointBuilder = new ComponentServiceEndpointBuilderImpl(assemblyFactory);
*/
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
            // Collect and fuse includes. Copy all of the components
            // out of the included composite into the including composite
            // and discards the included composite
            compositeIncludeBuilder.build(composite, definitions, monitor);
            
            // Expand nested composites. Clone any composite model that
            // is acting as a component implementation and connects the cloned
            // model to the component implementation in question
            // TODO - could this be deferred to after the static pass through
            //        the model is complete
            compositeCloneBuilder.build(composite, definitions, monitor);
            
            // create the static model by calculating the component type for the
            // top level implementation (composite). This has the effect of
            // recursively calculating component types and configuring the 
            // components that depend on them
            componentTypeBuilder.createComponentType(composite);

            // create the runtime model by updating the static model we have just 
            // created. This involves things like creating
            //  component URIs
            //  binding URIs
            //  Endpoints
            //  Endoint References
            
            

/*            
            // Configure all components. Created any derived model elements that
            // are required. Specifically
            //  Component name
            //  autowire flags
            //  callback references
            //  callback services
            //  default bindings
            componentConfigurationBuilder.build(composite, definitions, monitor);
            
            // Connect composite services/references to promoted services/references
            // so that subsequent processing can navigate down the hierarchy
            compositePromotionBuilder.build(composite, definitions, monitor);

            // calculate the component type for the composite that was passed in
            // this involves 
            

            // Configure composite services by copying bindings up the promotion
            // hierarchy overwriting automatic bindings with those added manually
            compositeServiceConfigurationBuilder.build(composite, definitions, monitor);

            // Configure composite references by copying bindings down promotion
            // hierarchy overwriting automatic bindings with those added manually
            compositeReferenceConfigurationBuilder.build(composite, definitions, monitor);

            // Configure service binding URIs and names. Creates an SCA defined URI based
            // on the scheme base URI, the component name and the binding name
            ((CompositeBuilderTmp)compositeBindingURIBuilder).build(composite, definitions, bindingBaseURIs, monitor);

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


            // Perform and reference binding related build activities. The binding
            // will provide the builder.
            componentReferenceBindingBuilder.build(composite, definitions, monitor);

            // Compute the policies across the model hierarchy
            compositePolicyBuilder.build(composite, definitions, monitor);
*/            
        } catch (Exception e) {
            throw new CompositeBuilderException("Exception while building model " + composite.getName(), e);
        } // end try

    } // end method build

} //end class
