/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Creates new instances of a Java class, calling a given set of injectors to configure the instance
 *
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.spi.injection.Injector
 */
public class PojoObjectFactory<T> implements ObjectFactory<T> {

    private static final ObjectFactory[] NO_INIT_PARAM = {};

    private final Constructor<T> ctr;
    private final ObjectFactory<?>[] initParamsArray;

    public PojoObjectFactory(Constructor<T> ctr) {
        this(ctr, null);
    }

    public PojoObjectFactory(Constructor<T> ctr, List<ObjectFactory> initParams) {
        this.ctr = ctr;
        if (initParams != null && initParams.size() > 0) {
            initParamsArray = initParams.toArray(new ObjectFactory[initParams.size()]);
        } else {
            initParamsArray = NO_INIT_PARAM;
        }
    }

    public T getInstance() throws ObjectCreationException {
        Object[] initargs = new Object[initParamsArray.length];
        // create the constructor arg array
        for (int i = 0; i < initParamsArray.length; i++) {
            ObjectFactory<?> objectFactory = initParamsArray[i];
            initargs[i] = objectFactory.getInstance();
        }
        try {
            ctr.setAccessible(true);
            return ctr.newInstance(initargs);
        } catch (InstantiationException e) {
            throw new AssertionError("Class is not instantiable [" + ctr.getDeclaringClass().getName() + "]");
        } catch (IllegalAccessException e) {
            throw new AssertionError("Constructor is not accessible [" + ctr + "]");
        } catch (InvocationTargetException e) {
            throw new ObjectCreationException("Exception thrown by constructor [" + ctr + "]", e);
        }
    }
}
