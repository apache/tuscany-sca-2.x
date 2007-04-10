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
package org.apache.tuscany.core.implementation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.ReflectiveInstanceFactory;
import org.apache.tuscany.core.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.InvalidAccessorException;
import org.apache.tuscany.core.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.processor.JavaIntrospectionHelper;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * Encapsulates confuration for a Java-based atomic component
 * 
 * @version $Rev$ $Date$
 */
public class PojoConfiguration<T> implements InstanceFactoryProvider<T> {
    private JavaImplementationDefinition definition;
    private ProxyService proxyService;
    private WorkContext workContext;
    private URI groupId;
    private URI name;

    private final List<JavaElement> injectionSites;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
    private final Map<JavaElement, Object> factories = new HashMap<JavaElement, Object>();

    public PojoConfiguration(JavaImplementationDefinition definition) {
        this.definition = definition;
        this.initInvoker = definition.getInitMethod() == null ? null : new MethodEventInvoker<T>(definition
            .getInitMethod());
        this.destroyInvoker = definition.getDestroyMethod() == null ? null : new MethodEventInvoker<T>(definition
            .getDestroyMethod());
        injectionSites = new ArrayList<JavaElement>();
    }
    
    public void setName(URI name) {
        this.name = name;
    }

    public URI getName() {
        return name;
    }

    public URI getGroupId() {
        return groupId;
    }

    public void setGroupId(URI groupId) {
        this.groupId = groupId;
    }

    public EventInvoker<Object> getInitInvoker() {
        return new MethodEventInvoker<Object>(definition.getInitMethod());
    }

    public EventInvoker<Object> getDestroyInvoker() {
        return new MethodEventInvoker<Object>(definition.getDestroyMethod());
    }

    public ProxyService getProxyService() {
        return proxyService;
    }

    public void setProxyService(ProxyService proxyService) {
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
    public ConstructorDefinition<?> getConstructor() {
        return definition.getConstructorDefinition();
    }

    /**
     * @return the definition
     */
    public JavaImplementationDefinition getDefinition() {
        return definition;
    }

    @SuppressWarnings("unchecked")
    public InstanceFactory<T> createFactory() {
        ObjectFactory<?>[] initArgs = getConstructorArgs();
        Injector<T>[] injectors = getInjectors();
        return new ReflectiveInstanceFactory<T>((Constructor<T>)definition.getConstructorDefinition().getConstructor(),
                                                initArgs, injectors, initInvoker, destroyInvoker);
    }

    protected ObjectFactory<?>[] getConstructorArgs() {
        ConstructorDefinition<?> constructor = definition.getConstructorDefinition();
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
        for (JavaElement element : injectionSites) {
            Object obj = factories.get(element);
            if (obj instanceof ObjectFactory) {
                ObjectFactory<?> factory = (ObjectFactory<?>)obj;
                if (element.getElementType() == ElementType.FIELD) {
                    injectors[i++] = new FieldInjector<T>((Field)element.getAnchor(), factory);
                } else if (element.getElementType() == ElementType.PARAMETER && element.getAnchor() instanceof Method) {
                    injectors[i++] = new MethodInjector<T>((Method)element.getAnchor(), factory);
                } else {
                    throw new AssertionError(String.valueOf(element));
                }
            } else {
                injectors[i++] = createMultiplicityInjector(element, (List<ObjectFactory<?>>)factories);
            }
        }
        return injectors;
    }

    protected Injector<T> createMultiplicityInjector(JavaElement element, List<ObjectFactory<?>> factories) {
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
            throw new InvalidAccessorException("Member must be a field or method", element.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public Class<T> getImplementationClass() {
        return (Class<T>)definition.getJavaClass();
    }

    public void setObjectFactory(JavaElement element, ObjectFactory<?> objectFactory) {
        factories.put(element, objectFactory);
    }

    public void setObjectFactories(JavaElement element, List<ObjectFactory<?>> objectFactory) {
        factories.put(element, objectFactory);
    }

    /**
     * @return the injectionSites
     */
    public List<JavaElement> getInjectionSites() {
        return injectionSites;
    }

    /**
     * @return the factories
     */
    public Map<JavaElement, Object> getFactories() {
        return factories;
    }

}
