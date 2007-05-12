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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;

/**
 * Injects a value created by an {@link org.apache.tuscany.sca.spi.ObjectFactory} using a given method
 *
 * @version $Rev$ $Date$
 */
public class MethodInjector<T> implements Injector<T> {
    private final Method method;
    private final ObjectFactory<?> objectFactory;

    public MethodInjector(Method method, ObjectFactory<?> objectFactory) {
        assert method != null;
        assert objectFactory != null;
        this.method = method;
        this.method.setAccessible(true);
        this.objectFactory = objectFactory;
    }

    public void inject(T instance) throws ObjectCreationException {
        try {
            method.invoke(instance, objectFactory.getInstance());
        } catch (IllegalAccessException e) {
            throw new AssertionError("Method is not accessible [" + method + "]");
        } catch (IllegalArgumentException e) {
            throw new ObjectCreationException("Exception thrown by setter: " + method.getName(), e);
        } catch (InvocationTargetException e) {
            throw new ObjectCreationException("Exception thrown by setter: " + method.getName(), e);
        }
    }
}
