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
package org.apache.tuscany.core.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class ReflectiveInstanceFactoryProvider<T> implements InstanceFactoryProvider<T> {
    private final Constructor<T> constructor;
    private final List<String> constructorNames;
    private final Map<String, Member> injectionSites;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
    private final Map<String, ObjectFactory<?>> factories = new HashMap<String, ObjectFactory<?>>();

    public ReflectiveInstanceFactoryProvider(Constructor<T> constructor,
                                             List<String> constructorNames,
                                             Map<String, Member> injectionSites,
                                             EventInvoker<T> initInvoker,
                                             EventInvoker<T> destroyInvoker) {
        this.constructor = constructor;
        this.constructorNames = constructorNames;
        this.injectionSites = injectionSites;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
    }

    public void setObjectFactory(String name, ObjectFactory<?> objectFactory) {
        factories.put(name, objectFactory);
    }

    public InstanceFactory<T> createFactory() {
        ObjectFactory<?>[] initArgs = getConstructorArgs();
        Injector<T>[] injectors = getInjectors();
        return new ReflectiveInstanceFactory<T>(constructor, initArgs, injectors, initInvoker, destroyInvoker);
    }

    protected ObjectFactory<?>[] getConstructorArgs() {
        ObjectFactory<?>[] initArgs = new ObjectFactory<?>[constructorNames.size()];
        for (int i = 0; i < initArgs.length; i++) {
            String name = constructorNames.get(i);
            ObjectFactory<?> factory = factories.get(name);
            assert factory != null;
            initArgs[i] = factory;
        }
        return initArgs;
    }

    protected Injector<T>[] getInjectors() {
        // work around JDK1.5 issue with allocating generic arrays
        @SuppressWarnings("unchecked")
        Injector<T>[] injectors = (Injector<T>[]) new Injector[injectionSites.size()];

        int i = 0;
        for (Map.Entry<String, Member> entry : injectionSites.entrySet()) {
            String name = entry.getKey();
            Member site = entry.getValue();
            ObjectFactory<?> factory = factories.get(name);
            assert factory != null;
            if (site instanceof Field) {
                injectors[i++] = new FieldInjector<T>((Field) site, factory);
            } else if (site instanceof Method) {
                injectors[i++] = new MethodInjector<T>((Method) site, factory);
            } else {
                throw new AssertionError(String.valueOf(site));
            }
        }
        return injectors;
    }
}
