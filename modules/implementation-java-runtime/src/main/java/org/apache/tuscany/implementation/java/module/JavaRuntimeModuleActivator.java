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
import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.impl.DefaultMediator;
import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.introspect.DefaultJavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.implementation.java.introspect.ExtensibleJavaClassIntrospector;
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
import org.apache.tuscany.implementation.java.invocation.JavaImplementationProviderFactory;
import org.apache.tuscany.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.policy.DefaultPolicyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.JDKProxyService;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.spi.component.WorkContext;

/**
 * @version $Rev$ $Date$
 */
public class JavaRuntimeModuleActivator implements ModuleActivator {
    
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    private PolicyFactory policyFactory;
    private JavaClassIntrospectorExtensionPoint classVisitors;
    
    public JavaRuntimeModuleActivator() {
        assemblyFactory = new DefaultAssemblyFactory();
        javaFactory = new DefaultJavaInterfaceFactory();
        policyFactory = new DefaultPolicyFactory();
        classVisitors = new DefaultJavaClassIntrospectorExtensionPoint();
    }
     

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(JavaClassIntrospectorExtensionPoint.class, classVisitors);
        return map;
    }

    public void start(ExtensionPointRegistry registry) {
        JDKProxyService proxyFactory = (JDKProxyService) registry.getExtensionPoint(ProxyFactory.class);
        
        JavaInterfaceIntrospectorExtensionPoint interfaceVisitors = registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        JavaInterfaceIntrospector interfaceIntrospector = new ExtensibleJavaInterfaceIntrospector(javaFactory, interfaceVisitors);
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
        for (JavaClassVisitor extension : extensions) {
            classVisitors.addClassVisitor(extension);
        }
        JavaClassIntrospector classIntrospector = new ExtensibleJavaClassIntrospector(classVisitors);
        
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        DefaultMediator mediator =new DefaultMediator(dataBindings, transformers);
        JavaPropertyValueObjectFactory factory = new JavaPropertyValueObjectFactory(mediator);

        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        
        WorkContext workContext = registry.getExtensionPoint(WorkContext.class);
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        JavaImplementationProcessor javaImplementationProcessor =
            new JavaImplementationProcessor(assemblyFactory, policyFactory, javaImplementationFactory, classIntrospector);
        processors.addArtifactProcessor(javaImplementationProcessor);

        JavaImplementationProviderFactory javaImplementationProviderFactory =
            new JavaImplementationProviderFactory(proxyFactory,
                                                 workContext, dataBindings, factory);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(javaImplementationProviderFactory);
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
