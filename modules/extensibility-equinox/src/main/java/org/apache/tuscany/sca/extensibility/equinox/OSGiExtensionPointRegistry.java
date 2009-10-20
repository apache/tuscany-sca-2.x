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
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
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
            Bundle bundle = declarationImpl.getBundle();
            /**
             * If this bundle is not in the STARTING, ACTIVE, or STOPPING states or this bundle
             * is a fragment bundle, then this bundle has no valid BundleContext. This method will
             * return null if this bundle has no valid BundleContext
             */
            if ((bundle.getState() & (Bundle.ACTIVE | Bundle.STARTING)) == 0) {
                try {
                    bundle.start();
                } catch (BundleException e) {
                    throw new IllegalStateException(e);
                }
            }
            context = bundle.getBundleContext();
        }
        Dictionary<Object, Object> props = new Hashtable<Object, Object>();
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

    @Override
    public void stop() {
        // Get a unique map as an extension point may exist in the map by different keys
        Map<LifeCycleListener, LifeCycleListener> map = new IdentityHashMap<LifeCycleListener, LifeCycleListener>();
        for (ServiceRegistration reg : services.values()) {
            ServiceReference ref = reg.getReference();
            if (ref != null) {
                Object service = bundleContext.getService(ref);
                if (service instanceof LifeCycleListener) {
                    LifeCycleListener activator = (LifeCycleListener)service;
                    map.put(activator, activator);
                }
                reg.unregister();
            }
        }
        for (LifeCycleListener activator : map.values()) {
            activator.stop();
        }
        services.clear();
    }

}
