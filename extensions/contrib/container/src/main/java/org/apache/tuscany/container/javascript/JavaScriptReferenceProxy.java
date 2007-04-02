/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.     
 */
package org.apache.tuscany.container.javascript;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.CodeVisitor;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;

import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.xml.XMLObject;

/**
 * This is a proxy that will mediate reference calls from the JavaScript. The mediation code here will be reviewed when the DataMediation
 * infrastructure is ready. This proxy assmes that there is no verloading of service methods on the reference interface i.e. there are no two service
 * methods that have the same method name or operation name.
 */
public class JavaScriptReferenceProxy {

    private Class interfaze;

    private Object wireProxy;

    private Scriptable instanceScope;

    public JavaScriptReferenceProxy(Class interfaze, Object wireProxy, Scriptable instScope) {
        this.interfaze = interfaze;
        this.wireProxy = wireProxy;
        this.instanceScope = instScope;
    }

    public Object createProxy() {
        try {
            GenericProxyClassLoader classloader = new GenericProxyClassLoader();
            final byte[] byteCode = generateGenericInterface(interfaze);

            Class genericInterface = classloader.defineClass(byteCode);
            InvocationHandler proxyHandler = new JavaScriptRefInvocInterceptor(wireProxy,
                    interfaze, instanceScope);
            // return genericInterface.cast(Proxy.newProxyInstance(classloader, new Class[]{genericInterface}, proxyHandler));
            return Proxy.newProxyInstance(classloader,
                                          new Class[]{genericInterface},
                                          proxyHandler);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] generateGenericInterface(Class serviceInterface) {
        String interfazeName = serviceInterface.getCanonicalName();
        ClassWriter cw = new ClassWriter(false);

        cw.visit(Constants.V1_5,
                 Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT + Constants.ACC_INTERFACE,
                 interfazeName.replace('.',
                                       '/'),
                 "java/lang/Object",
                 null,
                 serviceInterface.getSimpleName() + ".java");

        StringBuffer argsAndReturn = new StringBuffer("(");
        Method[] methods = serviceInterface.getMethods();
        for (int count = 0; count < methods.length; ++count) {
            argsAndReturn = new StringBuffer("(");
            Class[] paramTypes = methods[count].getParameterTypes();
            Class returnType = methods[count].getReturnType();

            for (int paramCount = 0; paramCount < paramTypes.length; ++paramCount) {
                argsAndReturn.append(Type.getType(Object.class));
            }
            argsAndReturn.append(")");
            argsAndReturn.append(Type.getType(Object.class));

            Class[] exceptionTypes = methods[count].getExceptionTypes();
            String[] exceptions = new String[exceptionTypes.length];
            for (int excCount = 0; excCount < exceptionTypes.length; ++excCount) {
                exceptions[excCount] = exceptionTypes[excCount].getName();
                exceptions[excCount] = exceptions[excCount].replace('.',
                                                                    '/');
            }

            CodeVisitor cv = cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_ABSTRACT,
                                            methods[count].getName(),
                                            argsAndReturn.toString(),
                                            exceptions,
                                            null);
            cw.visitEnd();
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    private class GenericProxyClassLoader extends ClassLoader {
        public Class defineClass(byte[] byteArray) {
            try {
                return defineClass(null,
                                   byteArray,
                                   0,
                                   byteArray.length);
            } catch (Throwable e) {
                return null;
            }
        }

    }
}
