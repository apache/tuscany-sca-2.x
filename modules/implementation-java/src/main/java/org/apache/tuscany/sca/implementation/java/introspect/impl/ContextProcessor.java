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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.Context;

/**
 * Processes {@link @Context} annotations on a component implementation and adds
 * a {@link JavaMappedProperty} to the component type which will be used to
 * inject the appropriate context
 * 
 * @version $Rev$ $Date$
 */
public class ContextProcessor extends BaseJavaClassVisitor {
    
    public ContextProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        if (method.getAnnotation(Context.class) == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalContextException("Context setter must have one parameter", method);
        }
        Class<?> paramType = method.getParameterTypes()[0];
        String name = JavaIntrospectionHelper.toPropertyName(method.getName());
        if (ComponentContext.class.equals(paramType) || RequestContext.class.equals(paramType)) {
            JavaElementImpl element = new JavaElementImpl(method, 0);
            element.setName(name);
            element.setClassifer(org.apache.tuscany.sca.implementation.java.introspect.impl.Resource.class);
            JavaResourceImpl resource = new JavaResourceImpl(element);
            type.getResources().put(resource.getName(), resource);
        } else {
            throw new UnknownContextTypeException(paramType.getName());
        }
    }

    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {
        if (field.getAnnotation(Context.class) == null) {
            return;
        }
        Class<?> paramType = field.getType();
        if (ComponentContext.class.equals(paramType) || RequestContext.class.equals(paramType)) {
            JavaElementImpl element = new JavaElementImpl(field);
            element.setClassifer(Resource.class);
            JavaResourceImpl resource = new JavaResourceImpl(element);
            type.getResources().put(resource.getName(), resource);
        } else {
            throw new UnknownContextTypeException(paramType.getName());
        }
    }
}
