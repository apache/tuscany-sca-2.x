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
package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.implementation.java.context.InstanceFactory;
import org.apache.tuscany.sca.implementation.java.context.InstanceFactoryProvider;
import org.apache.tuscany.sca.implementation.java.context.ReflectiveInstanceFactory;
import org.apache.tuscany.sca.implementation.java.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.EventInvoker;
import org.apache.tuscany.sca.implementation.java.injection.FieldInjector;
import org.apache.tuscany.sca.implementation.java.injection.Injector;
import org.apache.tuscany.sca.implementation.java.injection.InvalidAccessorException;
import org.apache.tuscany.sca.implementation.java.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.MethodEventInvoker;
import org.apache.tuscany.sca.implementation.java.injection.MethodInjector;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper;
import org.apache.tuscany.sca.spi.ObjectFactory;
import org.apache.tuscany.sca.spi.component.WorkContext;

/**
 * Encapsulates confuration for a Java-based atomic component
 * 
 * @version $Rev$ $Date$
 */
public class PojoConfiguration<T> implements InstanceFactoryProvider<T> {
    private JavaImplementation definition;
    private ProxyFactory proxyService;
    private WorkContext workContext;
    private URI groupId;
    private URI name;

    private final List<JavaElementImpl> injectionSites;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
    private final Map<JavaElementImpl, Object> factories = new HashMap<JavaElementImpl, Object>();

    public PojoConfiguration(JavaImplementation definition) {
        this.definition = definition;
        this.initInvoker = definition.getInitMethod() == null ? null : new MethodEventInvoker<T>(definition
            .getInitMethod());
        this.destroyInvoker = definition.getDestroyMethod() == null ? null : new MethodEventInvoker<T>(definition
            .getDestroyMethod());
        injectionSites = new ArrayList<JavaElementImpl>();
    }

    public URI getGroupId() {
        return groupId;
    }

    public void setGroupId(URI groupId) {
        this.groupId = groupId;
    }

    public EventInvoker<Object> getInitInvoker() {
        if (definition.getInitMethod() != null) {
            return new MethodEventInvoker<Object>(definition.getInitMethod());
        } else {
            return null;
        }
    }

    public EventInvoker<Object> getDestroyInvoker() {
        if (definition.getDestroyMethod() != null) {
            return new MethodEventInvoker<Object>(definition.getDestroyMethod());
        } else {
            return null;
        }
    }

    public ProxyFactory getProxyFactory() {
        return proxyService;
    }

    public void setProxyFactory(ProxyFactory proxyService) {
        this.proxyService = proxyService;
    }

    public WorkContext getWorkContext() {
        return workContext;
    }

    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    /**
     * @return the constructor
     */
    public JavaConstructorImpl<?> getConstructor() {
        return definition.getConstructor();
    }

    /**
     * @return the definition
     */
    public JavaImplementation getDefinition() {
        return definition;
    }

    @SuppressWarnings("unchecked")
    public InstanceFactory<T> createFactory() {
        ObjectFactory<?>[] initArgs = getConstructorArgs();
        Injector<T>[] injectors = getInjectors();
        return new ReflectiveInstanceFactory<T>((Constructor<T>)definition.getConstructor().getConstructor(),
                                                initArgs, injectors, initInvoker, destroyInvoker);
    }

    protected ObjectFactory<?>[] getConstructorArgs() {
        JavaConstructorImpl<?> constructor = definition.getConstructor();
        ObjectFactory<?>[] initArgs = new ObjectFactory<?>[constructor.getParameters().length];
        for (int i = 0; i < initArgs.length; i++) {
            ObjectFactory<?> factory = (ObjectFactory<?>)factories.get(constructor.getParameters()[i]);
            assert factory != null;
            initArgs[i] = factory;
        }
        return initArgs;
    }

    protected Injector<T>[] getInjectors() {
        // work around JDK1.5 issue with allocating generic arrays
        @SuppressWarnings("unchecked")
        Injector<T>[] injectors = (Injector<T>[])new Injector[injectionSites.size()];

        int i = 0;
        for (JavaElementImpl element : injectionSites) {
            Object obj = factories.get(element);
            if (obj != null) {
                if (obj instanceof ObjectFactory) {
                    ObjectFactory<?> factory = (ObjectFactory<?>)obj;
                    Member member = (Member)element.getAnchor();
                    if (element.getElementType() == ElementType.FIELD) {
                        injectors[i++] = new FieldInjector<T>((Field)member, factory);
                    } else if (element.getElementType() == ElementType.PARAMETER && member instanceof Method) {
                        injectors[i++] = new MethodInjector<T>((Method)member, factory);
                    } else if (member instanceof Constructor) {
                        // Ignore
                    } else {
                        throw new AssertionError(String.valueOf(element));
                    }
                } else {
                    injectors[i++] = createMultiplicityInjector(element, (List<ObjectFactory<?>>)obj);
                }
            }
        }
        return injectors;
    }

    protected Injector<T> createMultiplicityInjector(JavaElementImpl element, List<ObjectFactory<?>> factories) {
        Class<?> interfaceType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());

        if (element.getAnchor() instanceof Field) {
            Field field = (Field)element.getAnchor();
            if (field.getType().isArray()) {
                return new FieldInjector<T>(field, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new FieldInjector<T>(field, new ListMultiplicityObjectFactory(factories));
            }
        } else if (element.getAnchor() instanceof Method) {
            Method method = (Method)element.getAnchor();
            if (method.getParameterTypes()[0].isArray()) {
                return new MethodInjector<T>(method, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new MethodInjector<T>(method, new ListMultiplicityObjectFactory(factories));
            }
        } else {
            throw new InvalidAccessorException("Member must be a field or method: " + element.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public Class<T> getImplementationClass() {
        return (Class<T>)definition.getJavaClass();
    }

    public void setObjectFactory(JavaElementImpl element, ObjectFactory<?> objectFactory) {
        factories.put(element, objectFactory);
    }

    public void setObjectFactories(JavaElementImpl element, List<ObjectFactory<?>> objectFactory) {
        factories.put(element, objectFactory);
    }

    /**
     * @return the injectionSites
     */
    public List<JavaElementImpl> getInjectionSites() {
        return injectionSites;
    }

    /**
     * @return the factories
     */
    public Map<JavaElementImpl, Object> getFactories() {
        return factories;
    }

}
