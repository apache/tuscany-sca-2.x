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

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.wire.WireService;

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.proxy.Enhancer;
import org.apache.tuscany.host.rmi.RMIHost;
import org.apache.tuscany.host.rmi.RMIHostException;

/**
 * @version $Rev$ $Date$
 */
public class RMIServiceBinding<T extends Remote> extends ServiceBindingExtension {
    private static final QName BINDING_RMI = new QName(
        "http://tuscany.apache.org/xmlns/binding/rmi/1.0-SNAPSHOT", "binding.rmi");

    public static final String URI_PREFIX = "//localhost";
    public static final String SLASH = "/";
    public static final String COLON = ":";
    //private final String host;
    private final String port;
    private final String serviceName;
    private RMIHost rmiHost;

    // need this member to morph the service interface to extend from Remote if it does not
    // the base class's member variable interfaze is to be maintained to enable the connection
    // of the service outbound to the component's inbound wire which requires that the service
    // and the component match in their service contracts.
    private Class serviceInterface;
    private WireService wireService;

    public RMIServiceBinding(String name,
                             CompositeComponent parent,
                             WireService wireService,
                             RMIHost rHost,
                             String host,
                             String port,
                             String svcName,
                             Class<T> service) {
        super(name, parent);

        this.serviceInterface = service;
        this.rmiHost = rHost;
        //this.host = host;
        this.port = port;
        this.serviceName = svcName;
        this.wireService = wireService;
    }

    public void start() {
        super.start();
        Remote rmiProxy = createRmiService();

        try {
            // startRMIRegistry();
            rmiHost.registerService(serviceName,
                getPort(port),
                rmiProxy);
            // bindRmiService(uri,rmiProxy);
        } catch (RMIHostException e) {
            throw new NoRemoteServiceException(e);
        }
    }

    public void stop() {
        try {
            rmiHost.unregisterService(serviceName, getPort(port));
        } catch (RMIHostException e) {
            throw new NoRemoteServiceException(e.getMessage());
        }
        super.stop();
    }

    protected Remote createRmiService() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        enhancer.setCallback(new RemoteMethodHandler(wireService.createHandler(serviceInterface, getInboundWire()),
            serviceInterface));

        if (!Remote.class.isAssignableFrom(serviceInterface)) {
            RMIServiceClassLoader classloader =
                new RMIServiceClassLoader(getClass().getClassLoader());
            final byte[] byteCode = generateRemoteInterface(serviceInterface);
            serviceInterface = classloader.defineClass(byteCode);
            enhancer.setClassLoader(classloader);
        }
        enhancer.setInterfaces(new Class[]{serviceInterface});
        return (Remote) enhancer.create();
    }

    protected int getPort(String port) {
        int portNumber = RMIHost.RMI_DEFAULT_PORT;
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
        return BINDING_RMI;
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
