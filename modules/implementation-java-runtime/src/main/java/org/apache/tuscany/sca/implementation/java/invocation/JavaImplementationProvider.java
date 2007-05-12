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

import java.net.URI;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.injection.ResourceHost;
import org.apache.tuscany.sca.implementation.java.injection.ResourceObjectFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ScopedImplementationProvider;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.spi.ObjectFactory;
import org.apache.tuscany.sca.spi.component.TargetInvokerCreationException;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationProvider implements ScopedImplementationProvider {
    private JavaImplementation implementation;
    private JavaComponentInfo atomicComponent;

    public JavaImplementationProvider(
                                      RuntimeComponent component,
                                      JavaImplementation implementation,
                                      ProxyFactory proxyService,
                                      DataBindingExtensionPoint dataBindingRegistry,
                                      JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.implementation = implementation;

        try {
            PojoConfiguration configuration = new PojoConfiguration(implementation);
            configuration.setProxyFactory(proxyService);
            // FIXME: Group id to be removed
            configuration.setGroupId(URI.create("/"));
            atomicComponent = new JavaComponentInfo(component, configuration, dataBindingRegistry,
                                                                      propertyValueObjectFactory);

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

            if (implementation.getConversationIDMember() != null) {
                atomicComponent.addConversationIDFactory(implementation.getConversationIDMember());
            }

            atomicComponent.configureProperties(component.getProperties());
            handleResources(implementation, atomicComponent);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    private void handleResources(JavaImplementation componentType, JavaComponentInfo component) {
        for (JavaResourceImpl resource : componentType.getResources().values()) {
            String name = resource.getName();

            ObjectFactory<?> objectFactory = (ObjectFactory<?>)component.getConfiguration().getFactories().get(resource
                .getElement());
            Class<?> type = resource.getElement().getType();
            if (ComponentContext.class.equals(type)) {
                objectFactory = new PojoComponentContextFactory(component);
            } else {
                boolean optional = resource.isOptional();
                String mappedName = resource.getMappedName();
                objectFactory = createResourceObjectFactory(type, mappedName, optional, null);
            }
            component.addResourceFactory(name, objectFactory);
        }
    }

    private <T> ResourceObjectFactory<T> createResourceObjectFactory(Class<T> type,
                                                                     String mappedName,
                                                                     boolean optional,
                                                                     ResourceHost host) {
        return new ResourceObjectFactory<T>(type, mappedName, optional, host);
    }

    public Object createInstance(RuntimeComponent component, ComponentService service) {
        return atomicComponent.createInstance();
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        try {
            return new TargetInvokerInvoker(atomicComponent.createTargetInvoker(operation));
        } catch (TargetInvokerCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Invoker createCallbackInvoker(Operation operation) {
        try {
            return new TargetInvokerInvoker(atomicComponent.createTargetInvoker(operation));
        } catch (TargetInvokerCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Scope getScope() {
        return new Scope(implementation.getJavaScope().getScope());
    }

    public void start() {
        atomicComponent.start();
    }

    public void stop() {
        atomicComponent.stop();
    }

    public InstanceWrapper createInstanceWrapper() {
        return atomicComponent.createInstanceWrapper();
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
