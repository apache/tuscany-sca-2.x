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
package org.apache.tuscany.binding.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import javax.xml.namespace.QName;

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.rmi.RMIHostException;
import org.apache.tuscany.rmi.RMIHostExtensionPoint;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class RMIServiceBinding<T extends Remote> extends ServiceBindingExtension implements MethodInterceptor { 
    private RMIBinding rmiBinding;
    private RMIHostExtensionPoint rmiHost;

    // need this member to morph the service interface to extend from Remote if it does not
    // the base class's member variable interfaze is to be maintained to enable the connection
    // of the service outbound to the component's inbound wire which requires that the service
    // and the component match in their service contracts.
    private Interface serviceInterface;
    
    public RMIServiceBinding(URI name,
                             RMIBinding rmiBinding,
                             RMIHostExtensionPoint rHost,
                             Interface svcIfc) {
        super(name);
        //this.serviceInterface = service;
        this.rmiHost = rHost;
        this.rmiBinding = rmiBinding;
        this.serviceInterface = svcIfc;
        
        start(); // TODO: hack while start isn't getting called by runtime
    }

    public void start() {
        super.start();
        Remote rmiProxy = createRmiService();

        try {
            rmiHost.registerService(rmiBinding.getRmiServiceName(),
                                    getPort(rmiBinding.getRmiPort()),
                                    rmiProxy);
        } catch (RMIHostException e) {
            throw new NoRemoteServiceException(e);
        }
    }

    public void stop() {
        try {
            rmiHost.unregisterService(rmiBinding.getRmiServiceName(), 
                                      getPort(rmiBinding.getRmiPort()));
        } catch (RMIHostException e) {
            throw new NoRemoteServiceException(e.getMessage());
        }
        super.stop();
    }

    protected Remote createRmiService() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        enhancer.setCallback(this);
        Class targetJavaInterface = getTargetJavaClass(serviceInterface);
        if (!Remote.class.isAssignableFrom(targetJavaInterface)) {
            RMIServiceClassLoader classloader =
                new RMIServiceClassLoader(getClass().getClassLoader());
            final byte[] byteCode = generateRemoteInterface(targetJavaInterface);
            targetJavaInterface = classloader.defineClass(byteCode);
            enhancer.setClassLoader(classloader);
        }
        enhancer.setInterfaces(new Class[]{targetJavaInterface});
        return (Remote) enhancer.create();
    }

    protected int getPort(String port) {
        int portNumber = RMIHostExtensionPoint.RMI_DEFAULT_PORT;
        if (port != null && port.length() > 0) {
            portNumber = Integer.decode(port);
        }

        return portNumber;
    }

    // if the interface of the component whose serviceBindings must be exposed as RMI Service, does not
    // implement java.rmi.Remote, then generate such an interface. This method will stop with
    // just generating the bytecode. Defining the class from the byte code must tbe the responsibility
    // of the caller of this method, since it requires a classloader to be created to define and load
    // this interface.
    protected byte[] generateRemoteInterface(Class serviceInterface) {
        String interfazeName = serviceInterface.getCanonicalName();
        ClassWriter cw = new ClassWriter(false);

        String simpleName = serviceInterface.getSimpleName();
        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT + Constants.ACC_INTERFACE,
            interfazeName.replace('.', '/'), "java/lang/Object", new String[]{"java/rmi/Remote"}, simpleName + ".java");

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

            cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT, method.getName(), argsAndReturn.toString(),
                new String[]{"java/rmi/RemoteException"}, null);
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    public QName getBindingType() {
        return RMIBindingConstants.BINDING_RMI_QNAME;
    }

    private class RMIServiceClassLoader extends ClassLoader {
        public RMIServiceClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(byte[] byteArray) {
            return defineClass(null, byteArray, 0, byteArray.length);
        }
    }
    
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // since incoming method signatures have 'remotemethod invocation' it will not match with the
        // wired component's method signatures. Hence need to pull in the corresponding method from the
        // component's service contract interface to make this invocation.
        
        
        return invokeTarget(JavaInterfaceUtil.findOperation(method, serviceInterface.getOperations()), 
                                                            args);
    }
    
    public Object invokeTarget(Operation op, 
                               Object[] args) throws InvocationTargetException {
        InvocationChain chain = null;
        
        for (InvocationChain ic : wire.getInvocationChains()) {
            if (ic.getSourceOperation().equals(op)) {
                chain = ic;
            }
        }
        if (chain == null) {
            throw new IllegalStateException("no InvocationChain on wire for operation " + op);
        }
        
        Interceptor headInterceptor = chain.getHeadInterceptor();
        WorkContext workContext = WorkContextTunnel.getThreadWorkContext();
        if (workContext == null) {
            workContext = new SimpleWorkContext();
            workContext.setIdentifier(Scope.COMPOSITE, ComponentNames.TUSCANY_APPLICATION_ROOT.resolve("default"));
            WorkContextTunnel.setThreadWorkContext(workContext);
        }
        
        String oldConversationID = (String) workContext.getIdentifier(Scope.CONVERSATION);
        
        try {
            if (headInterceptor == null) {
                // short-circuit the dispatch and invoke the target directly
                TargetInvoker targetInvoker = chain.getTargetInvoker();
                if (targetInvoker == null) {
                    throw new AssertionError("No target invoker [" + chain.getTargetOperation().getName() + "]");
                }
                return targetInvoker.invokeTarget(args, TargetInvoker.NONE, null);
            } else {
                Message msg = new MessageImpl();
                msg.setTargetInvoker(chain.getTargetInvoker());
                msg.setBody(args);
                msg.setWorkContext(workContext);

                Message resp;
                // dispatch the wire down the chain and get the response
                resp = headInterceptor.invoke(msg);
                Object body = resp.getBody();
                if (resp.isFault()) {
                    throw new InvocationTargetException((Throwable) body);
                }
                return body;
            }
        } finally {
        }
    }
    
    private Class<?> getTargetJavaClass(Interface targetInterface) {
        //TODO: right now assume that the target is always a Java Implementation.  Need to figure out
        // how to generate Java Interface in cases where the target is not a Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }
    
    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) throws TargetInvokerCreationException {
        return null;
    }


}
