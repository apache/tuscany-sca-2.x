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

package org.apache.tuscany.sca.implementation.java.module;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.DefaultJavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.sca.implementation.java.introspect.ExtensibleJavaClassIntrospector;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassIntrospector;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.AllowsPassByReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ComponentNameProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ContextProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConversationIDProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConversationProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.DestroyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.EagerInitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.HeuristicPojoProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.InitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PolicyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ScopeProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.sca.implementation.java.invocation.CglibProxyFactory;
import org.apache.tuscany.sca.implementation.java.invocation.JavaImplementationProviderFactory;
import org.apache.tuscany.sca.implementation.java.xml.JavaImplementationProcessor;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class JavaRuntimeModuleActivator implements ModuleActivator {
    
    private JavaClassIntrospectorExtensionPoint classVisitors;
    
    public JavaRuntimeModuleActivator() {
    }
     

    public Object[] getExtensionPoints() {
        classVisitors = new DefaultJavaClassIntrospectorExtensionPoint();
        return new Object[] { classVisitors };
    }

    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        PolicyFactory policyFactory = factories.getFactory(PolicyFactory.class);
        
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        
        MessageFactory messageFactory = factories.getFactory(MessageFactory.class);
        ProxyFactoryExtensionPoint proxyFactory = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        proxyFactory.setClassProxyFactory(new CglibProxyFactory(messageFactory, proxyFactory.getInterfaceContractMapper()));
        
        JavaInterfaceIntrospectorExtensionPoint interfaceVisitors = registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        JavaInterfaceIntrospector interfaceIntrospector = new ExtensibleJavaInterfaceIntrospector(javaFactory, interfaceVisitors);
        BaseJavaClassVisitor[] extensions = new BaseJavaClassVisitor[] {
            new ConstructorProcessor(assemblyFactory),
            new AllowsPassByReferenceProcessor(assemblyFactory),
            new ComponentNameProcessor(assemblyFactory),
            new ContextProcessor(assemblyFactory),
            new ConversationIDProcessor(assemblyFactory),
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
        MediatorImpl mediator =new MediatorImpl(dataBindings, transformers);
        JavaPropertyValueObjectFactory factory = new JavaPropertyValueObjectFactory(mediator);

        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        JavaImplementationProcessor javaImplementationProcessor =
            new JavaImplementationProcessor(assemblyFactory, policyFactory, javaImplementationFactory, classIntrospector);
        processors.addArtifactProcessor(javaImplementationProcessor);

        ContextFactoryExtensionPoint contextFactories = registry.getExtensionPoint(ContextFactoryExtensionPoint.class);
        ComponentContextFactory componentContextFactory = contextFactories.getFactory(ComponentContextFactory.class);
        RequestContextFactory requestContextFactory = contextFactories.getFactory(RequestContextFactory.class);
        JavaImplementationProviderFactory javaImplementationProviderFactory =
            new JavaImplementationProviderFactory(proxyFactory, dataBindings, factory, componentContextFactory,
                                                  requestContextFactory);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(javaImplementationProviderFactory);
        
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
