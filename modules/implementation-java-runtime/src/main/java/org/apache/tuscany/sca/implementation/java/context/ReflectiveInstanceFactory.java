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
package org.apache.tuscany.sca.implementation.java.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.factory.InstanceWrapper;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter;
import org.apache.tuscany.sca.implementation.java.injection.Injector;
import org.apache.tuscany.sca.implementation.java.invocation.EventInvoker;

/**
 * @version $Rev$ $Date$
 */
public class ReflectiveInstanceFactory<T> implements InstanceFactory<T> {
    private static final Logger logger = Logger.getLogger(ReflectiveInstanceFactory.class.getName(),
                                                          "org.apache.tuscany.sca.implementation.java.runtime.implementation-java-runtime-validation-messages");

    private final Constructor<T> ctr;
    private final ObjectFactory<?>[] ctrArgs;
    private final Injector<T>[] injectors;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
	private final Injector<T>[] callbackInjectors;

    public ReflectiveInstanceFactory(Constructor<T> ctr,
                                     ObjectFactory<?>[] ctrArgs,
                                     Injector<T>[] injectors,
                                     Injector<T>[] callbackInjectors,
                                     EventInvoker<T> initInvoker,
                                     EventInvoker<T> destroyInvoker) {
        this.ctr = ctr;
        this.ctrArgs = ctrArgs;
        this.injectors = injectors;
        this.callbackInjectors = callbackInjectors;
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
            String message = logger.getResourceBundle().getString("ClassNoInstantiable");
            message = message.replace("{0}", name);
            throw new AssertionError(message);
        } catch (IllegalAccessException e) {
            String name = ctr.getName();
            String message = logger.getResourceBundle().getString("ConstructorNotAccessible");
            message = message.replace("{0}", name);
            throw new AssertionError(message);
        } catch (InvocationTargetException e) {
            String name = ctr.getName();
            String message = logger.getResourceBundle().getString("ConstructorException");
            message = message.replace("{0}", name);
            throw new ObjectCreationException(message, e);
        }

        if (injectors != null) {
            for (Injector<T> injector : injectors) {
                //FIXME Injectors should never be null
                if (injector != null)
                    try {
                        injector.inject(instance);
                    } catch (Exception e) {
                        if (destroyInvoker != null) {
                            destroyInvoker.invokeEvent(instance);
                        }
                        String message = logger.getResourceBundle().getString("InjectorException");
                        message = message.replace("{0}", e.getMessage());
                        throw new ObjectCreationException(message, e);
                    }
            }
        }

        return new ReflectiveInstanceWrapper<T>(instance, initInvoker, destroyInvoker, callbackInjectors);
    }
}
