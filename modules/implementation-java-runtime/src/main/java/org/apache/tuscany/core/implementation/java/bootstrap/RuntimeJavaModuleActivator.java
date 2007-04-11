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
import org.apache.tuscany.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ScopeProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
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
        map.put(JavaClassIntrospectorExtensionPoint.class, new DefaultJavaClassIntrospector());
        map.put(JavaInterfaceIntrospectorExtensionPoint.class, new DefaultJavaInterfaceIntrospector());
        return map;
    }

    /**
     * @see org.apache.tuscany.spi.bootstrap.ModuleActivator#start(org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry)
     */
    public void start(ExtensionPointRegistry extensionPointRegistry) {
        JavaInterfaceIntrospectorExtensionPoint interfaceIntrospector = extensionPointRegistry
            .getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);

        JavaClassIntrospectorExtensionPoint classIntrospector = extensionPointRegistry.getExtensionPoint(JavaClassIntrospectorExtensionPoint.class);
        BaseJavaClassIntrospectorExtension[] extensions = new BaseJavaClassIntrospectorExtension[] {
            new ConstructorProcessor(),
            new AllowsPassByReferenceProcessor(),
            new ContextProcessor(),
            new ConversationProcessor(),
            new DestroyProcessor(),
            new EagerInitProcessor(),
            new InitProcessor(),
            new PropertyProcessor(),
            new ReferenceProcessor(interfaceIntrospector),
            new ResourceProcessor(),
            new ScopeProcessor(),
            new ServiceProcessor(interfaceIntrospector),
            new HeuristicPojoProcessor(interfaceIntrospector)

        };
        for (JavaClassIntrospectorExtension e : extensions) {
            classIntrospector.addExtension(e);
        }

        StAXArtifactProcessorRegistry artifactProcessorRegistry = extensionPointRegistry
            .getExtensionPoint(StAXArtifactProcessorRegistry.class);
        JavaImplementationProcessor javaImplementationProcessor = new JavaImplementationProcessor(classIntrospector);
        artifactProcessorRegistry.addArtifactProcessor(javaImplementationProcessor);

        BuilderRegistry builderRegistry = extensionPointRegistry.getExtensionPoint(BuilderRegistry.class);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(extensionPointRegistry.getExtensionPoint(ScopeRegistry.class));
        builder.setProxyService(extensionPointRegistry.getExtensionPoint(ProxyService.class));
        builder.setWorkContext(extensionPointRegistry.getExtensionPoint(WorkContext.class));
        builderRegistry.register(JavaImplementation.class, builder);

    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
