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
import org.apache.tuscany.assembly.xml.impl.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.impl.CompositeProcessor;
import org.apache.tuscany.assembly.xml.impl.ConstrainingTypeProcessor;
import org.apache.tuscany.bean.context.CompositeApplicationContext;
import org.apache.tuscany.idl.java.xml.JavaInterfaceProcessor;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.DefaultArtifactResolver;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;

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
        DefaultStAXArtifactProcessorRegistry registry = new DefaultStAXArtifactProcessorRegistry();
        CompositeProcessor compositeProcessor = new CompositeProcessor(registry);
        registry.addArtifactProcessor(compositeProcessor);
        registry.addArtifactProcessor(new ComponentTypeProcessor(registry));
        registry.addArtifactProcessor(new ConstrainingTypeProcessor(registry));
        registry.addArtifactProcessor(new JavaInterfaceProcessor());
        registry.addArtifactProcessor(new JavaImplementationProcessor());
        
        // Create a resolver
        DefaultArtifactResolver resolver = new DefaultArtifactResolver();

        try {
            // Parse the composite file
            InputStream is = getClass().getClassLoader().getResourceAsStream(compositeFile);
            Composite composite = registry.read(is, Composite.class);
            resolver.put(composite, composite);
            
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
