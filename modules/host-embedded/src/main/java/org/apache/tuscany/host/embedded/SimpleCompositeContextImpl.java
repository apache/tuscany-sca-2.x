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

package org.apache.tuscany.host.embedded;

import java.net.URI;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.Service;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Temporary here to help the bring up of samples and integration tests that
 * still use the 0.95 CompositeContext interface.
 *
 * @version $Rev$ $Date$
 */
public class SimpleCompositeContextImpl implements CompositeContext {
    
    private SimpleRuntime runtime;
    private Composite composite;
    
    public SimpleCompositeContextImpl(SimpleRuntime runtime, Composite composite) {
        this.runtime = runtime;
        this.composite = composite;
    }

    public ServiceReference createServiceReferenceForSession(Object self) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self, String serviceName) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        if (composite.getName() != null) { 
            return composite.getName().getLocalPart();
        } else {
            return null;
        }
    }

    public RequestContext getRequestContext() {
        throw new UnsupportedOperationException();
    }

    public String getURI() {
        throw new UnsupportedOperationException();
    }

    public <T> T locateService(Class<T> serviceType, String serviceName) {
        String componentName;
        int i = serviceName.indexOf('/');
        if (i == -1) {
            for (Service service: composite.getServices()) {
                CompositeService compositeService = (CompositeService)service;
                if (serviceName.equals(compositeService.getName())) {
                    ComponentService componentService = compositeService.getPromotedService();
                    if (componentService != null) {
                        SCABinding binding = componentService.getBinding(SCABinding.class);
                        if (binding != null) {
                            Component component = binding.getComponent();
                            if (component != null) {
                                ComponentContext context = runtime.getComponentContext(URI.create(component.getName()));
                                if (context == null) {
                                    throw new ServiceRuntimeException("Service not found: " + serviceName);
                                }
                                ServiceReference<T> serviceReference = context.createSelfReference(serviceType);
                                return serviceReference.getService();
                            }
                        }
                    }
                    break;
                }
            }
            throw new ServiceRuntimeException("Service not found: " + serviceName);
            
        } else {
            componentName = serviceName.substring(0, i);
            serviceName = serviceName.substring(i + 1);
            ComponentContext context = runtime.getComponentContext(URI.create(componentName));
            if (context == null) {
                throw new ServiceRuntimeException("Component not found: " + componentName);
            }
            ServiceReference<T> serviceReference = context.createSelfReference(serviceType);
            return serviceReference.getService();
        }
    }

    public ServiceReference newSession(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName, Object sessionId) {
        throw new UnsupportedOperationException();
    }

}
