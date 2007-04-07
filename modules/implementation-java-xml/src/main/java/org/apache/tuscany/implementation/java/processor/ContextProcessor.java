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
package org.apache.tuscany.implementation.java.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Resource;
import org.apache.tuscany.implementation.java.introspection.ImplementationProcessorExtension;
import org.apache.tuscany.implementation.java.introspection.ProcessingException;
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
public class ContextProcessor extends ImplementationProcessorExtension {

    public void visitMethod(Method method, JavaImplementationDefinition type) throws ProcessingException {
        if (method.getAnnotation(Context.class) == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalContextException("Context setter must have one parameter", method);
        }
        Class<?> paramType = method.getParameterTypes()[0];
        if (ComponentContext.class.equals(paramType)) {
            String name = JavaIntrospectionHelper.toPropertyName(method.getName());
            Resource<ComponentContext> resource = new Resource<ComponentContext>(name, ComponentContext.class, method);
            type.getResources().put(name, resource);
        } else if (RequestContext.class.equals(paramType)) {
            String name = JavaIntrospectionHelper.toPropertyName(method.getName());
            Resource<RequestContext> resource = new Resource<RequestContext>(name, RequestContext.class, method);
            // FIXME: Move the association with ObjectFactory to a later stage
            // resource.setObjectFactory(new RequestContextObjectFactory(workContext));
            type.getResources().put(name, resource);
        } else {
            throw new UnknownContextTypeException(paramType.getName());
        }
    }

    public void visitField(Field field, JavaImplementationDefinition type) throws ProcessingException {
        if (field.getAnnotation(Context.class) == null) {
            return;
        }
        Class<?> paramType = field.getType();
        if (ComponentContext.class.equals(paramType)) {
            String name = field.getName();
            Resource<ComponentContext> resource = new Resource<ComponentContext>(name, ComponentContext.class, field);
            type.getResources().put(name, resource);
        } else if (RequestContext.class.equals(paramType)) {
            String name = field.getName();
            name = JavaIntrospectionHelper.toPropertyName(name);
            Resource<RequestContext> resource = new Resource<RequestContext>(name, RequestContext.class, field);
            
            // FIXME: Move the association with ObjectFactory to a later stage
            // resource.setObjectFactory(new RequestContextObjectFactory(workContext));
            type.getResources().put(name, resource);
        } else {
            throw new UnknownContextTypeException(paramType.getName());
        }
    }
}
