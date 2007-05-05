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
package org.apache.tuscany.implementation.java.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.implementation.java.injection.EventInvoker;
import org.apache.tuscany.implementation.java.injection.Injector;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * @version $Rev$ $Date$
 */
public class ReflectiveInstanceFactory<T> implements InstanceFactory<T> {
    private final Constructor<T> ctr;
    private final ObjectFactory<?>[] ctrArgs;
    private final Injector<T>[] injectors;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;

    public ReflectiveInstanceFactory(Constructor<T> ctr,
                                     ObjectFactory<?>[] ctrArgs,
                                     Injector<T>[] injectors,
                                     EventInvoker<T> initInvoker,
                                     EventInvoker<T> destroyInvoker) {
        this.ctr = ctr;
        this.ctrArgs = ctrArgs;
        this.injectors = injectors;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
    }

    public InstanceWrapper<T> newInstance() {
        T instance;
        try {
            if (ctrArgs != null) {
                Object[] args = new Object[ctrArgs.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = ctrArgs[i].getInstance();
                }
                instance = ctr.newInstance(args);
            } else {
                instance = ctr.newInstance();
            }
        } catch (InstantiationException e) {
            String name = ctr.getDeclaringClass().getName();
            throw new AssertionError("Class is not instantiable [" + name + "]");
        } catch (IllegalAccessException e) {
            String name = ctr.getName();
            throw new AssertionError("Constructor is not accessible [" + name + "]");
        } catch (
            InvocationTargetException e) {
            String name = ctr.getName();
            throw new ObjectCreationException("Exception thrown by constructor", name, e);
        }

        if (injectors != null) {
            for (Injector<T> injector : injectors) {
                //FIXME Injectors should never be null
                if (injector != null)
                    injector.inject(instance);
            }
        }

        return new ReflectiveInstanceWrapper<T>(instance, initInvoker, destroyInvoker);
    }
}
