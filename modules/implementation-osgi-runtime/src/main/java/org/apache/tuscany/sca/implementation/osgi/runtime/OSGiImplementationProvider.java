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

import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.REMOTE_CONFIG_SCA;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SCA_REFERENCE;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SERVICE_IMPORTED;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SERVICE_IMPORTED_CONFIGS;
import static org.osgi.framework.Constants.SERVICE_RANKING;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Extensible;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class OSGiImplementationProvider implements ImplementationProvider {
    private RuntimeComponent component;
    private ProxyFactoryExtensionPoint proxyFactoryExtensionPoint;
    private Bundle osgiBundle;
    private boolean startedByMe;
    private OSGiImplementation implementation;
    private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();
    private OSGiImplementationFactory implementationFactory;

    public OSGiImplementationProvider(RuntimeComponent component,
                                      OSGiImplementation impl,
                                      ProxyFactoryExtensionPoint proxyFactoryExtensionPoint,
                                      OSGiImplementationFactory implementationFactory) throws BundleException {
        this.component = component;
        this.proxyFactoryExtensionPoint = proxyFactoryExtensionPoint;
        this.implementationFactory = implementationFactory;
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
                startedByMe = true;
            }
        } catch (BundleException e) {
            throw new ServiceRuntimeException(e);
        }

        for (ComponentReference ref : component.getReferences()) {
            RuntimeComponentReference reference = (RuntimeComponentReference)ref;
            InterfaceContract interfaceContract = reference.getInterfaceContract();
            final JavaInterface javaInterface = (JavaInterface)interfaceContract.getInterface();
            // final Class<?> interfaceClass = javaInterface.getJavaClass();

            //            final Hashtable<String, Object> props = new Hashtable<String, Object>();
            //            props.put(FILTER_MATCH_CRITERIA, "");
            //            Collection<String> interfaceNames = new ArrayList<String>();
            //            props.put(INTERFACE_MATCH_CRITERIA, interfaceNames);
            //            interfaceNames.add(interfaceClass.getName());

            final Hashtable<String, Object> osgiProps = getOSGiProperties(reference);
            osgiProps.put(SERVICE_RANKING, Integer.MAX_VALUE);
            osgiProps.put(SCA_REFERENCE, component.getURI() + "#reference(" + ref.getName() + ")");
            osgiProps.put(SERVICE_IMPORTED, "true");
            osgiProps.put(SERVICE_IMPORTED_CONFIGS, new String[] {REMOTE_CONFIG_SCA});

            for (EndpointReference epr : reference.getEndpointReferences()) {
                final OSGiServiceFactory serviceFactory = new OSGiServiceFactory(javaInterface.getName(), epr);
                ServiceRegistration registration =
                    AccessController.doPrivileged(new PrivilegedAction<ServiceRegistration>() {
                        public ServiceRegistration run() {
                            // Register the proxy as OSGi service
                            BundleContext context = osgiBundle.getBundleContext();
                            ServiceRegistration registration =
                                context.registerService(javaInterface.getName(), serviceFactory, osgiProps);
                            return registration;
                        }
                    });
                registrations.add(registration);
            }
        }

        // Set the OSGi service reference properties into the SCA service
        for (ComponentService service : component.getServices()) {
            // The properties might have been set by the export service
            boolean found = false;
            for (Object ext : service.getExtensions()) {
                if (ext instanceof OSGiProperty) {
                    found = true;
                    break;
                }
            }
            if (found) {
                continue;
            }
            ServiceReference serviceReference = getServiceReference(osgiBundle.getBundleContext(), service);
            if (serviceReference != null) {
                service.getExtensions().addAll(implementationFactory.createOSGiProperties(serviceReference));
            }
        }
    }

    public void stop() {
        for (ServiceRegistration registration : registrations) {
            try {
                registration.unregister();
            } catch (IllegalStateException e) {
                // The service has been unregistered, ignore it
            }
        }
        registrations.clear();
        // [REVIEW] Shoud it take care of stopping the bundle?
        if (startedByMe) {
            try {
                int state = osgiBundle.getState();
                if ((state & Bundle.STOPPING) == 0 && (state & Bundle.ACTIVE) != 0) {
                    osgiBundle.stop();
                }
            } catch (BundleException e) {
                throw new ServiceRuntimeException(e);
            } finally {
                startedByMe = false;
            }
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

    protected Object getOSGiService(ComponentService service) {
        BundleContext bundleContext = osgiBundle.getBundleContext();
        ServiceReference ref = getServiceReference(bundleContext, service);
        if (ref != null) {
            Object instance = bundleContext.getService(ref);
            return instance;
        } else {
            return null;
        }
    }

    private ServiceReference getServiceReference(BundleContext bundleContext, ComponentService service) {
        JavaInterface javaInterface = (JavaInterface)service.getInterfaceContract().getInterface();
        // String filter = getOSGiFilter(provider.getOSGiProperties(service));
        // FIXME: What is the filter?
        String filter = "(!(" + SERVICE_IMPORTED + "=*))";
        // "(sca.service=" + component.getURI() + "#service-name\\(" + service.getName() + "\\))";
        ServiceReference ref;
        try {
            ref = bundleContext.getServiceReferences(javaInterface.getName(), filter)[0];
        } catch (InvalidSyntaxException e) {
            throw new ServiceRuntimeException(e);
        }
        return ref;
    }

    RuntimeComponent getComponent() {
        return component;
    }

    OSGiImplementation getImplementation() {
        return implementation;
    }

    /**
     * A proxy invocation handler that wrap exceptions into OSGi ServiceException
     */
    private static class InvocationHandlerDelegate implements InvocationHandler {
        private final Object instance;

        public InvocationHandlerDelegate(Object instance) {
            super();
            this.instance = instance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!Proxy.isProxyClass(instance.getClass())) {
                Method m = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
                try {
                    return m.invoke(instance, args);
                } catch (InvocationTargetException e) {
                    wrapException(method, e.getCause());
                    return null;
                }
            } else {
                InvocationHandler handler = Proxy.getInvocationHandler(instance);
                try {
                    return handler.invoke(instance, method, args);
                } catch (Throwable e) {
                    wrapException(method, e);
                    return null;
                }
            }
        }

        private void wrapException(Method method, Throwable e) throws Throwable {
            for (Class<?> exType : method.getExceptionTypes()) {
                if (exType.isInstance(e)) {
                    throw e;
                }
            }
            throw new ServiceException(e.getMessage(), ServiceException.REMOTE, e);
        }

        /**
         * A utility to cast the object to the given interface. If the class for the object
         * is loaded by a different classloader, a proxy will be created.
         *
         * @param <T>
         * @param obj
         * @param cls
         * @return
         */
        static <T> T cast(Object obj, Class<T> cls) {
            if (obj == null) {
                return null;
            } else {
                return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(),
                                                       new Class<?>[] {cls},
                                                       new InvocationHandlerDelegate(obj)));
            }
        }
    }

    public class OSGiServiceFactory implements ServiceFactory {
        private RuntimeEndpointReference epr;
        private String interfaceName;

        /**
         * @param interfaceName
         * @param epr
         */
        public OSGiServiceFactory(String interfaceName, EndpointReference epr) {
            super();
            this.interfaceName = interfaceName;
            this.epr = (RuntimeEndpointReference)epr;
        }

        public Object getService(Bundle bundle, ServiceRegistration registration) {
            try {
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = bundle.loadClass(interfaceName);
                } catch (ClassNotFoundException e) {
                    return null;
                }
                ProxyFactory proxyService = proxyFactoryExtensionPoint.getInterfaceProxyFactory();
                if (!interfaceClass.isInterface()) {
                    proxyService = proxyFactoryExtensionPoint.getClassProxyFactory();
                }
                Object proxy = proxyService.createProxy(interfaceClass, epr);
                return InvocationHandlerDelegate.cast(proxy, interfaceClass);
            } catch (Throwable e) {
                throw new ServiceException(e.getMessage(), ServiceException.FACTORY_EXCEPTION, e);
            }
        }

        public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            // Do we need to release the proxy?
        }

    }

}
