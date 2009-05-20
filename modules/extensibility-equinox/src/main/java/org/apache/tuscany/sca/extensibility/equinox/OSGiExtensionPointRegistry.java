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

package org.apache.tuscany.sca.extensibility.equinox;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class OSGiExtensionPointRegistry extends DefaultExtensionPointRegistry {
    private Map<Class<?>, ServiceRegistration> services = new ConcurrentHashMap<Class<?>, ServiceRegistration>();
    private BundleContext bundleContext;

    public OSGiExtensionPointRegistry(BundleContext bundleContext) {
        super();
        this.bundleContext = bundleContext;
    }

    @Override
    protected <T> Object findExtensionPoint(Class<T> extensionPointType) {
        ServiceRegistration registration = services.get(extensionPointType);
        if (registration != null) {
            ServiceReference ref = registration.getReference();
            if (ref != null) {
                return ref.getBundle().getBundleContext().getService(ref);
            }
        }
        /*
        else {
            ServiceReference ref = bundleContext.getServiceReference(extensionPointType.getName());
            if (ref != null) {
                return bundleContext.getService(ref);
            }
        }
        */
        return null;
    }

    @Override
    protected void registerExtensionPoint(Class<?> i, Object extensionPoint, ServiceDeclaration declaration) {
        BundleContext context = bundleContext;
        if (declaration instanceof EquinoxServiceDiscoverer.ServiceDeclarationImpl) {
            EquinoxServiceDiscoverer.ServiceDeclarationImpl declarationImpl =
                (EquinoxServiceDiscoverer.ServiceDeclarationImpl)declaration;
            context = declarationImpl.getBundle().getBundleContext();
        }
        Dictionary<Object, Object> props = new java.util.Hashtable<Object, Object>();
        ServiceRegistration registration = context.registerService(i.getName(), extensionPoint, props);
        services.put(i, registration);
    }

    @Override
    protected void unregisterExtensionPoint(Class<?> i) {
        ServiceRegistration registration = services.get(i);
        if (registration != null) {
            registration.unregister();
        }
        services.remove(i);
    }

}
