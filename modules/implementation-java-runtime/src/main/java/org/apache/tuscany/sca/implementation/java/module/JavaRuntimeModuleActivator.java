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

import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.CglibProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.invocation.JavaCallbackRuntimeWireProcessor;
import org.apache.tuscany.sca.implementation.java.invocation.JavaImplementationProviderFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.util.PolicyHandlerDefinitionsLoader;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class JavaRuntimeModuleActivator implements ModuleActivator {

    public JavaRuntimeModuleActivator() {
    }

    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);

        MessageFactory messageFactory = factories.getFactory(MessageFactory.class);
        ProxyFactoryExtensionPoint proxyFactory = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        proxyFactory.setClassProxyFactory(new CglibProxyFactory(messageFactory, proxyFactory
            .getInterfaceContractMapper()));

        JavaInterfaceFactory javaFactory = factories.getFactory(JavaInterfaceFactory.class);

        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        JavaPropertyValueObjectFactory factory = new JavaPropertyValueObjectFactory(mediator);

        ContextFactoryExtensionPoint contextFactories = registry.getExtensionPoint(ContextFactoryExtensionPoint.class);
        ComponentContextFactory componentContextFactory = contextFactories.getFactory(ComponentContextFactory.class);
        RequestContextFactory requestContextFactory = contextFactories.getFactory(RequestContextFactory.class);

        Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassNames = null;
        policyHandlerClassNames = PolicyHandlerDefinitionsLoader.loadPolicyHandlerClassnames();
        
        JavaImplementationProviderFactory javaImplementationProviderFactory =
            new JavaImplementationProviderFactory(proxyFactory, dataBindings, factory, componentContextFactory,
                                                  requestContextFactory, policyHandlerClassNames);

        ProviderFactoryExtensionPoint providerFactories =
            registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(javaImplementationProviderFactory);

        InterfaceContractMapper interfaceContractMapper = registry.getExtensionPoint(InterfaceContractMapper.class);
        RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint =
            registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        if (wireProcessorExtensionPoint != null) {
            wireProcessorExtensionPoint.addWireProcessor(new JavaCallbackRuntimeWireProcessor(interfaceContractMapper,
                                                                                              javaFactory));
            //wireProcessorExtensionPoint.addWireProcessor(new JavaPolicyHandlingRuntimeWireProcessor());
        }
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
