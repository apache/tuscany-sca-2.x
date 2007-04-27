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
package test.model.variant;

import java.io.InputStream;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.bean.impl.BeanAssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.bean.impl.BeanJavaImplementationFactory;
import org.apache.tuscany.implementation.java.introspect.DefaultJavaClassIntrospector;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
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
public class VariantRuntimeContext {

    private DefaultListableBeanFactory beanFactory;

    public VariantRuntimeContext(String compositeFile) {

        // Create Spring bean factory
        beanFactory = new DefaultListableBeanFactory();

        // Create SCA assembly and SCA Java factories
        AssemblyFactory assemblyFactory = new BeanAssemblyFactory(new DefaultAssemblyFactory(), beanFactory);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        InterfaceContractMapper interfaceContractMapper = new DefaultInterfaceContractMapper();
        JavaImplementationFactory javaImplementationFactory = new BeanJavaImplementationFactory(beanFactory);

        // Populate ArtifactProcessor registry
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        CompositeProcessor compositeProcessor = new CompositeProcessor(assemblyFactory, policyFactory,
                                                                       interfaceContractMapper, staxProcessors);
        staxProcessors.addExtension(compositeProcessor);
        staxProcessors.addExtension(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addExtension(new ConstrainingTypeProcessor(staxProcessors));
        staxProcessors.addExtension(new JavaInterfaceProcessor());
        staxProcessors.addExtension(new JavaImplementationProcessor(
            assemblyFactory, policyFactory, javaImplementationFactory, new DefaultJavaClassIntrospector()));
        
        // Create a resolver
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DefaultArtifactResolver resolver = new DefaultArtifactResolver(classLoader);

        try {
            // Parse the composite file
            InputStream is = classLoader.getResourceAsStream(compositeFile);
            Composite composite = staxProcessors.read(is, Composite.class);
            resolver.add(composite);
            
            // Resolve and configure the composite
            compositeProcessor.resolve(composite, resolver);
            compositeProcessor.wire(composite);
            
        } catch (ContributionException e) {
            throw new RuntimeException(e);
        }
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        Object bean = beanFactory.getBean(serviceName);
        return businessInterface.cast(bean);
    }

}
