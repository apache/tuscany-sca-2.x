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
package org.apache.tuscany.sca.core.spring.context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.core.spring.assembly.impl.BeanAssemblyFactory;
import org.apache.tuscany.sca.core.spring.implementation.java.impl.BeanJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.AllowsPassByReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ComponentNameProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ContextProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConversationProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.DestroyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.EagerInitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.HeuristicPojoProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.InitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PolicyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ScopeProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.sca.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.xml.JavaInterfaceProcessor;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * A mini test runtime that uses the SCA assembly model variant implementation
 * backed by Spring bean definitions.
 * 
 * @version $Rev$ $Date$
 */
public class SCADomainContext {

    private DefaultListableBeanFactory beanFactory;
    
    public SCADomainContext(String... compositeFiles) {

        // Create Spring bean factory
        beanFactory = new DefaultListableBeanFactory();

        // Create SCA assembly and SCA Java factories
        ModelFactoryExtensionPoint modelFactories = new DefaultModelFactoryExtensionPoint();
        AssemblyFactory assemblyFactory = new BeanAssemblyFactory(new DefaultAssemblyFactory(), beanFactory);
        modelFactories.addFactory(assemblyFactory);
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        modelFactories.addFactory(policyFactory);
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        InterfaceContractMapper interfaceContractMapper = new InterfaceContractMapperImpl();
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        modelFactories.addFactory(javaFactory);
        JavaImplementationFactory javaImplementationFactory = new BeanJavaImplementationFactory(beanFactory);
        modelFactories.addFactory(javaImplementationFactory);
        
        BaseJavaClassVisitor[] extensions = new BaseJavaClassVisitor[] {
            new ConstructorProcessor(assemblyFactory),
            new AllowsPassByReferenceProcessor(assemblyFactory),
            new ComponentNameProcessor(assemblyFactory),
            new ContextProcessor(assemblyFactory),
            new ConversationProcessor(assemblyFactory),
            new DestroyProcessor(assemblyFactory),
            new EagerInitProcessor(assemblyFactory),
            new InitProcessor(assemblyFactory),
            new PropertyProcessor(assemblyFactory),
            new ReferenceProcessor(assemblyFactory, javaFactory),
            new ResourceProcessor(assemblyFactory),
            new ScopeProcessor(assemblyFactory),
            new ServiceProcessor(assemblyFactory, javaFactory),
            new HeuristicPojoProcessor(assemblyFactory, javaFactory),
            new PolicyProcessor(assemblyFactory, policyFactory)
        };
        for (JavaClassVisitor e : extensions) {
            javaImplementationFactory.addClassVisitor(e);
        }

        // Populate ArtifactProcessor registry
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(modelFactories);
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        CompositeProcessor compositeProcessor = new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory,
                                                                       interfaceContractMapper, staxProcessor);
        staxProcessors.addArtifactProcessor(compositeProcessor);
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new JavaInterfaceProcessor(modelFactories));
        staxProcessors.addArtifactProcessor(new JavaImplementationProcessor(modelFactories));
        
        // Create a resolver
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ModelResolverImpl resolver = new ModelResolverImpl(classLoader);

        try {
            
            // Read the composite files
            List<Composite> composites = new ArrayList<Composite>();
            for (String compositeFile: compositeFiles) {
                InputStream is = classLoader.getResourceAsStream(compositeFile);
                Composite composite = staxProcessor.read(is, Composite.class);
                resolver.addModel(composite);
                composites.add(composite);
            }
            
            for (Composite composite: composites) {
                
                // Resolve the composite
                compositeProcessor.resolve(composite, resolver);
            }
            
            // Wire the top level component's composite
            buildComposite(composites.get(0), assemblyFactory, scaBindingFactory, interfaceContractMapper);
            
        } catch (ContributionException e) {
            throw new RuntimeException(e);
        } catch (CompositeBuilderException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void buildComposite(Composite composite, AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory, InterfaceContractMapper interfaceContractMapper) throws CompositeBuilderException {

        CompositeBuilderMonitor monitor = new CompositeBuilderMonitor() {

            public void problem(Problem problem) {
                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.

                System.out.println("Composite assembly problem: " + problem.getMessage());
            }
        };

        // Configure and wire the composite
        CompositeBuilderImpl compositeUtil = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, monitor);
        compositeUtil.build(composite);

    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        Object bean = beanFactory.getBean(serviceName);
        return businessInterface.cast(bean);
    }

}
