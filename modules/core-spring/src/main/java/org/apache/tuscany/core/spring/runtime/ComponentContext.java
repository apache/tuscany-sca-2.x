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
package org.apache.tuscany.core.spring.runtime;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.assembly.builder.Problem;
import org.apache.tuscany.assembly.builder.impl.DefaultCompositeBuilder;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.core.spring.assembly.impl.BeanAssemblyFactory;
import org.apache.tuscany.core.spring.implementation.java.impl.BeanJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.introspect.DefaultJavaClassIntrospector;
import org.apache.tuscany.implementation.java.introspect.DefaultJavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.implementation.java.introspect.JavaClassIntrospector;
import org.apache.tuscany.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.implementation.java.introspect.JavaClassVisitor;
import org.apache.tuscany.implementation.java.introspect.impl.AllowsPassByReferenceProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.BaseJavaClassVisitor;
import org.apache.tuscany.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ContextProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ConversationProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.DestroyProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.EagerInitProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.HeuristicPojoProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.InitProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.PolicyProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ScopeProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.xml.JavaInterfaceProcessor;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * A mini test runtime that uses the SCA assembly model variant implementation
 * backed by Spring bean definitions.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentContext {

    private DefaultListableBeanFactory beanFactory;
    
    public ComponentContext(String... compositeFiles) {

        // Create Spring bean factory
        beanFactory = new DefaultListableBeanFactory();

        // Create SCA assembly and SCA Java factories
        AssemblyFactory assemblyFactory = new BeanAssemblyFactory(new DefaultAssemblyFactory(), beanFactory);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        InterfaceContractMapper interfaceContractMapper = new DefaultInterfaceContractMapper();
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorExtensionPoint interfaceVisitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
        JavaImplementationFactory javaImplementationFactory = new BeanJavaImplementationFactory(beanFactory);
        JavaClassIntrospectorExtensionPoint classVisitors = new DefaultJavaClassIntrospectorExtensionPoint();
        
        JavaInterfaceIntrospector interfaceIntrospector = new DefaultJavaInterfaceIntrospector(javaFactory, interfaceVisitors);
        
        BaseJavaClassVisitor[] extensions = new BaseJavaClassVisitor[] {
            new ConstructorProcessor(assemblyFactory),
            new AllowsPassByReferenceProcessor(assemblyFactory),
            new ContextProcessor(assemblyFactory),
            new ConversationProcessor(assemblyFactory),
            new DestroyProcessor(assemblyFactory),
            new EagerInitProcessor(assemblyFactory),
            new InitProcessor(assemblyFactory),
            new PropertyProcessor(assemblyFactory),
            new ReferenceProcessor(assemblyFactory, javaFactory, interfaceIntrospector),
            new ResourceProcessor(assemblyFactory),
            new ScopeProcessor(assemblyFactory),
            new ServiceProcessor(assemblyFactory, javaFactory, interfaceIntrospector),
            new HeuristicPojoProcessor(assemblyFactory, javaFactory, interfaceIntrospector),
            new PolicyProcessor(assemblyFactory, policyFactory)
        };
        for (JavaClassVisitor e : extensions) {
            classVisitors.addClassVisitor(e);
        }
        JavaClassIntrospector classIntrospector = new DefaultJavaClassIntrospector(classVisitors);

        // Populate ArtifactProcessor registry
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        CompositeProcessor compositeProcessor = new CompositeProcessor(assemblyFactory, policyFactory,
                                                                       interfaceContractMapper, staxProcessors);
        staxProcessors.addArtifactProcessor(compositeProcessor);
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addArtifactProcessor(new JavaInterfaceProcessor(javaFactory, interfaceIntrospector));
        staxProcessors.addArtifactProcessor(new JavaImplementationProcessor(assemblyFactory, policyFactory, javaImplementationFactory, classIntrospector));
        
        // Create a resolver
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DefaultArtifactResolver resolver = new DefaultArtifactResolver(classLoader);

        try {
            
            // Read the composite files
            List<Composite> composites = new ArrayList<Composite>();
            for (String compositeFile: compositeFiles) {
                InputStream is = classLoader.getResourceAsStream(compositeFile);
                Composite composite = staxProcessors.read(is, Composite.class);
                resolver.add(composite);
                composites.add(composite);
            }
            
            for (Composite composite: composites) {
                
                // Resolve the composite
                compositeProcessor.resolve(composite, resolver);
            }
            
            // Wire the top level component's composite
            buildComposite(composites.get(0), assemblyFactory, interfaceContractMapper);
            
        } catch (ContributionException e) {
            throw new RuntimeException(e);
        } catch (CompositeBuilderException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void buildComposite(Composite composite, AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) throws CompositeBuilderException {

        CompositeBuilderMonitor monitor = new CompositeBuilderMonitor() {

            public void problem(Problem problem) {
                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.

                System.out.println("Composite assembly problem: " + problem.getMessage());
            }
        };

        // Configure and wire the composite
        DefaultCompositeBuilder compositeUtil = new DefaultCompositeBuilder(assemblyFactory, interfaceContractMapper, monitor);
        compositeUtil.build(composite);

    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        Object bean = beanFactory.getBean(serviceName);
        return businessInterface.cast(bean);
    }

}
