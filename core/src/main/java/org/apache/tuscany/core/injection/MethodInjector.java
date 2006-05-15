/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ObjectFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Injects a value created by an {@link ObjectFactory} using a given method
 * @version $Rev$ $Date$
 */
public class MethodInjector<T> implements Injector<T> {
    private final Method method;
    private final ObjectFactory<?> objectFactory;

    public MethodInjector(Method method, ObjectFactory<?> objectFactory) {
        this.method = method;
        this.objectFactory = objectFactory;
    }

    public void inject(T instance) throws ObjectCreationException {
        try {
            method.invoke(instance, objectFactory.getInstance());
        } catch (IllegalAccessException e) {
            throw new AssertionError("Method is not accessible [" + method + "]");
        } catch (InvocationTargetException e) {
            ObjectCreationException oce= new ObjectCreationException("Exception thrown by setter", e);
            oce.setIdentifier(method.getName());
            throw oce;
        }
    }
}
