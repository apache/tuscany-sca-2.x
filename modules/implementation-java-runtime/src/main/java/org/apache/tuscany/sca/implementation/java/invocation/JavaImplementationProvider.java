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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopedImplementationProvider;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.RequestContextObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.ResourceHost;
import org.apache.tuscany.sca.implementation.java.injection.ResourceObjectFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationProvider implements ScopedImplementationProvider {
    private JavaImplementation implementation;
    private JavaComponentContextProvider componentContextProvider;
    private RequestContextFactory requestContextFactory;
    
    public JavaImplementationProvider(RuntimeComponent component,
                                      JavaImplementation implementation,
                                      ProxyFactory proxyService,
                                      DataBindingExtensionPoint dataBindingRegistry,
                                      JavaPropertyValueObjectFactory propertyValueObjectFactory,
                                      ComponentContextFactory componentContextFactory,
                                      RequestContextFactory requestContextFactory,
                                      List<PolicyHandlerTuple> policyHandlerClassNames) {
        super();
        this.implementation = implementation;
        this.requestContextFactory = requestContextFactory;
        this.implementation.setPolicyHandlerClassNames(policyHandlerClassNames);
        
        try {
            JavaInstanceFactoryProvider configuration = new JavaInstanceFactoryProvider(implementation);
            configuration.setProxyFactory(proxyService);
            componentContextProvider =
                new JavaComponentContextProvider(component, 
                                                 configuration, 
                                                 dataBindingRegistry,
                                                 propertyValueObjectFactory,
                                                 componentContextFactory, 
                                                 requestContextFactory);

            Scope scope = getScope();

            if (scope == Scope.SYSTEM || scope == Scope.COMPOSITE) {
                // Nothing
            } else {
                // Check for conversational contract if conversational scope
                if (scope == Scope.CONVERSATION) {
                    boolean hasConversationalContract = false;
                    for (Service serviceDef : implementation.getServices()) {
                        if (serviceDef.getInterfaceContract().getInterface().isConversational()) {
                            hasConversationalContract = true;
                            break;
                        }
                    }
                    if (!hasConversationalContract) {
                        String name = implementation.getJavaClass().getName();
                        throw new NoConversationalContractException(name);
                    }
                }
            }

            if (implementation.getConversationIDMembers().size() > 0) {
                componentContextProvider.addConversationIDFactories(implementation.getConversationIDMembers());
            }

            componentContextProvider.configureProperties(component.getProperties());
            handleResources(implementation, proxyService);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        
    }
    
    private void handleResources(JavaImplementation componentType, ProxyFactory proxyService) {
        for (JavaResourceImpl resource : componentType.getResources().values()) {
            String name = resource.getName();

            ObjectFactory<?> objectFactory =
                (ObjectFactory<?>)componentContextProvider.getInstanceFactoryProvider().getFactories().get(resource.getElement());
            if (objectFactory == null) {
                Class<?> type = resource.getElement().getType();
                if (ComponentContext.class.equals(type)) {
                    objectFactory = new JavaComponentContextFactory(componentContextProvider);
                } else if (RequestContext.class.equals(type)) {
                    objectFactory = new RequestContextObjectFactory(requestContextFactory, proxyService);
                } else if (String.class.equals(type)) {
                    objectFactory = new JavaComponentNameFactory(componentContextProvider);
                } else {
                    boolean optional = resource.isOptional();
                    String mappedName = resource.getMappedName();
                    objectFactory = createResourceObjectFactory(type, mappedName, optional, null);
                }
            }
            componentContextProvider.addResourceFactory(name, objectFactory);
        }
    }

    private <T> ResourceObjectFactory<T> createResourceObjectFactory(Class<T> type,
                                                                     String mappedName,
                                                                     boolean optional,
                                                                     ResourceHost host) {
        return new ResourceObjectFactory<T>(type, mappedName, optional, host);
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        try {
            return componentContextProvider.createInvoker(operation);
        } catch (NoSuchMethodException e) {
            // It's possible that the instance being invoked is a user-specified
            // callback object that isn't an instance of the component implementation
            // class.  As an attempt to deal with this, look up a method object from
            // the service interface.  This isn't foolproof, as it's possible that
            // the service interface isn't a Java interface, or that the callback
            // object has the right method signature without implementing the
            // callback interface.  There is code in JavaImplementationInvoker
            // to deal with these possibilities.
            Interface iface = service.getInterfaceContract().getInterface();
            if (iface instanceof JavaInterface) {
                try {
                    Method method = JavaInterfaceUtil.findMethod(((JavaInterface)iface).getJavaClass(), operation);
                    return new JavaImplementationInvoker(operation, method, componentContextProvider.getComponent());
                } catch (NoSuchMethodException e1) {
                    throw new IllegalArgumentException(e1);
                }
            } else {
                return new JavaImplementationInvoker(operation, componentContextProvider.getComponent());
            }
        }
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Scope getScope() {
        return new Scope(implementation.getJavaScope().getScope());
    }

    public void start() {
        componentContextProvider.start();
    }

    public void stop() {
        componentContextProvider.stop();
    }

    public InstanceWrapper createInstanceWrapper() {
        return componentContextProvider.createInstanceWrapper();
    }

    public boolean isEagerInit() {
        return implementation.isEagerInit();
    }

    public long getMaxAge() {
        return implementation.getMaxAge();
    }

    public long getMaxIdleTime() {
        return implementation.getMaxIdleTime();
    }
}
