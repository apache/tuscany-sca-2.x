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

package org.apache.tuscany.sca.binding.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.sca.core.invocation.MessageImpl;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.rmi.RMIHost;
import org.apache.tuscany.sca.rmi.RMIHostException;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.ServiceListener;
import org.osoa.sca.ServiceRuntimeException;

public class RMIServiceListener implements ServiceListener {

    RuntimeComponent component;
    RuntimeComponentService service;
    private RMIBinding binding;
    RMIHost rmiHost;

    public RMIServiceListener(RuntimeComponent rc, RuntimeComponentService rcs, RMIBinding binding, RMIHost rmiHost) {
        this.component = rc;
        this.service = rcs;
        this.binding = binding;
        this.rmiHost = rmiHost;
    }

    public void start() {
        // URI uri = URI.create(component.getURI() + "/" + binding.getName());
        // binding.setURI(uri.toString());

        Interface serviceInterface = service.getInterfaceContract().getInterface();

        Remote rmiProxy = createRmiService(serviceInterface);

        try {

            rmiHost.registerService(binding.getServiceName(), getPort(binding.getPort()), rmiProxy);

        } catch (RMIHostException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
    }

    protected int getPort(String port) {
        int portNumber = RMIHost.RMI_DEFAULT_PORT;
        if (port != null && port.length() > 0) {
            portNumber = Integer.decode(port);
        }
        return portNumber;
    }

    protected Remote createRmiService(final Interface serviceInterface) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object arg0, Method method, Object[] args, MethodProxy arg3) throws Throwable {
                return invokeTarget(JavaInterfaceUtil.findOperation(method, serviceInterface.getOperations()), args);
            }
        });
        Class targetJavaInterface = getTargetJavaClass(serviceInterface);
        if (!Remote.class.isAssignableFrom(targetJavaInterface)) {
            RMIServiceClassLoader classloader = new RMIServiceClassLoader(getClass().getClassLoader());
            final byte[] byteCode = generateRemoteInterface(targetJavaInterface);
            targetJavaInterface = classloader.defineClass(byteCode);
            enhancer.setClassLoader(classloader);
        }
        enhancer.setInterfaces(new Class[] {targetJavaInterface});
        return (Remote)enhancer.create();
    }

    protected Object invokeTarget(Operation op, Object[] args) throws InvocationTargetException {

        Message requestMsg = new MessageImpl();
        ThreadMessageContext.setMessageContext(requestMsg);
        requestMsg.setBody(args);

        Message responseMsg = service.getInvoker(binding, op).invoke(requestMsg);

        if (responseMsg.isFault()) {
            throw new InvocationTargetException((Throwable)responseMsg.getBody());
        }
        return responseMsg.getBody();
    }


    /**
     * if the interface of the component whose serviceBindings must be exposed as RMI Service, does not
     * implement java.rmi.Remote, then generate such an interface. This method will stop with just 
     * generating the bytecode. Defining the class from the byte code must be the responsibility of the 
     * caller of this method, since it requires a classloader to be created to define and load this interface.
     */
    protected byte[] generateRemoteInterface(Class serviceInterface) {
        String interfazeName = serviceInterface.getCanonicalName();
        ClassWriter cw = new ClassWriter(false);

        String simpleName = serviceInterface.getSimpleName();
        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT + Constants.ACC_INTERFACE, interfazeName
            .replace('.', '/'), "java/lang/Object", new String[] {"java/rmi/Remote"}, simpleName + ".java");

        StringBuffer argsAndReturn = null;
        Method[] methods = serviceInterface.getMethods();
        for (Method method : methods) {
            argsAndReturn = new StringBuffer("(");
            Class[] paramTypes = method.getParameterTypes();
            Class returnType = method.getReturnType();

            for (Class paramType : paramTypes) {
                argsAndReturn.append(Type.getType(paramType));
            }
            argsAndReturn.append(")");
            argsAndReturn.append(Type.getType(returnType));

            cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT,
                           method.getName(),
                           argsAndReturn.toString(),
                           new String[] {"java/rmi/RemoteException"},
                           null);
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    protected Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out
        // how to generate Java Interface in cases where the target is not a
        // Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }

    protected class RMIServiceClassLoader extends ClassLoader {
        public RMIServiceClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(byte[] byteArray) {
            return defineClass(null, byteArray, 0, byteArray.length);
        }
    }
}
