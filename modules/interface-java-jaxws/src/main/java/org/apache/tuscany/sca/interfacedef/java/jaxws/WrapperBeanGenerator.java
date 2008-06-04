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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;

public class WrapperBeanGenerator extends BaseBeanGenerator {

    public List<Class<?>> generateWrapperBeans(Class<?> sei) {
        GeneratedClassLoader cl = new GeneratedClassLoader(sei.getClassLoader());
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Method m : sei.getMethods()) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }
            classes.add(generateRequestWrapper(sei, m, cl));
            classes.add(generateResponseWrapper(sei, m, cl));
        }
        return classes;

    }

    public Class<?> generateRequestWrapper(Class<?> sei, Method m, GeneratedClassLoader cl) {
        String wrapperNamespace = JavaInterfaceUtil.getNamespace(sei);
        String wrapperName = m.getName();
        String wrapperBeanName = capitalize(wrapperName);
        String wrapperClassName = sei.getPackage().getName() + ".jaxws." + wrapperBeanName;

        return generateRequestWrapper(m, wrapperClassName, wrapperNamespace, wrapperName, cl);
    }

    public Class<?> generateRequestWrapper(Method m,
                                           String wrapperClassName,
                                           String wrapperNamespace,
                                           String wrapperName,
                                           GeneratedClassLoader cl) {
        synchronized (m.getDeclaringClass()) {
            MethodKey key = new MethodKey(m, true);
            Class<?> wrapperClass = generatedClasses.get(key);
            if (wrapperClass == null) {
                String wrapperClassDescriptor = wrapperClassName.replace('.', '/');
                String wrapperClassSignature = "L" + wrapperClassDescriptor + ";";

                Class<?>[] paramTypes = m.getParameterTypes();
                Type[] genericParamTypes = m.getGenericParameterTypes();
                BeanProperty[] properties = new BeanProperty[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    String propName = "arg" + i;
                    properties[i] = new BeanProperty(propName, paramTypes[i], genericParamTypes[i]);
                }

                wrapperClass =
                    generate(wrapperClassDescriptor,
                             wrapperClassSignature,
                             wrapperNamespace,
                             wrapperName,
                             properties,
                             cl);
                generatedClasses.put(key, wrapperClass);
            }
            return wrapperClass;

        }
    }

    public Class<?> generateResponseWrapper(Class<?> sei, Method m, GeneratedClassLoader cl) {
        String wrapperNamespace = JavaInterfaceUtil.getNamespace(sei);

        String wrapperName = m.getName() + "Response";
        String wrapperBeanName = capitalize(wrapperName);
        String wrapperClassName = sei.getPackage().getName() + ".jaxws." + wrapperBeanName;
        return generateResponseWrapper(m, wrapperClassName, wrapperNamespace, wrapperName, cl);

    }

    public Class<?> generateResponseWrapper(Method m,
                                            String wrapperClassName,
                                            String wrapperNamespace,
                                            String wrapperName,
                                            GeneratedClassLoader cl) {
        synchronized (m.getDeclaringClass()) {
            MethodKey key = new MethodKey(m, false);
            Class<?> wrapperClass = generatedClasses.get(key);
            if (wrapperClass == null) {
                String wrapperClassDescriptor = wrapperClassName.replace('.', '/');
                String wrapperClassSignature = "L" + wrapperClassDescriptor + ";";

                Class<?> returnType = m.getReturnType();
                BeanProperty[] properties = null;
                if (returnType != void.class) {
                    Type genericReturnType = m.getGenericReturnType();
                    String propName = "return";
                    properties = new BeanProperty[] {new BeanProperty(propName, returnType, genericReturnType)};
                }
                wrapperClass =
                    generate(wrapperClassDescriptor,
                             wrapperClassSignature,
                             wrapperNamespace,
                             wrapperName,
                             properties,
                             cl);
                generatedClasses.put(key, wrapperClass);
            }
            return wrapperClass;

        }
    }

    private static class MethodKey {
        private Method m;
        private boolean request;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((m == null) ? 0 : m.hashCode());
            result = prime * result + (request ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final MethodKey other = (MethodKey)obj;
            if (m == null) {
                if (other.m != null)
                    return false;
            } else if (!m.equals(other.m))
                return false;
            if (request != other.request)
                return false;
            return true;
        }

        public MethodKey(Method m, boolean request) {
            super();
            this.m = m;
            this.request = request;
        }
    }

}
