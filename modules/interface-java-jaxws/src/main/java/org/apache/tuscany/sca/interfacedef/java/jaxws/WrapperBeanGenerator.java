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

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;

import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.oasisopen.sca.ResponseDispatch;

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
        String wrapperClassName = CodeGenerationHelper.getPackagePrefix(sei) + wrapperBeanName;

        return generateRequestWrapper(m, wrapperClassName, wrapperNamespace, wrapperName, cl);
    }

    public Class<?> generateRequestWrapper(Method m,
                                           String wrapperClassName,
                                           String wrapperNamespace,
                                           String wrapperName,
                                           GeneratedClassLoader cl) {
        synchronized (m.getDeclaringClass()) {
            MethodKey key = new MethodKey(m, true);
            WeakReference<Class<?>> wr = generatedClasses.get(key);
            Class<?> wrapperClass = null;
            if (wr != null){
                wrapperClass = wr.get();
            }
            if (wrapperClass == null) {
                String wrapperClassDescriptor = wrapperClassName.replace('.', '/');
                String wrapperClassSignature = "L" + wrapperClassDescriptor + ";";

                Class<?>[] paramTypes = m.getParameterTypes();
                Type[] genericParamTypes = m.getGenericParameterTypes();
                Annotation[][] paramAnnotations = m.getParameterAnnotations();
                List<BeanProperty> properties = new ArrayList<BeanProperty>();

                boolean asyncMethod = m.getName().endsWith("Async") && paramTypes.length > 0 && ResponseDispatch.class.equals(paramTypes[paramTypes.length-1]);
                int length = paramTypes.length;
                if (asyncMethod) {
                    length -= 1;
                }
                
                for (int i = 0; i < length; i++) {
                    
                    String propNS = "";
                    String propName = "arg" + i;

                    WebParam webParam = findAnnotation(paramAnnotations[i], WebParam.class);
                    if (webParam != null && webParam.header()) {
                        continue;
                    }
                    WebParam.Mode mode = WebParam.Mode.IN;
                    if (webParam != null) {
                        mode = webParam.mode();
                        if (webParam.name().length() > 0) {
                            propName = webParam.name();
                        }
                        propNS = webParam.targetNamespace();
                    }
                    
                    if (mode.equals(WebParam.Mode.IN) || mode.equals(WebParam.Mode.INOUT)) {
                        java.lang.reflect.Type genericParamType = getHolderValueType(genericParamTypes[i]);
                        Class<?> paramType = CodeGenerationHelper.getErasure(genericParamType);
                        BeanProperty prop = new BeanProperty(propNS, propName, paramType, genericParamType, true);
                        prop.getJaxbAnnotaions().addAll(findJAXBAnnotations(paramAnnotations[i]));
                        properties.add(prop);
                    }
                }

                wrapperClass =
                    generate(wrapperClassDescriptor, wrapperClassSignature, wrapperNamespace, wrapperName, properties
                        .toArray(new BeanProperty[properties.size()]), cl);
                generatedClasses.put(key, new WeakReference<Class<?>>(wrapperClass));
            }
            return wrapperClass;

        }
    }

    public Class<?> generateResponseWrapper(Class<?> sei, Method m, GeneratedClassLoader cl) {
        String wrapperNamespace = JavaInterfaceUtil.getNamespace(sei);

        String wrapperName = m.getName() + "Response";
        String wrapperBeanName = capitalize(wrapperName);
        String wrapperClassName = CodeGenerationHelper.getPackagePrefix(sei) + wrapperBeanName;
        return generateResponseWrapper(m, wrapperClassName, wrapperNamespace, wrapperName, cl);

    }

    public Class<?> generateResponseWrapper(Method m,
                                            String wrapperClassName,
                                            String wrapperNamespace,
                                            String wrapperName,
                                            GeneratedClassLoader cl) {
        synchronized (m.getDeclaringClass()) {
            MethodKey key = new MethodKey(m, false);
            WeakReference<Class<?>> wr = generatedClasses.get(key);
            Class<?> wrapperClass = null;
            if (wr != null){
                wrapperClass = wr.get();
            }
            if (wrapperClass == null) {
                String wrapperClassDescriptor = wrapperClassName.replace('.', '/');
                String wrapperClassSignature = "L" + wrapperClassDescriptor + ";";

                List<BeanProperty> properties = new ArrayList<BeanProperty>();
                // Collect all OUT, INOUT parameters as fields
                Annotation[][] paramAnns = m.getParameterAnnotations();
                Class<?>[] paramTypes = m.getParameterTypes();
                java.lang.reflect.Type[] genericParamTypes = m.getGenericParameterTypes();

                boolean asyncMethod = m.getName().endsWith("Async") && paramTypes.length > 0 && ResponseDispatch.class.equals(paramTypes[paramTypes.length-1]);
                int length = paramTypes.length;
                if (asyncMethod) {
                    length -= 1;
                }
                
                for (int i = 0; i < length; i++) {
                    WebParam webParam = findAnnotation(paramAnns[i], WebParam.class);
                    if (webParam != null) {
                        if (webParam.header() || webParam.mode() == WebParam.Mode.IN) {
                            continue;
                        }
                    }
                    if (!isHolder(genericParamTypes[i])) {
                        continue;
                    }

                    List<Annotation> jaxb = findJAXBAnnotations(paramAnns[i]);

                    java.lang.reflect.Type genericParamType = getHolderValueType(genericParamTypes[i]);
                    Class<?> paramType = CodeGenerationHelper.getErasure(genericParamType);

                    String paramNamespace = "";
                    String paramName = "arg" + i;

                    if (webParam != null) {
                        if (webParam.name().length() > 0)
                            paramName = webParam.name();
                        if (webParam.targetNamespace().length() > 0)
                            paramNamespace = webParam.targetNamespace();
                    }

                    BeanProperty prop = new BeanProperty(paramNamespace, paramName, paramType, genericParamType, true);
                    prop.getJaxbAnnotaions().addAll(jaxb);
                    properties.add(prop);
                }

                WebResult webResult = m.getAnnotation(WebResult.class);
                Class<?> returnType = m.getReturnType();
                if (asyncMethod) {
                    returnType = (Class<?>)((ParameterizedType)genericParamTypes[genericParamTypes.length-1]).getActualTypeArguments()[0];
                }
                if (!((webResult != null && webResult.header()) || returnType == Void.TYPE)) {
                    String propName = "return";
                    String propNS = "";

                    if (webResult != null) {
                        if (webResult.name().length() > 0) {
                            propName = webResult.name();
                        }
                        if (webResult.targetNamespace().length() > 1) {
                            propNS = webResult.targetNamespace();
                        }
                    }

                    List<Annotation> jaxb = findJAXBAnnotations(m.getAnnotations());

                    Type genericReturnType = asyncMethod? returnType : m.getGenericReturnType();
                    BeanProperty prop = new BeanProperty(propNS, propName, returnType, genericReturnType, true);
                    prop.getJaxbAnnotaions().addAll(jaxb);
                    // TUSCANY-3283 - As per JAXWS spec () the "return" value should come first in the 
                    //                list when there are holders. 
                    properties.add(0, prop);
                }
                wrapperClass =
                    generate(wrapperClassDescriptor, wrapperClassSignature, wrapperNamespace, wrapperName, properties
                        .toArray(new BeanProperty[properties.size()]), cl);
                generatedClasses.put(key, new WeakReference<Class<?>>(wrapperClass));
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
