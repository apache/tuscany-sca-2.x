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
package org.apache.tuscany.sca.plugin.itest.implementation.junit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.implementation.java.IntrospectionRegistry;
import org.apache.tuscany.spi.implementation.java.Introspector;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class JUnitComponentTypeLoader extends ComponentTypeLoaderExtension<ImplementationJUnit> {
    private static final URI TEST_SERVICE_NAME = URI.create("#testService");
    private Introspector introspector;

    @Constructor
    public JUnitComponentTypeLoader(@Reference LoaderRegistry loaderRegistry,
                                    @Reference IntrospectionRegistry introspector) {
        super(loaderRegistry);
        this.introspector = introspector;
    }

    @Override
    protected Class<ImplementationJUnit> getImplementationClass() {
        return ImplementationJUnit.class;
    }

    public void load(ImplementationJUnit implementation, DeploymentContext context) throws LoaderException {
        String className = implementation.getClassName();
        Class<?> implClass;
        try {
            implClass = context.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new MissingResourceException(className, e);
        }
        PojoComponentType componentType = loadByIntrospection(implementation, context, implClass);
        implementation.setComponentType(componentType);
    }

    protected PojoComponentType loadByIntrospection(ImplementationJUnit implementation,
                                                    DeploymentContext deploymentContext,
                                                    Class<?> implClass) throws ProcessingException {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>(implClass);
        introspector.introspect(implClass, componentType, deploymentContext);

        if (componentType.getInitMethod() == null) {
            componentType.setInitMethod(getCallback(implClass, "setUp"));
        }
        if (componentType.getDestroyMethod() == null) {
            componentType.setDestroyMethod(getCallback(implClass, "tearDown"));
        }
        ServiceContract testContract = generateTestContract(implClass);
        JavaMappedService testService = new JavaMappedService(TEST_SERVICE_NAME, testContract, false);
        componentType.add(testService);
        return componentType;
    }

    protected Method getCallback(Class<?> implClass, String name) {
        while (Object.class != implClass) {
            try {
                Method callback = implClass.getDeclaredMethod(name);
                callback.setAccessible(true);
                return callback;
            } catch (NoSuchMethodException e) {
                implClass = implClass.getSuperclass();
                continue;
            }
        }
        return null;
    }

    private static final DataType<List<DataType<Type>>> INPUT_TYPE;
    private static final DataType<Type> OUTPUT_TYPE;
    private static final List<DataType<Type>> FAULT_TYPE;

    static {
        List<DataType<Type>> paramDataTypes = Collections.emptyList();
        INPUT_TYPE = new DataType<List<DataType<Type>>>("idl:input", Object[].class, paramDataTypes);
        OUTPUT_TYPE = new DataType<Type>(null, void.class, void.class);
        FAULT_TYPE = Collections.emptyList();
    }

    protected ServiceContract generateTestContract(Class<?> implClass) {
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        for (Method method : implClass.getMethods()) {
            // see if this is a test method
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getReturnType() != void.class) {
                continue;
            }
            if (method.getParameterTypes().length != 0) {
                continue;
            }
            String name = method.getName();
            if (name.length() < 5 || !name.startsWith("test")) {
                continue;
            }
            Operation<Type> operation = new Operation<Type>(name, INPUT_TYPE, OUTPUT_TYPE, FAULT_TYPE);
            operations.put(name, operation);
        }
        return new JUnitServiceContract(operations);
    }
}
