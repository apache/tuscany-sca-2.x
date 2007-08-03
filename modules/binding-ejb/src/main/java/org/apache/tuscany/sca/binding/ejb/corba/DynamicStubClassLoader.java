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
package org.apache.tuscany.sca.binding.ejb.corba;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.rmi.CORBA.Stub;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

/**
 * @version $Revision$ $Date$
 */
public class DynamicStubClassLoader extends ClassLoader {
    private final static String PACKAGE_PREFIX = "org.omg.stub.";

    public synchronized Class loadClass(final String name) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // check if the stub already exists first
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
        }

        // if this is not a class from the org.omb.stub name space don't attempt to generate
        if (!name.startsWith(PACKAGE_PREFIX)) {
            throw new ClassNotFoundException("Could not load class: " + name);
        }

        // load the interfaces class we are attempting to create a stub for
        Class iface = loadStubInterfaceClass(name, classLoader);

        // create the stub builder
        try {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Stub.class);
            enhancer.setInterfaces(new Class[] {iface});
            enhancer.setCallbackFilter(FILTER);
            enhancer.setCallbackTypes(new Class[] {NoOp.class, MethodInterceptor.class, FixedValue.class});
            enhancer.setUseFactory(false);
            enhancer.setClassLoader(classLoader);
            enhancer.setNamingPolicy(new NamingPolicy() {
                public String getClassName(String s, String s1, Object o, Predicate predicate) {
                    return name;
                }
            });

            // generate the class
            Class result = enhancer.createClass();
            assert result != null;

            StubMethodInterceptor interceptor = new StubMethodInterceptor(iface);
            Ids ids = new Ids(iface);
            Enhancer.registerStaticCallbacks(result, new Callback[] {NoOp.INSTANCE, interceptor, ids});

            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        }
    }

    private Class loadStubInterfaceClass(String name, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            int begin = name.lastIndexOf('.') + 1;
            String iPackage = name.substring(13, begin);
            String iName = iPackage + name.substring(begin + 1, name.length() - 5);

            return classLoader.loadClass(iName);
        } catch (ClassNotFoundException e) {
            // don't log exceptions from CosNaming because it attempts to load every
            // class bound into the name server
            boolean shouldLog = true;
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement stackTraceElement = stackTrace[i];
                if (stackTraceElement.getClassName().equals("org.omg.CosNaming.NamingContextExtPOA") && stackTraceElement
                    .getMethodName().equals("_invoke")) {
                    shouldLog = false;
                    break;
                }
            }

            throw new ClassNotFoundException("Unable to generate stub", e);
        }
    }

    private static final CallbackFilter FILTER = new CallbackFilter() {
        public int accept(Method method) {
            // we don't intercept non-public methods like finalize
            if (!Modifier.isPublic(method.getModifiers())) {
                return 0;
            }

            if (method.getReturnType().equals(String[].class) && method.getParameterTypes().length == 0
                && method.getName().equals("_ids")) {
                return 2;
            }

            if (Modifier.isAbstract(method.getModifiers())) {
                return 1;
            }

            return 0;
        }
    };

    private static final class Ids implements FixedValue {
        private final String[] typeIds;

        public Ids(Class type) {
            typeIds = Java2IDLUtil.createCorbaIds(type);
        }

        public Object loadObject() throws Exception {
            return typeIds;
        }
    }

}
