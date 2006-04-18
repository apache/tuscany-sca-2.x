/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.mock.binding.foo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.EntryPointContextFactory;
import org.apache.tuscany.core.builder.impl.HierarchicalBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * Creates a <code>ContextFactoryBuilder</code> for an entry point or external service configured with the
 * {@link FooBinding}
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class FooBindingBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    /* the top-level builder responsible for evaluating policies */
    private HierarchicalBuilder policyBuilder = new HierarchicalBuilder();

    public FooBindingBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.proxyFactoryFactory = factory;
    }

    /**
     * Sets the factory used to construct wire messages
     * 
     * @param msgFactory
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.messageFactory = msgFactory;
    }

    /**
     * Adds a builder responsible for creating source-side and target-side wire chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and wire chain.
     */
    public void addPolicyBuilder(ContextFactoryBuilder builder) {
        policyBuilder.addBuilder(builder);
    }

    public void build(AssemblyObject object) throws BuilderException {
        if (object instanceof EntryPoint) {
            EntryPoint ep = (EntryPoint) object;
            if (ep.getBindings().size() < 1 || !(ep.getBindings().get(0) instanceof FooBinding)) {
                return;
            }
            EntryPointContextFactory contextFactory = new FooEntryPointContextFactory(ep.getName(), messageFactory);

            ConfiguredService configuredService = ep.getConfiguredService();
            Service service = configuredService.getPort();
            ServiceContract serviceContract = service.getServiceContract();
            Map<Method, SourceInvocationConfiguration> iConfigMap = new HashMap<Method, SourceInvocationConfiguration>();
            SourceWireFactory proxyFactory = proxyFactoryFactory.createSourceWireFactory();
            Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
            for (Method method : javaMethods) {
                SourceInvocationConfiguration iConfig = new SourceInvocationConfiguration(method);
                iConfigMap.put(method, iConfig);
            }
            QualifiedName qName = new QualifiedName(ep.getConfiguredReference().getTargetConfiguredServices().get(0).getPart().getName() + '/' + service.getName());
            WireSourceConfiguration wireConfiguration = new WireSourceConfiguration("foo",qName, iConfigMap, serviceContract.getInterface().getClassLoader(), messageFactory);
            proxyFactory.setBusinessInterface(serviceContract.getInterface());
            proxyFactory.setProxyConfiguration(wireConfiguration);
            contextFactory.addSourceProxyFactory(service.getName(), proxyFactory);
            configuredService.setProxyFactory(proxyFactory);
            if (policyBuilder != null) {
                // invoke the reference builder to handle additional policy metadata
                policyBuilder.build(configuredService);
            }
            // add tail interceptor
            //for (SourceInvocationConfiguration iConfig : iConfigMap.values()) {
            //    iConfig.addInterceptor(new InvokerInterceptor());
            //}
            ep.setContextFactory(contextFactory);

        } else if (object instanceof ExternalService) {
            ExternalService es = (ExternalService) object;
            if (es.getBindings().size() < 1 || !(es.getBindings().get(0) instanceof FooBinding)) {
                return;
            }

            FooExternalServiceContextFactory contextFactory = new FooExternalServiceContextFactory(es.getName(),
                    new FooClientFactory());

            ConfiguredService configuredService = es.getConfiguredService();
            Service service = configuredService.getPort();
            ServiceContract serviceContract = service.getServiceContract();
            Map<Method, TargetInvocationConfiguration> iConfigMap = new HashMap<Method, TargetInvocationConfiguration>();
            TargetWireFactory proxyFactory = proxyFactoryFactory.createTargetWireFactory();
            Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
            for (Method method : javaMethods) {
                TargetInvocationConfiguration iConfig = new TargetInvocationConfiguration(method);
                iConfigMap.put(method, iConfig);
            }
            QualifiedName qName = new QualifiedName(es.getName() + QualifiedName.NAME_SEPARATOR+ service.getName());
            WireTargetConfiguration wireConfiguration = new WireTargetConfiguration(qName, iConfigMap, serviceContract.getInterface().getClassLoader(), messageFactory);
            proxyFactory.setBusinessInterface(serviceContract.getInterface());
            proxyFactory.setProxyConfiguration(wireConfiguration);
            contextFactory.addTargetProxyFactory(service.getName(), proxyFactory);
            configuredService.setProxyFactory(proxyFactory);
            if (policyBuilder != null) {
                // invoke the reference builder to handle additional policy metadata
                policyBuilder.build(configuredService);
            }
            // add tail interceptor
            for (TargetInvocationConfiguration iConfig : iConfigMap.values()) {
                iConfig.addInterceptor(new InvokerInterceptor());
            }

            es.setContextFactory(contextFactory);
        }
    }

    private static class FooClientFactory implements ObjectFactory {

        public Object getInstance() throws ObjectCreationException {
            return new FooClient();
        }
    }
}
