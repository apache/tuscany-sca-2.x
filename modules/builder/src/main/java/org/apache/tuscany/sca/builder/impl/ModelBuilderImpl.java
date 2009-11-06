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
package org.apache.tuscany.sca.builder.impl;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;

public class ModelBuilderImpl implements CompositeBuilder {
    private ExtensionPointRegistry registry;
    
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private CompositeComponentTypeBuilderImpl compositeComponentTypeBuilder;
    private ComponentBuilderImpl componentBuilder;

    private CompositeBuilder structuralURIBuilder;
    private BindingURIBuilderImpl bindingURIBuilder;
    private ComponentServiceBindingBuilderImpl componentServiceBindingBuilder;
    private ComponentReferenceBindingBuilderImpl componentReferenceBindingBuilder;
    private EndpointBuilderImpl endpointBuilder;
    private EndpointReferenceBuilderImpl endpointReferenceBuilder;
    
    private CompositeBuilder policyAttachmentBuilder;
    private CompositePolicyBuilderImpl compositePolicyBuilder;

    /**
     * Constructs a new composite builder.
     * 
     * @param registry the extension point registry
     */
    public ModelBuilderImpl(ExtensionPointRegistry registry) {
        this.registry = registry;

        compositeIncludeBuilder = new CompositeIncludeBuilderImpl();
        compositeCloneBuilder = new CompositeCloneBuilderImpl();

        compositeComponentTypeBuilder = new CompositeComponentTypeBuilderImpl(registry);
        componentBuilder = new ComponentBuilderImpl(registry);

        compositeComponentTypeBuilder.setComponentBuilder(componentBuilder);
        componentBuilder.setComponentTypeBuilder(compositeComponentTypeBuilder);

        structuralURIBuilder = new StructuralURIBuilderImpl(registry);
        bindingURIBuilder = new BindingURIBuilderImpl(registry);
        componentServiceBindingBuilder = new ComponentServiceBindingBuilderImpl(registry);
        componentReferenceBindingBuilder = new ComponentReferenceBindingBuilderImpl(registry);
        endpointBuilder = new EndpointBuilderImpl(registry);
        endpointReferenceBuilder = new EndpointReferenceBuilderImpl(registry);

        policyAttachmentBuilder = new PolicyAttachmentBuilderImpl(registry);
        compositePolicyBuilder = new CompositePolicyBuilderImpl(registry);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeBuilder";
    }

    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        Monitor monitor = context.getMonitor();
        try {
            // Clone the composites that are included or referenced in implementation.composite
            composite = compositeCloneBuilder.build(composite, context);

            // Collect and fuse includes. Copy all of the components
            // out of the included composite into the including composite
            // and discards the included composite
            composite = compositeIncludeBuilder.build(composite, context);

            // Set up the structural URIs for components (services/references/bindings)
            // TODO does this need to happen before policy attachment
            composite = structuralURIBuilder.build(composite, context);
            
            // Apply policy external attachment. Happens before the composite type
            // is created so that suitable promotion and structural processing is
            // applied to the attached policies
            composite = policyAttachmentBuilder.build(composite, context);

            // Process the implementation hierarchy by calculating the component type 
            // for the top level implementation (composite). This has the effect of
            // recursively calculating component types and configuring the 
            // components that depend on them
            compositeComponentTypeBuilder.createComponentType(null, composite, context);

            // Calculate the URI associated with service bindings
            composite = bindingURIBuilder.build(composite, context);
            
            // perform any binding specific build processing
            composite = componentServiceBindingBuilder.build(composite, context); // binding specific build
            composite = componentReferenceBindingBuilder.build(composite, context); // binding specific build

            // compute all the service endpoints
            endpointBuilder.build(composite, context);
            
            // compute all the reference endpoint references
            endpointReferenceBuilder.build(composite, context);
            
            // calculate intents and policy sets across the model hierarchy
            // relies on the endpoints and endpoint references having been calculated
            composite = compositePolicyBuilder.build(composite, context); 
            
            // For debugging - in success cases
            //System.out.println(dumpBuiltComposite(composite));
            
            return composite;
        } catch (Exception e) {
            // For debugging - in failure cases
            //System.out.println(dumpBuiltComposite(composite));
            throw new CompositeBuilderException("Exception while building model " + composite.getName(), e);
        }
    }
    
    /**
     * For debugging the build process
     * 
     * @return a tring version of the built model 
     */
    public String dumpBuiltComposite(Composite composite) {
        
        StAXArtifactProcessorExtensionPoint xmlProcessors = 
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Composite>  compositeProcessor = 
            xmlProcessors.getProcessor(Composite.class);   
     
        return writeComposite(composite, compositeProcessor);
    }
       
    private String writeComposite(Composite composite, StAXArtifactProcessor<Composite> compositeProcessor){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory =
            registry.getExtensionPoint(FactoryExtensionPoint.class)
                .getFactory(XMLOutputFactory.class);
        
        try {
            compositeProcessor.write(composite, outputFactory.createXMLStreamWriter(bos), new ProcessorContext(registry));
        } catch(Exception ex) {
            return ex.toString();
        }
        
        String result = bos.toString();
        
        // write out and nested composites
        for (Component component : composite.getComponents()) {
            if (component.getImplementation() instanceof Composite) {
                result += "\n<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->\n" + 
                          writeComposite((Composite)component.getImplementation(),
                                          compositeProcessor);
            }
        }
        
        return result;
    }    
}
