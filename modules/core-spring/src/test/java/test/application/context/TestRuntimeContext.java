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
package test.application.context;

import java.io.InputStream;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.core.bean.context.CompositeApplicationContext;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.java.xml.JavaInterfaceProcessor;

/**
 * A mini test runtime that uses a custom Spring ApplicationContext to turn an
 * SCA composite into an assembly of Spring bean definitions.
 * 
 * @version $Rev$ $Date$
 */
public class TestRuntimeContext {

    private CompositeApplicationContext applicationContext;

    public TestRuntimeContext(String compositeFile) {
        
        // Populate ArtifactProcessor registry
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        CompositeProcessor compositeProcessor = new CompositeProcessor(staxProcessors);
        staxProcessors.addExtension(compositeProcessor);
        staxProcessors.addExtension(new ComponentTypeProcessor(staxProcessors));
        staxProcessors.addExtension(new ConstrainingTypeProcessor(staxProcessors));
        staxProcessors.addExtension(new JavaInterfaceProcessor());
        staxProcessors.addExtension(new JavaImplementationProcessor());
        
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
    
            // Create a Spring application composite context for the composite
            applicationContext = new CompositeApplicationContext(composite);
            applicationContext.refresh();
            
        } catch (ContributionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the service with the specified name.
     * 
     * @param <B>
     * @param businessInterface
     * @param serviceName
     * @return
     */
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        Object bean = applicationContext.getBean(serviceName);
        return businessInterface.cast(bean);
    }

}
