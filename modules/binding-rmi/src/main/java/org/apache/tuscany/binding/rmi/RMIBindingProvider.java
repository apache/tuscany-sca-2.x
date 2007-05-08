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

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.invocation.Message;
import org.apache.tuscany.invocation.MessageImpl;
import org.apache.tuscany.provider.ReferenceBindingProvider;
import org.apache.tuscany.provider.ServiceBindingProvider;
import org.apache.tuscany.rmi.RMIHost;
import org.apache.tuscany.rmi.RMIHostException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;


/**
 * 
 * RMIBindingProvider
 *
 * @version $Rev: $ $Date: $
 */
public class RMIBindingProvider implements ReferenceBindingProvider<RMIBinding>,
    ServiceBindingProvider<RMIBinding>, MethodInterceptor {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private RuntimeComponentReference reference;
    private RMIBinding binding;
    private RMIHost rmiHost;
    private RuntimeWire wire;
    
    //need this member to morph the service interface to extend from Remote if it does not
    // the base class's member variable interfaze is to be maintained to enable the connection
    // of the service outbound to the component's inbound wire which requires that the service
    // and the component match in their service contracts.
    private Interface serviceInterface;
    
    public RMIBindingProvider(RuntimeComponent component, RuntimeComponentService service, RMIBinding binding, RMIHost rmiHost) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.rmiHost = rmiHost;
    }

    public RMIBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, RMIBinding binding, RMIHost rmiHost) {
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
            
            //TODO : Need to figure out why we do a get(0)... will this work always...
            this.wire = service.getRuntimeWires().get(0);
            this.serviceInterface = service.getInterfaceContract().getInterface();
            
            Remote rmiProxy = createRmiService();
            
            try {
                rmiHost.registerService(binding.getRmiServiceName(),
                                        getPort(binding.getRmiPort()),
                                        rmiProxy);
            } catch (RMIHostException e) {
                throw new NoRemoteServiceException(e);
            }
        }
    }

    public void stop() {
        if (service != null) {
            try {
                rmiHost.unregisterService(binding.getRmiServiceName(), 
                                          getPort(binding.getRmiPort()));
            } catch (RMIHostException e) {
                throw new NoRemoteServiceException(e.getMessage());
            }
        }
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
       try {
            Method remoteMethod = 
                JavaInterfaceUtil.findMethod(((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass(),
                                                operation);
            return new RMIBindingInvoker(rmiHost, 
                                             binding.getRmiHostName(), 
                                             binding.getRmiPort(), 
                                             binding.getRmiServiceName(), 
                                             remoteMethod);
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
            RMIServiceClassLoader classloader =
                new RMIServiceClassLoader(getClass().getClassLoader());
            final byte[] byteCode = generateRemoteInterface(targetJavaInterface);
            targetJavaInterface = classloader.defineClass(byteCode);
            enhancer.setClassLoader(classloader);
        }
        enhancer.setInterfaces(new Class[]{targetJavaInterface});
        return (Remote) enhancer.create();
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
        
        Invoker headInvoker = chain.getHeadInvoker();
        WorkContext workContext = WorkContextTunnel.getThreadWorkContext();
        
        Message msg = new MessageImpl();
        msg.setBody(args);
        msg.setWorkContext(workContext);

        Message resp;
        // dispatch the wire down the chain and get the response
        resp = headInvoker.invoke(msg);
        Object body = resp.getBody();
        if (resp.isFault()) {
            throw new InvocationTargetException((Throwable) body);
        }
        return body;
    }
    
    protected int getPort(String port) {
        int portNumber = RMIHost.RMI_DEFAULT_PORT;
        if (port != null && port.length() > 0) {
            portNumber = Integer.decode(port);
        }

        return portNumber;
    }
    
    private Class<?> getTargetJavaClass(Interface targetInterface) {
        //TODO: right now assume that the target is always a Java Implementation.  Need to figure out
        // how to generate Java Interface in cases where the target is not a Java Implementation
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
