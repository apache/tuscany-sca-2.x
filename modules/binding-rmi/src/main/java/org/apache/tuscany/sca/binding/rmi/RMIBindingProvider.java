/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.sca.binding.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.rmi.RMIHost;
import org.apache.tuscany.rmi.RMIHostException;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.spi.component.WorkContextTunnel;

/**
 * RMIBindingProvider
 */
public class RMIBindingProvider implements ReferenceBindingProvider, ServiceBindingProvider, MethodInterceptor {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private RuntimeComponentReference reference;
    private RMIBinding binding;
    private MessageFactory messageFactory;
    private RMIHost rmiHost;

    // need this member to morph the service interface to extend from Remote if
    // it does not
    // the base class's member variable interfaze is to be maintained to enable
    // the connection
    // of the service outbound to the component's inbound wire which requires
    // that the service
    // and the component match in their service contracts.
    private Interface serviceInterface;

    public RMIBindingProvider(RuntimeComponent component,
                              RuntimeComponentService service,
                              RMIBinding binding,
                              MessageFactory messageFactory,
                              RMIHost rmiHost) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.rmiHost = rmiHost;
        this.messageFactory = messageFactory;
    }

    public RMIBindingProvider(RuntimeComponent component,
                              RuntimeComponentReference reference,
                              RMIBinding binding,
                              RMIHost rmiHost) {
        this.component = component;
        this.reference = reference;
        this.binding = binding;
        this.rmiHost = rmiHost;
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (service != null)
            return service.getInterfaceContract();
        else
            return reference.getInterfaceContract();
    }

    public void start() {
        if (service != null) {
            URI uri = URI.create(component.getURI() + "/" + binding.getName());
            binding.setURI(uri.toString());

            this.serviceInterface = service.getInterfaceContract().getInterface();

            Remote rmiProxy = createRmiService();

            try {
                rmiHost.registerService(binding.getRmiServiceName(), getPort(binding.getRmiPort()), rmiProxy);
            } catch (RMIHostException e) {
                throw new NoRemoteServiceException(e);
            }
        }
    }

    public void stop() {
        if (service != null) {
            try {
                rmiHost.unregisterService(binding.getRmiServiceName(), getPort(binding.getRmiPort()));
            } catch (RMIHostException e) {
                throw new NoRemoteServiceException(e.getMessage());
            }
        }
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        try {
            Method remoteMethod = JavaInterfaceUtil.findMethod(((JavaInterface)reference.getInterfaceContract()
                .getInterface()).getJavaClass(), operation);
            return new RMIBindingInvoker(rmiHost, binding.getRmiHostName(), binding.getRmiPort(), binding
                .getRmiServiceName(), remoteMethod);
        } catch (NoSuchMethodException e) {
            throw new NoRemoteMethodException(operation.toString(), e);
        }
    }

    protected Remote createRmiService() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        enhancer.setCallback(this);
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

    // if the interface of the component whose serviceBindings must be exposed
    // as RMI Service, does not
    // implement java.rmi.Remote, then generate such an interface. This method
    // will stop with
    // just generating the bytecode. Defining the class from the byte code must
    // tbe the responsibility
    // of the caller of this method, since it requires a classloader to be
    // created to define and load
    // this interface.
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

    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // since incoming method signatures have 'remotemethod invocation' it
        // will not match with the
        // wired component's method signatures. Hence need to pull in the
        // corresponding method from the
        // component's service contract interface to make this invocation.

        return invokeTarget(JavaInterfaceUtil.findOperation(method, serviceInterface.getOperations()), args);
    }

    public Object invokeTarget(Operation op, Object[] args) throws InvocationTargetException {
        
        Message requestMsg = messageFactory.createMessage();
        requestMsg.setWorkContext(WorkContextTunnel.getThreadWorkContext());
        requestMsg.setBody(args);

        Message responseMsg = service.getInvoker(binding, op).invoke(requestMsg);

        if (responseMsg.isFault()) {
            throw new InvocationTargetException((Throwable)responseMsg.getBody());
        }
        return responseMsg.getBody();
    }

    protected int getPort(String port) {
        int portNumber = RMIHost.RMI_DEFAULT_PORT;
        if (port != null && port.length() > 0) {
            portNumber = Integer.decode(port);
        }

        return portNumber;
    }

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out
        // how to generate Java Interface in cases where the target is not a
        // Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }

    private class RMIServiceClassLoader extends ClassLoader {
        public RMIServiceClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(byte[] byteArray) {
            return defineClass(null, byteArray, 0, byteArray.length);
        }
    }

}
