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

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.cglib.core.Constants;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

/**
 * 
 */
public class RemoteInterfaceGenerator {
    private final static Map<Class<?>, Class<? extends Remote>> remoteInterfaces =
        Collections.synchronizedMap(new WeakHashMap<Class<?>, Class<? extends Remote>>());

    static class RemoteInterfaceClassLoader extends ClassLoader {
        public RemoteInterfaceClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] byteArray) {
            return defineClass(name, byteArray, 0, byteArray.length);
        }
    }

    /**
     * if the interface of the component whose serviceBindings must be exposed as RMI Service, does not
     * implement java.rmi.Remote, then generate such an interface. This method will stop with just 
     * generating the bytecode. Defining the class from the byte code must be the responsibility of the 
     * caller of this method, since it requires a ClassLoader to be created to define and load this interface.
     */
    private static byte[] generateRemoteInterface(Class<?> serviceInterface) {
        String interfazeName = serviceInterface.getName();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT + Constants.ACC_INTERFACE, interfazeName
            .replace('.', '/'), null, "java/lang/Object", new String[] {"java/rmi/Remote"});

        StringBuffer argsAndReturn = null;
        Method[] methods = serviceInterface.getMethods();
        for (Method method : methods) {
            argsAndReturn = new StringBuffer("(");
            Class<?>[] paramTypes = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();

            for (Class<?> paramType : paramTypes) {
                argsAndReturn.append(Type.getType(paramType));
            }
            argsAndReturn.append(")");
            argsAndReturn.append(Type.getType(returnType));

            cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT,
                           method.getName(),
                           argsAndReturn.toString(),
                           null,
                           new String[] {"java/rmi/RemoteException"});
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    public static Class<? extends Remote> generate(Class<?> javaInterface) {
        if (!Remote.class.isAssignableFrom(javaInterface)) {
            Class<? extends Remote> remote = remoteInterfaces.get(javaInterface);
            if (remote == null) {
                RemoteInterfaceClassLoader classloader = new RemoteInterfaceClassLoader(javaInterface.getClassLoader());
                final byte[] byteCode = generateRemoteInterface(javaInterface);
                javaInterface = classloader.defineClass(javaInterface.getName(), byteCode);
                remote = (Class<? extends Remote>)javaInterface;
                remoteInterfaces.put(javaInterface, remote);
            }
            return remote;
        } else {
            return (Class<? extends Remote>)javaInterface;
        }
    }

}
