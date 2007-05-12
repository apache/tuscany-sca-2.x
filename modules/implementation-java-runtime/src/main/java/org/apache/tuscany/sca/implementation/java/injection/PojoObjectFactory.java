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
package org.apache.tuscany.sca.implementation.java.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;

/**
 * Creates new instances of a Java class
 *
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.sca.implementation.java.injection.Injector
 */
public class PojoObjectFactory<T> implements ObjectFactory<T> {

    private final Constructor<T> ctr;
    private ObjectFactory[] initializerFactories;

    /**
     * Creates the object factory
     *
     * @param ctr the constructor to use when instantiating a new object
     */
    public PojoObjectFactory(Constructor<T> ctr) {
        assert ctr != null;
        this.ctr = ctr;
        initializerFactories = new ObjectFactory[ctr.getParameterTypes().length];
    }

    /**
     * Creates the object factory
     *
     * @param ctr       the constructor to use when instantiating a new object
     * @param factories an ordered list of <code>ObjectFactory</code>s to use for returning constructor parameters
     */
    public PojoObjectFactory(Constructor<T> ctr, List<ObjectFactory> factories) {
        assert ctr != null;
        int params = ctr.getParameterTypes().length;
        assert params == factories.size();
        this.ctr = ctr;
        initializerFactories = new ObjectFactory[params];
        int i = 0;
        for (ObjectFactory factory : factories) {
            initializerFactories[i] = factory;
            i++;
        }
    }

    /**
     * Returns the ordered array of <code>ObjectFactory</code>s use in creating constructor parameters
     */
    public ObjectFactory[] getInitializerFactories() {
        return initializerFactories;
    }

    /**
     * Sets an <code>ObjectFactory</code>s to use in creating constructor parameter
     *
     * @param pos     the constructor parameter position
     * @param factory the object factory
     */
    public void setInitializerFactory(int pos, ObjectFactory factory) {
        assert pos < initializerFactories.length;
        initializerFactories[pos] = factory;
    }

    /**
     * Creates a new instance of an object
     */
    public T getInstance() throws ObjectCreationException {
        int size = initializerFactories.length;
        Object[] initargs = new Object[size];
        // create the constructor arg array
        for (int i = 0; i < size; i++) {
            ObjectFactory<?> objectFactory = initializerFactories[i];
            if (objectFactory == null) {
                // this can happen if a reference is optional
                initargs[i] = null;
            } else {
                initargs[i] = objectFactory.getInstance();
            }
        }
        try {
            ctr.setAccessible(true);
            return ctr.newInstance(initargs);
        } catch (IllegalArgumentException e) {
            String name = ctr.getName();
            throw new ObjectCreationException("Exception thrown by constructor: " + name, e);
        } catch (InstantiationException e) {
            String name = ctr.getDeclaringClass().getName();
            throw new AssertionError("Class is not instantiable [" + name + "]");
        } catch (IllegalAccessException e) {
            String name = ctr.getName();
            throw new AssertionError("Constructor is not accessible [" + name + "]");
        } catch (InvocationTargetException e) {
            String name = ctr.getName();
            throw new ObjectCreationException("Exception thrown by constructor: " + name, e);
        }
    }
}
