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
import org.apache.tuscany.core.invocation.JDKProxyService;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.impl.DefaultMediator;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.context.JavaComponentBuilder;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
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
import org.apache.tuscany.implementation.java.invocation.RuntimeJavaImplementationFactory;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.scope.ScopeRegistry;
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
        map.put(ProxyService.class, new org.apache.tuscany.implementation.java.proxy.JDKProxyService());
        map.put(JavaClassIntrospectorExtensionPoint.class, new DefaultJavaClassIntrospector());
        map.put(JavaInterfaceIntrospectorExtensionPoint.class, new DefaultJavaInterfaceIntrospector(javaFactory));
        return map;
    }

    public void start(ExtensionPointRegistry registry) {
        JDKProxyService proxyFactory = (JDKProxyService) registry.getExtensionPoint(ProxyFactory.class);
        
        JavaInterfaceIntrospectorExtensionPoint interfaceIntrospector = registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);

        JavaClassIntrospectorExtensionPoint classIntrospector = registry.getExtensionPoint(JavaClassIntrospectorExtensionPoint.class);
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
        for (JavaClassIntrospectorExtension extension : extensions) {
            classIntrospector.addExtension(extension);
        }
        
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        DefaultMediator mediator =new DefaultMediator(dataBindings, transformers);
        JavaPropertyValueObjectFactory factory = new JavaPropertyValueObjectFactory(mediator);

        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        
        ScopeRegistry scopeRegistry = registry.getExtensionPoint(ScopeRegistry.class);

        WorkContext workContext = registry.getExtensionPoint(WorkContext.class);
        JavaImplementationFactory javaImplementationFactory =
            new RuntimeJavaImplementationFactory(assemblyFactory, scopeRegistry, proxyFactory,
                                                 workContext, dataBindings, factory);
        JavaImplementationProcessor javaImplementationProcessor =
            new JavaImplementationProcessor(assemblyFactory, policyFactory, javaImplementationFactory, classIntrospector);
        processors.addExtension(javaImplementationProcessor);

        // FIXME: To be removed
        org.apache.tuscany.implementation.java.proxy.JDKProxyService proxyService =
            (org.apache.tuscany.implementation.java.proxy.JDKProxyService)registry.getExtensionPoint(ProxyService.class);
        InterfaceContractMapper mapper = new DefaultInterfaceContractMapper();
        proxyService.setInterfaceContractMapper(mapper);
        proxyService.setWorkContext(workContext);

        BuilderRegistry builderRegistry = registry.getExtensionPoint(BuilderRegistry.class);
        if (builderRegistry != null) {
            JavaComponentBuilder builder = new JavaComponentBuilder();
            builder.setProxyService(registry.getExtensionPoint(ProxyService.class));
            builder.setWorkContext(registry.getExtensionPoint(WorkContext.class));
            builderRegistry.register(JavaImplementation.class, builder);

            builder.setPropertyValueObjectFactory(factory);
            builder.setDataBindingRegistry(dataBindings);
        }

    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
