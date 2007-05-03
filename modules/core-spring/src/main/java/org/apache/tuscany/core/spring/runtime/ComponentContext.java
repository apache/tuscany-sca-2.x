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
import org.apache.tuscany.assembly.Base;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.assembly.util.PrintUtil;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.core.spring.assembly.impl.BeanAssemblyFactory;
import org.apache.tuscany.core.spring.implementation.java.impl.BeanJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.introspect.BaseJavaClassIntrospectorExtension;
import org.apache.tuscany.implementation.java.introspect.DefaultJavaClassIntrospector;
import org.apache.tuscany.implementation.java.introspect.JavaClassIntrospectorExtension;
import org.apache.tuscany.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.implementation.java.introspect.impl.AllowsPassByReferenceProcessor;
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
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
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
        JavaFactory javaFactory = new DefaultJavaFactory();
        JavaInterfaceIntrospectorExtensionPoint interfaceIntrospector = new DefaultJavaInterfaceIntrospector(javaFactory);
        JavaImplementationFactory javaImplementationFactory = new BeanJavaImplementationFactory(beanFactory);
        JavaClassIntrospectorExtensionPoint classIntrospector = new DefaultJavaClassIntrospector();
        
        BaseJavaClassIntrospectorExtension[] extensions = new BaseJavaClassIntrospectorExtension[] {
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
        for (JavaClassIntrospectorExtension e : extensions) {
            classIntrospector.addExtension(e);
        }

        // Populate ArtifactProcessor registry
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        CompositeProcessor compositeProcessor = new CompositeProcessor(assemblyFactory, policyFactory,
                                                                       interfaceContractMapper, staxProcessors);
        staxProcessors.addExtension(compositeProcessor);
        staxProcessors.addExtension(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addExtension(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addExtension(new JavaInterfaceProcessor(javaFactory, interfaceIntrospector));
        staxProcessors.addExtension(new JavaImplementationProcessor(assemblyFactory, policyFactory, javaImplementationFactory, classIntrospector));
        
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
            wire(composites.get(0), assemblyFactory, interfaceContractMapper);
            
        } catch (ContributionException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void wire(Composite composite, AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
        CompositeUtil compositeUtil = new CompositeUtil(assemblyFactory, interfaceContractMapper);

        List<Base> problems = new ArrayList<Base>() {
            private static final long serialVersionUID = 4819831446590718923L;
            
            @Override
            public boolean add(Base o) {
                //TODO Use a monitor to report configuration problems
                
                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.
                
                System.err.println("Composite configuration problem:");
                new PrintUtil(System.err).print(o);
                return super.add(o);
            }
        };
        

        // Configure and wire the composite
        compositeUtil.configureAndWire(composite, problems);

//        if (!problems.isEmpty()) {
//            throw new VariantRuntimeException(new RuntimeException("Problems in the composite..."));
//        }
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        Object bean = beanFactory.getBean(serviceName);
        return businessInterface.cast(bean);
    }

}
