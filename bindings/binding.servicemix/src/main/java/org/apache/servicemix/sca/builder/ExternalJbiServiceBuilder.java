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
package org.apache.servicemix.sca.builder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.servicemix.sca.assembly.JbiBinding;
import org.apache.servicemix.sca.config.ExternalJbiServiceContextFactory;
import org.apache.servicemix.sca.handler.ExternalJbiServiceClient;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.scdl.WebServiceBinding;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * Creates a <code>RuntimeConfigurationBuilder</code> for an external service configured with the {@link WebServiceBinding}
 */
@Scope("MODULE")
public class ExternalJbiServiceBuilder implements ContextFactoryBuilder {

    private RuntimeContext runtimeContext;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    private ContextFactoryBuilder policyBuilder;

    public ExternalJbiServiceBuilder() {
    }

    @Init(eager = true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    /**
     * @param runtimeContext The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.proxyFactoryFactory = factory;
    }

    /**
     * Sets the factory used to construct invocation messages
     * 
     * @param msgFactory
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.messageFactory = msgFactory;
    }

    /**
     * Sets a builder responsible for creating source-side and target-side invocation chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and invocation chain.
     * 
     * @see org.apache.tuscany.core.builder.impl.HierarchicalBuilder
     */
    public void setPolicyBuilder(ContextFactoryBuilder builder) {
        policyBuilder = builder;
    }

    public void build(AssemblyModelObject object) throws BuilderException {
        if (!(object instanceof ExternalService)) {
            return;
        }
        ExternalService externalService = (ExternalService) object;
        if (externalService.getBindings().size() < 1 || !(externalService.getBindings().get(0) instanceof JbiBinding)) {
            return;
        }

        ExternalJbiServiceClient externalJbiServiceClient = new ExternalJbiServiceClient(externalService);
        ExternalJbiServiceContextFactory config = new ExternalJbiServiceContextFactory(externalService.getName(), new SingletonObjectFactory<ExternalJbiServiceClient>(externalJbiServiceClient));

        ConfiguredService configuredService = externalService.getConfiguredService();
        Service service = configuredService.getService();
        ServiceContract serviceContract = service.getServiceContract();
        Map<Method, InvocationConfiguration> iConfigMap = new MethodHashMap();
        ProxyFactory proxyFactory = proxyFactoryFactory.createProxyFactory();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
        for (Method method : javaMethods) {
            InvocationConfiguration iConfig = new InvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        QualifiedName qName = new QualifiedName(externalService.getName() + "/" + service.getName());
        ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, serviceContract.getInterface().getClassLoader(), messageFactory);
        proxyFactory.setBusinessInterface(serviceContract.getInterface());
        proxyFactory.setProxyConfiguration(pConfiguration);
        config.addTargetProxyFactory(service.getName(), proxyFactory);
        configuredService.setProxyFactory(proxyFactory);
        if (policyBuilder != null) {
            // invoke the reference builder to handle additional policy metadata
            policyBuilder.build(configuredService);
        }
        // add tail interceptor
        for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
            iConfig.addTargetInterceptor(new InvokerInterceptor());
        }

        externalService.getConfiguredService().setContextFactory(config);
    }

}
