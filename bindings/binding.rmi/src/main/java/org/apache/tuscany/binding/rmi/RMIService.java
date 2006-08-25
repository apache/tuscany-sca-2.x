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

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.proxy.Enhancer;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.host.RMIHost;
import org.apache.tuscany.spi.host.RemoteServiceException;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class RMIService<T extends Remote> extends ServiceExtension<T> {
    public static final String URI_PREFIX = "//localhost";

    public static final String SLASH = "/";
 
    public static final String COLON = ":";

    private final String host;

    private final String port;

    private final String serviceName;

    private RMIHost rmiHost;

    // need this member to morph the service interface to extend from Remote if it does not
    // the base class's member variable interfaze is to be maintained to enable the connection
    // of the service outbound to the component's inbound wire which requires that the service
    // and the component match in their service contracts.
    private Class serviceInterface;

    public RMIService(String name,
                      CompositeComponent parent,
                      WireService wireService,
                      RMIHost rHost,
                      String host,
                      String port,
                      String svcName,
                      Class<T> service) {
        super(name, service, parent, wireService);

        this.serviceInterface = service;
        this.rmiHost = rHost;
        this.host = host;
        this.port = port;
        this.serviceName = svcName;
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
        } catch (RemoteServiceException e) {
            throw new NoRemoteServiceException(e);
        }
    }

    public void stop() {
        try {
            rmiHost.unregisterService(serviceName);
        } catch (RemoteServiceException e) {
            throw new NoRemoteServiceException(e.getMessage());
        }
        super.stop();
    }

    private Remote createRmiService() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        enhancer.setCallback(new RemoteMethodHandler(getHandler(), interfaze));

        if (!Remote.class.isAssignableFrom(serviceInterface)) {
            RMIServiceClassLoader classloader = new RMIServiceClassLoader();
            final byte[] byteCode = generateRemoteInterface(serviceInterface);
            serviceInterface = classloader.defineClass(byteCode);
            enhancer.setClassLoader(classloader);
        }
        enhancer.setInterfaces(new Class[]{serviceInterface});
        return (Remote) enhancer.create();
    }

    private int getPort(String port) {
        int portNumber = RMIHost.RMI_DEFAULT_PORT;
        if (port != null && port.length() > 0) {
            portNumber = Integer.decode(port).intValue();
        }

        return portNumber;
    }

    // if the interface of the component whose services must be exposed as RMI Service, does not
    // implement java.rmi.Remote, then generate such an interface. This method will stop with
    // just generating the bytecode. Defining the class from the byte code must tbe the responsibility
    // of the caller of this method, since it requires a classloader to be created to define and load
    // this interface.
    private byte[] generateRemoteInterface(Class serviceInterface) {
        String interfazeName = serviceInterface.getCanonicalName();
        ClassWriter cw = new ClassWriter(false);

        cw.visit(Constants.V1_5,
                 Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT + Constants.ACC_INTERFACE,
                 interfazeName.replace('.',
                                       '/'),
                 "java/lang/Object",
                 new String[]{"java/rmi/Remote"},
                 serviceInterface.getSimpleName() + ".java");

        StringBuffer argsAndReturn = new StringBuffer("(");
        Method[] methods = serviceInterface.getMethods();
        for (int count = 0; count < methods.length; ++count) {
            Class[] paramTypes = methods[count].getParameterTypes();
            Class returnType = methods[count].getReturnType();

            for (int paramCount = 0; paramCount < paramTypes.length; ++paramCount) {
                argsAndReturn.append(Type.getType(paramTypes[paramCount]));
            }
            argsAndReturn.append(")");
            argsAndReturn.append(Type.getType(returnType));

            cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT,
                           methods[count].getName(),
                           argsAndReturn.toString(),
                           new String[]{"java/rmi/RemoteException"},
                           null);
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    private class RMIServiceClassLoader extends ClassLoader {
        public Class defineClass(byte[] byteArray) {
            return defineClass(null,
                               byteArray,
                               0,
                               byteArray.length);
        }

    }
}
