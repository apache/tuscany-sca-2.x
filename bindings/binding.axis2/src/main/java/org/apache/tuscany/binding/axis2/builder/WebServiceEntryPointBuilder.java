/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.config.WebServiceEntryPointContextFactory;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.EntryPointContextFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.InvocationRuntimeException;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;


/**
 * Creates a <code>ContextFactoryBuilder</code> for an entry point configured
 * with the {@link WebServiceBinding}
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class WebServiceEntryPointBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    private ContextFactoryBuilder policyBuilder;

    public WebServiceEntryPointBuilder() {
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
     * Sets a builder responsible for creating source-side and target-side wire chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that 
     * operate on specific metadata used to construct and wire chain.
     * 
     * @see org.apache.tuscany.core.builder.impl.HierarchicalBuilder
     */
    public void setPolicyBuilder(ContextFactoryBuilder builder) {
        policyBuilder = builder;
    }

    public void build(AssemblyObject object) throws BuilderException {
        if (!(object instanceof EntryPoint)) {
            return;
        }
        EntryPoint entryPoint = (EntryPoint) object;
        if (entryPoint.getBindings().size() < 1
            || !(entryPoint.getBindings().get(0) instanceof WebServiceBinding)) {
            return;
        }

        EntryPointContextFactory config
            = new WebServiceEntryPointContextFactory(entryPoint.getName(),
                                                     entryPoint.getConfiguredService()
                                                         .getPort().getName(),
                                                     messageFactory);

        ConfiguredService configuredService = entryPoint.getConfiguredService();
        Service service = configuredService.getPort();
        ServiceContract serviceContract = service.getServiceContract();
        Map<Method, SourceInvocationConfiguration> iConfigMap
            = new HashMap<Method, SourceInvocationConfiguration>();
        SourceWireFactory proxyFactory = proxyFactoryFactory.createSourceWireFactory();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
        for (Method method : javaMethods) {
            SourceInvocationConfiguration iConfig = new SourceInvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        QualifiedName qName = new QualifiedName(entryPoint.getConfiguredReference()
                                                    .getTargetConfiguredServices().get(0)
                                                    .getPart().getName()
                                                + '/' + service.getName());
        WireSourceConfiguration wireConfiguration
            = new WireSourceConfiguration(qName,
                                          iConfigMap,
                                          serviceContract.getInterface().getClassLoader(),
                                          messageFactory);
        proxyFactory.setBusinessInterface(serviceContract.getInterface());
        proxyFactory.setConfiguration(wireConfiguration);
        config.addSourceProxyFactory(service.getName(), proxyFactory);
        configuredService.setProxyFactory(proxyFactory);
        if (policyBuilder != null) {
            // invoke the reference builder to handle additional policy metadata
            policyBuilder.build(configuredService);
        }
        // add tail interceptor
        //for (TargetInvocationConfiguration iConfig : iConfigMap.values()) {
        //    iConfig.addInterceptor(new EntryPointInvokerInterceptor());
        //}
        entryPoint.setContextFactory(config);
    }
    
    //FIXME same as the InvokerInterceptor except that it doesn't throw an exception in setNext
    // For some reason another InvokerInterceptor is added after this one, need Jim to look into it
    // and figure out why.
    public static class EntryPointInvokerInterceptor implements Interceptor {
        
        public EntryPointInvokerInterceptor() {
        }

        public Message invoke(Message msg) throws InvocationRuntimeException {
            TargetInvoker invoker = msg.getTargetInvoker();
            if (invoker == null) {
                throw new InvocationRuntimeException("No target invoker specified on message");
            }
            return invoker.invoke(msg);
        }

        public void setNext(Interceptor next) {
        }

    }


}
