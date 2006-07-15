/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.Context;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Processes {@link @Context} annotations on a component implementation and adds a {@link JavaMappedProperty} to the
 * component type which will be used to inject the appropriate context
 *
 * @version $Rev$ $Date$
 */
public class ContextProcessor extends ImplementationProcessorSupport {

    public void visitMethod(CompositeComponent<?> parent, Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        if (method.getAnnotation(Context.class) == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            IllegalContextException e = new IllegalContextException("Context setter must have one parameter");
            e.setIdentifier(method.toString());
            throw e;
        }
        Class<?> paramType = method.getParameterTypes()[0];
        if (CompositeContext.class.equals(paramType)) {
            String name = method.getName();
            if (name.startsWith("set")) {
                name = JavaIntrospectionHelper.toPropertyName(name);
            }
            JavaMappedProperty property = new JavaMappedProperty();
            property.setName(name);
            property.setMember(method);
            throw new UnsupportedOperationException();
            // TODO pass in composite context
            //SingletonObjectFactory factory = new SingletonObjectFactory(compositeContext);
            //property.setDefaultValueFactory(factory);
            //type.getProperties().put(name,property);
        } else if (RequestContext.class.equals(paramType)) {
            String name = method.getName();
            if (name.startsWith("set")) {
                name = JavaIntrospectionHelper.toPropertyName(name);
            }
            JavaMappedProperty property = new JavaMappedProperty();
            property.setName(name);
            property.setMember(method);
            throw new UnsupportedOperationException();
            // TODO pass in request context
            //property.setDefaultValueFactory(factory);
            //type.getProperties().put(name,property);
        } else {
            throw new UnknownContextTypeException(paramType.getName());
        }

    }

    public void visitField(CompositeComponent<?> parent, Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        super.visitField(parent, field, type, context);
    }
}
