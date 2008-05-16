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
package org.apace.tuscany.sca.binding.sca;


import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.endpoint.impl.EndpointProviderFactoryImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.EndpointProviderFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * EndpointTestCase
 *
 * @version $Rev$ $Date$
 */
public class EndpointTestCase {
	
    private static URLArtifactProcessor<Contribution> contributionProcessor;
    private static ModelResolverExtensionPoint modelResolvers;
    private static ModelFactoryExtensionPoint modelFactories;
    private static AssemblyFactory assemblyFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> xmlProcessor; 
    private static CompositeBuilder compositeBuilder;
    private static ModelResolver modelResolver;
    private static CompositeActivator compositeActivator;
    private static ExtensionPointRegistry extensionPoints;

    @BeforeClass
    public static void init() {
        
        // Create extension point registry 
        extensionPoints = new DefaultExtensionPointRegistry();
        
        modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
                
        // Initialize the Tuscany module activators
        ModuleActivatorExtensionPoint moduleActivators = extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator activator: moduleActivators.getModuleActivators()) {
            activator.start(extensionPoints);
        }

        // Get XML input/output factories

        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Get contribution workspace and assembly model factories
        assemblyFactory = new RuntimeAssemblyFactory();
        modelFactories.addFactory(assemblyFactory);
        
        // Create XML artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory);
        
        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);
        
        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        modelResolver = new ExtensibleModelResolver(null, modelResolvers, modelFactories);
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
               
        // Create a composite builder
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        InterfaceContractMapper contractMapper = utilities.getUtility(InterfaceContractMapper.class);
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, contractMapper, monitor);
        
        // Runtime Init ===================
/*        
        ContextFactoryExtensionPoint contextFactories = new DefaultContextFactoryExtensionPoint(extensionPoints);
        extensionPoints.addExtensionPoint(contextFactories);
        
        // Create a wire post processor extension point
        RuntimeWireProcessorExtensionPoint wireProcessors =
            extensionPoints.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        RuntimeWireProcessor wireProcessor = new ExtensibleWireProcessor(wireProcessors);

        JavaInterfaceFactory javaInterfaceFactory =
            extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class).getFactory(JavaInterfaceFactory.class);
        RequestContextFactory requestContextFactory =
            extensionPoints.getExtensionPoint(ContextFactoryExtensionPoint.class).getFactory(RequestContextFactory.class);

        ConversationManager conversationManager = new ConversationManagerImpl();
        extensionPoints.addExtensionPoint(conversationManager); 
        
        
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        ScopeContainerFactory[] factories =
            new ScopeContainerFactory[] {new CompositeScopeContainerFactory(), new StatelessScopeContainerFactory(),
                                         new RequestScopeContainerFactory(),
                                         new ConversationalScopeContainerFactory(null),
            // new HttpSessionScopeContainer(monitor)
            };
        for (ScopeContainerFactory f : factories) {
            scopeRegistry.register(f);
        }        
        
        compositeActivator =new CompositeActivatorImpl(assemblyFactory, 
                                                       messageFactory, 
                                                       javaInterfaceFactory, 
                                                       scaBindingFactory,
                                                       mapper, 
                                                       scopeRegistry, 
                                                       extensionPoints.getExtensionPoint(WorkScheduler.class), 
                                                       wireProcessor, 
                                                       requestContextFactory,
                                                       new DefaultProxyFactoryExtensionPoint(messageFactory, mapper), 
                                                       extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class), 
                                                       extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class), 
                                                       conversationManager);
                                                       
*/                                                       

        
    }
    
    @Test
    public void testProvider(){
        try {
            URI calculatorURI = URI.create("calcualtor");
            URL calculatorURL = new File("./target/test-classes").toURI().toURL();
            Contribution contribution = contributionProcessor.read(null, calculatorURI, calculatorURL);
           
            contributionProcessor.resolve(contribution, modelResolver);
            
            Composite composite = contribution.getDeployables().get(0);
            
            compositeBuilder.build(composite);
            
            ComponentReference ref = (composite.getComponents().get(0).getReferences().get(0));
            
            Assert.assertEquals(1, ref.getEndpoints().size());
            
            Endpoint endpoint = ref.getEndpoints().get(0);
            
            EndpointProviderFactory factory = new EndpointProviderFactoryImpl(extensionPoints);
            
            EndpointProvider endpointProvider = factory.createEndpointProvider(endpoint);
            
            Assert.assertNotNull(endpointProvider);
              
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
            Assert.fail();
        }
    }
    
  
}
