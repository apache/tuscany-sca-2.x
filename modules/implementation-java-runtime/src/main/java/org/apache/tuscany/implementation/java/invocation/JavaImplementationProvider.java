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

package org.apache.tuscany.implementation.java.invocation;

import java.net.URI;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.core.ImplementationActivator;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.ScopedImplementationProvider;
import org.apache.tuscany.core.scope.CompositeScopeContainer;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.impl.JavaImplementationImpl;
import org.apache.tuscany.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.implementation.java.injection.ResourceObjectFactory;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.scope.ScopeNotFoundException;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.host.ResourceHost;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationProvider extends JavaImplementationImpl implements JavaImplementation,
    ScopedImplementationProvider, ImplementationActivator {
    private JavaPropertyValueObjectFactory propertyValueObjectFactory;
    private DataBindingExtensionPoint dataBindingRegistry;
    private ProxyFactory proxyService;
    private WorkContext workContext;
    private ScopeRegistry scopeRegistry;

    public JavaImplementationProvider(ScopeRegistry scopeRegistry,
                                      ProxyFactory proxyService,
                                      WorkContext workContext,
                                      DataBindingExtensionPoint dataBindingRegistry,
                                      JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.scopeRegistry = scopeRegistry;
        this.proxyService = proxyService;
        this.workContext = workContext;
        this.dataBindingRegistry = dataBindingRegistry;
        this.propertyValueObjectFactory = propertyValueObjectFactory;
    }

    public void configure(RuntimeComponent component) {
        try {
            PojoConfiguration configuration = new PojoConfiguration(this);
            configuration.setProxyFactory(proxyService);
            configuration.setWorkContext(workContext);
            // FIXME: Group id to be removed
            configuration.setGroupId(URI.create("/"));
            JavaComponentInfo atomicComponent = new JavaComponentInfo(component, configuration, dataBindingRegistry,
                                                                      propertyValueObjectFactory);

            Scope scope = getScope();

            if (scope == Scope.SYSTEM || scope == Scope.COMPOSITE) {
                // FIXME:
                atomicComponent.setScopeContainer(new CompositeScopeContainer());
            } else {
                // Check for conversational contract if conversational scope
                if (scope == Scope.CONVERSATION) {
                    boolean hasConversationalContract = false;
                    for (Service serviceDef : getServices()) {
                        if (serviceDef.getInterfaceContract().getInterface().isConversational()) {
                            hasConversationalContract = true;
                            break;
                        }
                    }
                    if (!hasConversationalContract) {
                        String name = getJavaClass().getName();
                        throw new NoConversationalContractException(name);
                    }
                }
                // Now it's ok to set the scope container
                ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(scope);
                if (scopeContainer == null) {
                    throw new ScopeNotFoundException(scope.toString());
                }
                atomicComponent.setScopeContainer(scopeContainer);
            }
            component.setImplementationConfiguration(atomicComponent);

            if (getConversationIDMember() != null) {
                atomicComponent.addConversationIDFactory(getConversationIDMember());
            }

            atomicComponent.configureProperties(component.getProperties());
            handleResources(this, atomicComponent);
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
        JavaComponentInfo atomicComponent = (JavaComponentInfo)component.getImplementationConfiguration();
        return atomicComponent.createInstance();
    }

    public Interceptor createInterceptor(RuntimeComponent component, RuntimeComponentService service, Operation operation) {
        JavaComponentInfo atomicComponent = (JavaComponentInfo)component.getImplementationConfiguration();
        try {
            return new TargetInvokerInterceptor(atomicComponent.createTargetInvoker(operation));
        } catch (TargetInvokerCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Interceptor createCallbackInterceptor(RuntimeComponent component, Operation operation) {
        JavaComponentInfo atomicComponent = (JavaComponentInfo)component.getImplementationConfiguration();
        try {
            return new TargetInvokerInterceptor(atomicComponent.createTargetInvoker(operation));
        } catch (TargetInvokerCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public InterfaceContract getImplementationInterfaceContract(RuntimeComponentService service) {
        return service.getInterfaceContract();
    }

    /**
     * @see org.apache.tuscany.core.ImplementationProvider#getScope()
     */
    public Scope getScope() {
        return new Scope(getJavaScope().getScope());
    }

    public void start(RuntimeComponent component) {
        JavaComponentInfo atomicComponent = (JavaComponentInfo)component.getImplementationConfiguration();
        atomicComponent.start();
    }

    public void stop(RuntimeComponent component) {
        JavaComponentInfo atomicComponent = (JavaComponentInfo)component.getImplementationConfiguration();
        atomicComponent.stop();
    }

    public InstanceWrapper createInstanceWrapper(RuntimeComponent component) {
        JavaComponentInfo atomicComponent = (JavaComponentInfo)component.getImplementationConfiguration();
        return atomicComponent.createInstanceWrapper();
    }

    public boolean isEagerInit(RuntimeComponent component) {
        return isEagerInit();
    }

}
