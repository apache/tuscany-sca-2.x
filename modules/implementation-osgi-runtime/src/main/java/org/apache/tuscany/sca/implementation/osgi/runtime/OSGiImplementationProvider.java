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

package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Extensible;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceRuntimeException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class OSGiImplementationProvider implements ImplementationProvider {
    private RuntimeComponent component;
    private ProxyFactoryExtensionPoint proxyFactoryExtensionPoint;
    private Bundle osgiBundle;
    private OSGiImplementation implementation;

    public OSGiImplementationProvider(RuntimeComponent component,
                                      OSGiImplementation impl,
                                      ProxyFactoryExtensionPoint proxyFactoryExtensionPoint) throws BundleException {
        this.component = component;
        this.proxyFactoryExtensionPoint = proxyFactoryExtensionPoint;
        this.implementation = impl;
        this.osgiBundle = impl.getBundle();
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        return new OSGiTargetInvoker(operation, this, service);
    }

    public void start() {
        // First try to start the osgi bundle
        try {
            int state = osgiBundle.getState();
            if ((state & Bundle.STARTING) == 0 && (state & Bundle.ACTIVE) == 0) {
                osgiBundle.start();
            }
        } catch (BundleException e) {
            throw new ServiceRuntimeException(e);
        }
        for (ComponentReference ref : component.getReferences()) {
            RuntimeComponentReference reference = (RuntimeComponentReference)ref;
            InterfaceContract interfaceContract = reference.getInterfaceContract();
            JavaInterface javaInterface = (JavaInterface)interfaceContract.getInterface();
            final Class<?> interfaceClass = javaInterface.getJavaClass();

            final Hashtable<String, Object> osgiProps = getOSGiProperties(reference);
            osgiProps.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
            osgiProps.put("sca.reference", component.getURI() + "#reference(" + ref.getName() + ")");
            osgiProps.put(OSGiProperty.OSGI_REMOTE, "true");
            osgiProps.put(OSGiProperty.OSGI_REMOTE_CONFIGURATION_TYPE, "sca");
            osgiProps.put(OSGiProperty.OSGI_REMOTE_INTERFACES, interfaceClass.getName());

            ProxyFactory proxyService = proxyFactoryExtensionPoint.getInterfaceProxyFactory();
            if (!interfaceClass.isInterface()) {
                proxyService = proxyFactoryExtensionPoint.getClassProxyFactory();
            }

            for (RuntimeWire wire : reference.getRuntimeWires()) {
                final Object proxy = proxyService.createProxy(interfaceClass, wire);
                AccessController.doPrivileged(new PrivilegedAction<ServiceRegistration>() {
                    public ServiceRegistration run() {
                        return osgiBundle.getBundleContext()
                            .registerService(interfaceClass.getName(), proxy, osgiProps);
                    }
                });
            }

        }
    }

    public void stop() {
        // Do we have to unregister the services?
        try {
            int state = osgiBundle.getState();
            if ((state & Bundle.STOPPING) == 0 && (state & Bundle.ACTIVE) != 0) {
                osgiBundle.stop();
            }
        } catch (BundleException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    /**
     * Get all the OSGi properties from the extension list
     * @param extensible
     * @return
     */
    protected Hashtable<String, Object> getOSGiProperties(Extensible extensible) {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        for (Object ext : extensible.getExtensions()) {
            if (ext instanceof OSGiProperty) {
                OSGiProperty p = (OSGiProperty)ext;
                props.put(p.getName(), p.getValue());
            }
        }
        return props;
    }

    RuntimeComponent getComponent() {
        return component;
    }

    OSGiImplementation getImplementation() {
        return implementation;
    }

}
