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

import java.net.URL;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.impl.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.impl.CompositeProcessor;
import org.apache.tuscany.assembly.xml.impl.ConstrainingTypeProcessor;
import org.apache.tuscany.bean.impl.BeanAssemblyFactory;
import org.apache.tuscany.idl.java.xml.JavaInterfaceProcessor;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.java.bean.impl.BeanJavaImplementationFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.DefaultArtifactResolver;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;
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
        JavaImplementationFactory javaImplementationFactory = new BeanJavaImplementationFactory(beanFactory);

        // Populate ArtifactProcessor registry
        DefaultStAXArtifactProcessorRegistry registry = new DefaultStAXArtifactProcessorRegistry();
        CompositeProcessor compositeProcessor = new CompositeProcessor(assemblyFactory, policyFactory, registry);
        registry.addArtifactProcessor(compositeProcessor);
        registry.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, registry));
        registry.addArtifactProcessor(new ConstrainingTypeProcessor(registry));
        registry.addArtifactProcessor(new JavaInterfaceProcessor());
        registry.addArtifactProcessor(new JavaImplementationProcessor(javaImplementationFactory));
        
        // Create a resolver
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();

        try {
            // Parse the composite file
            URL url = getClass().getClassLoader().getResource(compositeFile);
            Composite composite = registry.read(url, Composite.class);
            resolver.put(composite, composite);
            
            // Resolve and configure the composite
            compositeProcessor.resolve(composite, resolver);
            compositeProcessor.optimize(composite);
            
        } catch (ContributionException e) {
            throw new RuntimeException(e);
        }
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        Object bean = beanFactory.getBean(serviceName);
        return businessInterface.cast(bean);
    }

}
