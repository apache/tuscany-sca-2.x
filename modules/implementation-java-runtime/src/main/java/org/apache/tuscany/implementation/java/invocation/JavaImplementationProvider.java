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

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.core.ImplementationActivator;
import org.apache.tuscany.core.ImplementationProvider;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.context.JavaAtomicComponent;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.context.PojoConfiguration;
import org.apache.tuscany.implementation.java.impl.JavaImplementationImpl;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.TargetInvokerInterceptor;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationProvider extends JavaImplementationImpl implements JavaImplementation, ImplementationProvider,
    ImplementationActivator {
    private JavaPropertyValueObjectFactory propertyValueObjectFactory;
    private DataBindingExtensionPoint dataBindingRegistry;
    private ProxyService proxyService;
    private WorkContext workContext;

    public JavaImplementationProvider(ProxyService proxyService,
                                      WorkContext workContext,
                                      DataBindingExtensionPoint dataBindingRegistry,
                                      JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.proxyService = proxyService;
        this.workContext = workContext;
        this.dataBindingRegistry = dataBindingRegistry;
        this.propertyValueObjectFactory = propertyValueObjectFactory;
    }

    public void configure(RuntimeComponent component) {
        PojoConfiguration configuration = new PojoConfiguration(this);
        configuration.setProxyService(proxyService);
        configuration.setWorkContext(workContext);
        JavaAtomicComponent atomicComponent = new JavaAtomicComponent(configuration);
        atomicComponent.setDataBindingRegistry(dataBindingRegistry);
        atomicComponent.setPropertyValueFactory(propertyValueObjectFactory);
        component.setImplementationConfiguration(atomicComponent);
    }

    /**
     * @see org.apache.tuscany.core.ImplementationProvider#createInstance(org.apache.tuscany.core.RuntimeComponent,
     *      org.apache.tuscany.assembly.ComponentService)
     */
    public Object createInstance(RuntimeComponent component, ComponentService service) {
        JavaAtomicComponent atomicComponent = (JavaAtomicComponent)component.getImplementationConfiguration();
        return atomicComponent.createInstance();
    }

    public Interceptor createInterceptor(RuntimeComponent component,
                                         ComponentService service,
                                         Operation operation,
                                         boolean isCallback) {
        JavaAtomicComponent atomicComponent = (JavaAtomicComponent)component.getImplementationConfiguration();
        try {
            return new TargetInvokerInterceptor(atomicComponent.createTargetInvoker(null, operation, isCallback));
        } catch (TargetInvokerCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public InterfaceContract getImplementationInterfaceContract(ComponentService service) {
        return service.getInterfaceContract();
    }

    /**
     * @see org.apache.tuscany.core.ImplementationProvider#getScope()
     */
    public Scope getScope() {
        return new Scope(getJavaScope().getScope());
    }

    public void start(RuntimeComponent component) {
        JavaAtomicComponent atomicComponent = (JavaAtomicComponent)component.getImplementationConfiguration();
        atomicComponent.start();
    }

    public void stop(RuntimeComponent component) {
        JavaAtomicComponent atomicComponent = (JavaAtomicComponent)component.getImplementationConfiguration();
        atomicComponent.stop();
    }

}
