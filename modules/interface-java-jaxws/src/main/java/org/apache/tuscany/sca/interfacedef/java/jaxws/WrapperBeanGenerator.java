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
import org.objectweb.asm.ClassWriter;

public class WrapperBeanGenerator extends BaseBeanGenerator {


    public List<Class<?>> generateWrapperBeans(Class<?> sei) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Method m : sei.getMethods()) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }
            classes.add(generateRequestWrapper(sei, m));
            classes.add(generateResponseWrapper(sei, m));
        }
        return classes;

    }

    private Class<?> generateRequestWrapper(Class<?> sei, Method m) {
        String wrapperNamespace = JavaInterfaceUtil.getNamespace(sei);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String wrapperName = capitalize(m.getName());
        String wrapperClassName = sei.getPackage().getName() + ".jaxws." + wrapperName;
        String wrapperClassDescriptor = wrapperClassName.replace('.', '/');
        String wrapperClassSignature = "L" + wrapperClassDescriptor + ";";

        Class<?>[] paramTypes = m.getParameterTypes();
        Type[] genericParamTypes = m.getGenericParameterTypes();
        BeanProperty[] properties = new BeanProperty[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            String propName = "arg" + i;
            properties[i] = new BeanProperty(propName, paramTypes[i], genericParamTypes[i]);
        }

        defineClass(cw, wrapperClassDescriptor, wrapperClassSignature, wrapperNamespace, m.getName(), properties);
        GeneratedClassLoader cl =
            new GeneratedClassLoader(sei.getClassLoader());
        Class<?> generated = cl.getGeneratedClass(wrapperClassName, cw.toByteArray());
        return generated;
    }

    private Class<?> generateResponseWrapper(Class<?> sei, Method m) {
        String wrapperNamespace = JavaInterfaceUtil.getNamespace(sei);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String wrapperName = capitalize(m.getName()) + "Response";
        String wrapperClassName = sei.getPackage().getName() + ".jaxws." + wrapperName;
        String wrapperClassDescriptor = wrapperClassName.replace('.', '/');
        String wrapperClassSignature = "L" + wrapperClassDescriptor + ";";
        
        Class<?> returnType = m.getReturnType();
        BeanProperty[] properties = null;
        if (returnType != void.class) {
            Type genericReturnType = m.getGenericReturnType();
            String propName = "return";
            properties = new BeanProperty[] {new BeanProperty(propName, returnType, genericReturnType)};
        }
        byte[] byteCode = defineClass(cw, wrapperClassDescriptor, wrapperClassSignature, wrapperNamespace, m.getName() + "Response", properties);
        GeneratedClassLoader cl =
            new GeneratedClassLoader(sei.getClassLoader());
        Class<?> generated = cl.getGeneratedClass(wrapperClassName, byteCode);
        return generated;
    }

}
