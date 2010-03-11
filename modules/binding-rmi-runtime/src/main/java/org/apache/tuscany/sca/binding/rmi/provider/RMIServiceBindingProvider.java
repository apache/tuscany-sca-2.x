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

package org.apache.tuscany.sca.binding.rmi.provider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.common.java.classloader.ClassLoaderDelegate;
import org.apache.tuscany.sca.host.rmi.RMIHost;
import org.apache.tuscany.sca.host.rmi.RMIHostException;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Implementation of a Service for the RMIBinding.
 *
 * @version $Rev$ $Date$
 */
public class RMIServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private RMIBinding binding;
    private RMIHost rmiHost;
    private RuntimeEndpoint endpoint;
    private Remote rmiProxy;

    public RMIServiceBindingProvider(RuntimeEndpoint endpoint, RMIHost rmiHost) {
        this.endpoint = endpoint;
        this.component = (RuntimeComponent)endpoint.getComponent();
        this.service = (RuntimeComponentService)endpoint.getService();
        this.binding = (RMIBinding)endpoint.getBinding();
        this.rmiHost = rmiHost;
    }

    public void start() {
        
        Interface serviceInterface = service.getInterfaceContract().getInterface();

        rmiProxy = createRmiService(serviceInterface);

        try {

            String uri = rmiHost.registerService(binding.getURI(), rmiProxy);
            // Update the binding with the physical URI
            binding.setURI(uri);

        } catch (RMIHostException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
        rmiHost.unregisterService(binding.getURI());
        try {
            UnicastRemoteObject.unexportObject(rmiProxy, false);
        } catch (NoSuchObjectException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private int getPort(String port) {
        int portNumber = RMIHost.RMI_DEFAULT_PORT;
        if (port != null && port.length() > 0) {
            portNumber = Integer.decode(port);
        }
        return portNumber;
    }

    private Remote createRmiService(final Interface serviceInterface) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object arg0, Method method, Object[] args, MethodProxy arg3) throws Throwable {
                try {
                    return invokeTarget(JavaInterfaceUtil.findOperation(method, serviceInterface.getOperations()), args);
                } catch (InvocationTargetException e) {
                    final Throwable cause = e.getCause();
                    for (Class<?> declaredType : method.getExceptionTypes()) {
                        if (declaredType.isInstance(cause)) {
                            throw e;
                        }
                    }

                    if (cause.getCause() != null) {
                        // TUSCANY-2545: don't inlcude nested cause object
                        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                            public Object run() throws Exception {
                                Field field = Throwable.class.getDeclaredField("cause");
                                field.setAccessible(true);
                                field.set(cause, null);
                                field.setAccessible(false);
                                return null;
                            }
                        });
                    }

                    throw cause;
                }
            }
        });
        Class<?> targetJavaInterface = getTargetJavaClass(serviceInterface);
        targetJavaInterface = RemoteInterfaceGenerator.generate(targetJavaInterface);
        /*
         * In OSGi, the classloader for the interface cannot access the classes for the CGLIB  
         */
        enhancer.setClassLoader(new ClassLoaderDelegate(targetJavaInterface.getClassLoader(), getClass().getClassLoader()));
        enhancer.setInterfaces(new Class[] {targetJavaInterface});
        return (Remote)enhancer.create();
    }
    
    private Object invokeTarget(Operation op, Object[] args) throws InvocationTargetException {
        return endpoint.invoke(op, args);
    }

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out
        // how to generate Java Interface in cases where the target is not a
        // Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }
    
}
