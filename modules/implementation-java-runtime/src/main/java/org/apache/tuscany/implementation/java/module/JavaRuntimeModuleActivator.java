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

package org.apache.tuscany.implementation.java.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.context.JavaComponentBuilder;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
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
import org.apache.tuscany.implementation.java.proxy.JDKProxyService;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * @version $Rev$ $Date$
 */
public class JavaRuntimeModuleActivator implements ModuleActivator {
    
    private AssemblyFactory assemblyFactory;
    private JavaFactory javaFactory;
    private PolicyFactory policyFactory;
    
    public JavaRuntimeModuleActivator() {
        assemblyFactory = new DefaultAssemblyFactory();
        javaFactory = new DefaultJavaFactory();
        policyFactory = new DefaultPolicyFactory();
    }
     

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(ProxyService.class, new JDKProxyService());
        map.put(JavaClassIntrospectorExtensionPoint.class, new DefaultJavaClassIntrospector());
        map.put(JavaInterfaceIntrospectorExtensionPoint.class, new DefaultJavaInterfaceIntrospector(javaFactory));
        return map;
    }

    public void start(ExtensionPointRegistry extensionPointRegistry) {
        JDKProxyService proxyService = (JDKProxyService) extensionPointRegistry.getExtensionPoint(ProxyService.class);
        InterfaceContractMapper mapper = extensionPointRegistry.getExtensionPoint(InterfaceContractMapper.class);
        proxyService.setInterfaceContractMapper(mapper);
        WorkContext workContext = extensionPointRegistry.getExtensionPoint(WorkContext.class);
        proxyService.setWorkContext(workContext);
        
        JavaInterfaceIntrospectorExtensionPoint interfaceIntrospector = extensionPointRegistry
            .getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);

        JavaClassIntrospectorExtensionPoint classIntrospector = extensionPointRegistry
            .getExtensionPoint(JavaClassIntrospectorExtensionPoint.class);
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

        StAXArtifactProcessorExtensionPoint artifactProcessorRegistry = extensionPointRegistry
            .getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(assemblyFactory);
        JavaImplementationProcessor javaImplementationProcessor =
            new JavaImplementationProcessor(assemblyFactory, policyFactory, javaImplementationFactory, classIntrospector);
        artifactProcessorRegistry.addExtension(javaImplementationProcessor);

        BuilderRegistry builderRegistry = extensionPointRegistry.getExtensionPoint(BuilderRegistry.class);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setProxyService(extensionPointRegistry.getExtensionPoint(ProxyService.class));
        builder.setWorkContext(extensionPointRegistry.getExtensionPoint(WorkContext.class));
        builderRegistry.register(JavaImplementation.class, builder);

        Mediator mediator = extensionPointRegistry.getExtensionPoint(Mediator.class);
        JavaPropertyValueObjectFactory factory = new JavaPropertyValueObjectFactory(mediator);
        builder.setPropertyValueObjectFactory(factory);

        DataBindingExtensionPoint dataBindingRegistry = extensionPointRegistry.getExtensionPoint(DataBindingExtensionPoint.class);
        builder.setDataBindingRegistry(dataBindingRegistry);

    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
