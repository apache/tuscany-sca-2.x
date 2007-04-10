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

package org.apache.tuscany.core.implementation.java.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.wire.jdk.JDKProxyService;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.introspection.ImplementationProcessorExtension;
import org.apache.tuscany.implementation.java.introspection.IntrospectionRegistry;
import org.apache.tuscany.implementation.java.introspection.impl.IntrospectionRegistryImpl;
import org.apache.tuscany.implementation.java.processor.AllowsPassByReferenceProcessor;
import org.apache.tuscany.implementation.java.processor.ConstructorProcessor;
import org.apache.tuscany.implementation.java.processor.ContextProcessor;
import org.apache.tuscany.implementation.java.processor.ConversationProcessor;
import org.apache.tuscany.implementation.java.processor.DestroyProcessor;
import org.apache.tuscany.implementation.java.processor.EagerInitProcessor;
import org.apache.tuscany.implementation.java.processor.HeuristicPojoProcessor;
import org.apache.tuscany.implementation.java.processor.InitProcessor;
import org.apache.tuscany.implementation.java.processor.PropertyProcessor;
import org.apache.tuscany.implementation.java.processor.ReferenceProcessor;
import org.apache.tuscany.implementation.java.processor.ResourceProcessor;
import org.apache.tuscany.implementation.java.processor.ScopeProcessor;
import org.apache.tuscany.implementation.java.processor.ServiceProcessor;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessorRegistry;
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.bootstrap.ModuleActivator;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeJavaModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(ProxyService.class, new JDKProxyService());
        map.put(IntrospectionRegistry.class, new IntrospectionRegistryImpl());
        return map;
    }
    

    /**
     * @see org.apache.tuscany.spi.bootstrap.ModuleActivator#start(org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry)
     */
    public void start(ExtensionPointRegistry registry) {
        IntrospectionRegistry introspectionRegistry = registry.getExtensionPoint(IntrospectionRegistry.class);
        ImplementationProcessorExtension[] extensions = new ImplementationProcessorExtension[] {new ConstructorProcessor(),
                                                                                                new AllowsPassByReferenceProcessor(),
                                                                                                new ContextProcessor(),
                                                                                                new ConversationProcessor(),
                                                                                                new DestroyProcessor(),
                                                                                                new EagerInitProcessor(),
                                                                                                new InitProcessor(),
                                                                                                new PropertyProcessor(),
                                                                                                new ReferenceProcessor(),
                                                                                                new ResourceProcessor(),
                                                                                                new ScopeProcessor(),
                                                                                                new ServiceProcessor(),
                                                                                                new HeuristicPojoProcessor()

        };
        for (ImplementationProcessorExtension e : extensions) {
            e.setRegistry(introspectionRegistry);
            introspectionRegistry.registerProcessor(e);
        }
        
        StAXArtifactProcessorRegistry artifactProcessorRegistry = registry.getExtensionPoint(StAXArtifactProcessorRegistry.class);
        artifactProcessorRegistry.addArtifactProcessor(new JavaImplementationProcessor());

        BuilderRegistry builderRegistry = registry.getExtensionPoint(BuilderRegistry.class);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(registry.getExtensionPoint(ScopeRegistry.class));
        builder.setProxyService(registry.getExtensionPoint(ProxyService.class));
        builder.setWorkContext(registry.getExtensionPoint(WorkContext.class));
        builderRegistry.register(JavaImplementation.class, builder);

    }
    

    public void stop(ExtensionPointRegistry registry) {
    }

}
