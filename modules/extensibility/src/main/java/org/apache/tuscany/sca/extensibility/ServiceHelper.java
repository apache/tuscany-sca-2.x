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

package org.apache.tuscany.sca.extensibility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;

/**
 * A helper for handling service lifecycle and instantiations
 */
public class ServiceHelper {
    private ServiceHelper() {
    }

    /**
     * Start the service instance
     * @param instance
     */
    public static boolean start(Object instance) {
        if (instance instanceof LifeCycleListener) {
            ((LifeCycleListener)instance).start();
            return true;
        }
        return false;
    }

    /**
     * Stop the service instance
     * @param instance
     */
    public static boolean stop(Object instance) {
        if (instance instanceof LifeCycleListener) {
            ((LifeCycleListener)instance).stop();
            return true;
        }
        return false;
    }

    /**
     * Stop a collection of service instances
     * @param instances
     */
    public static void stop(Collection<? extends Object> instances) {
        if (instances == null) {
            return;
        }
        for (Object instance : instances) {
            if (instance instanceof LifeCycleListener) {
                ((LifeCycleListener)instance).stop();
            }
        }
    }

    /**
     * Create a service instance with one parameter
     * @param cls The service type
     * @param parameterType The parameter type
     * @param parameter The parameter value
     * @return The newly created service instance
     * @throws Exception
     */
    public static <T> T newInstance(Class<T> cls, Class<?> parameterType, Object parameter) throws Exception {
        Constructor<T> constructor = cls.getConstructor(parameterType);
        return constructor.newInstance(parameter);
    }

    /**
     * Create a service instance with an array of parameters
     * @param cls The service type
     * @param parameterTypes An array of parameter types
     * @param parameters An array of parameter values
     * @return The newly created service instance 
     * @throws Exception
     */
    public static <T> T newInstance(Class<T> cls, Class<?> parameterTypes[], Object... parameters) throws Exception {
        Constructor<T> constructor = cls.getConstructor(parameterTypes);
        return constructor.newInstance(parameters);
    }

    /**
     * Create a service instance with the default no-arg constructor
     * @param cls The service type
     * @return The newly created service instance
     * @throws Exception
     */
    public static <T> T newInstance(Class<T> cls) throws Exception {
        Constructor<T> constructor = cls.getConstructor();
        return constructor.newInstance();
    }

    private final static Class<?>[] ARG_TYPES = new Class<?>[] {ExtensionPointRegistry.class, Map.class};

    /**
     * Create a service instance from the service declaration
     * @param <T>
     * @param registry The extension point registry
     * @param sd The service declaration
     * @return The newly created service instance
     * @throws Exception
     */
    public static <T> T newInstance(ExtensionPointRegistry registry, ServiceDeclaration sd) throws Exception {
        Class<T> cls = (Class<T>)sd.loadClass();
        T instance = null;
        try {
            // Try constructor(ExtensionPointRegistry.class)
            instance = newInstance(cls, ExtensionPointRegistry.class, registry);
        } catch (NoSuchMethodException e) {
            try {
                // Try Try constructor(ExtensionPointRegistry.class, Map.class)
                instance = newInstance(cls, ARG_TYPES, registry, sd.getAttributes());
            } catch (NoSuchMethodException e1) {
                // Try constructor()
                instance = newInstance(cls);
            }
        }
        return instance;
    }

    public static <T> T newLazyInstance(ExtensionPointRegistry registry, ServiceDeclaration sd, Class<T> serviceType) {
        return serviceType.cast(Proxy.newProxyInstance(serviceType.getClassLoader(),
                                                       new Class<?>[] {serviceType, LifeCycleListener.class},
                                                       new InvocationHandlerImpl(registry, serviceType, sd)));
    }

    private static class InvocationHandlerImpl implements InvocationHandler {
        private ExtensionPointRegistry registry;
        private Class<?> type;
        private ServiceDeclaration sd;
        private Object instance;

        private final static Method STOP_METHOD = getMethod(LifeCycleListener.class, "stop");
        private final static Method START_METHOD = getMethod(LifeCycleListener.class, "start");
        private final static Method EQUALS_METHOD = getMethod(Object.class, "equals");
        private final static Method HASHCODE_METHOD = getMethod(Object.class, "hashCode");
        private final static Method TOSTRING_METHOD = getMethod(Object.class, "toString");

        private static Method getMethod(Class<?> type, String name) {
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                if (name.equals(method.getName())) {
                    return method;
                }
            }
            return null;
        }

        public InvocationHandlerImpl(ExtensionPointRegistry registry, Class<?> type, ServiceDeclaration sd) {
            super();
            this.registry = registry;
            this.sd = sd;
            this.type = type;
        }

        private Object getAttribute(Method method) throws Exception {
            if (method.getParameterTypes().length != 0) {
                return null;
            }
            String name = method.getName();
            if (name.equals("getModelType") && method.getReturnType() == Class.class) {
                return sd.loadClass(sd.getAttributes().get("model"));
            } else if (name.equals("getArtifactType")) {
                return ServiceDeclarationParser.getQName(sd.getAttributes().get("qname"));
            }
            return null;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            synchronized (this) {
                // Check if the method is to get the qname/model attribute
                Object value = getAttribute(method);
                if (value != null) {
                    return value;
                }
                if (instance == null && method.getDeclaringClass() == type) {
                    // Only initialize the instance when a method on the service type is invoked
                    instance = newInstance(registry, sd);
                    start(instance);
                }
                if (method.equals(EQUALS_METHOD)) {
                    return proxy == args[0];
                } else if (method.equals(HASHCODE_METHOD)) {
                    return System.identityHashCode(proxy);
                } else if (method.equals(TOSTRING_METHOD)) {
                    return "Proxy: " + sd.toString();
                }
                if (instance == null) {
                    return null;
                }
            }
            if (method.equals(STOP_METHOD)) {
                stop(instance);
                return null;
            } else if (method.equals(START_METHOD)) {
                // Skip the start()
                return null;
            } else {
                return method.invoke(instance, args);
            }
        }
    }
}
