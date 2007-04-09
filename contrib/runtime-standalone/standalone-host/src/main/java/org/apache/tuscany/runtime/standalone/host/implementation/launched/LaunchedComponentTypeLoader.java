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
package org.apache.tuscany.runtime.standalone.host.implementation.launched;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @version $Revision$ $Date$
 */
public class LaunchedComponentTypeLoader extends ComponentTypeLoaderExtension<Launched> {
    private static final URI SERVICE_NAME = URI.create("#main");
    private Introspector introspector;

    public LaunchedComponentTypeLoader(@Reference LoaderRegistry loaderRegistry,
                                       @Reference IntrospectionRegistry introspector) {
        super(loaderRegistry);
        this.introspector = introspector;
    }

    @Override
    protected Class<Launched> getImplementationClass() {
        return Launched.class;
    }

    public void load(
        Launched implementation,
        DeploymentContext deploymentContext) throws LoaderException {
        String className = implementation.getClassName();
        Class<?> implClass;
        try {
            implClass = deploymentContext.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new MissingResourceException(className, e);
        }
        PojoComponentType componentType = loadByIntrospection(implementation, deploymentContext, implClass);
        implementation.setComponentType(componentType);
    }

    protected PojoComponentType loadByIntrospection(
        Launched implementation,
        DeploymentContext deploymentContext,
        Class<?> implClass) throws ProcessingException {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>(implClass);
        introspector.introspect(implClass, componentType, deploymentContext);

        ServiceContract launchedContract = generateContract(implClass);
        JavaMappedService testService = new JavaMappedService(SERVICE_NAME, launchedContract, false);
        componentType.add(testService);
        return componentType;
    }

    private static final DataType<List<DataType<Type>>> INPUT_TYPE;
    private static final DataType<Type> OUTPUT_TYPE;
    private static final List<DataType<Type>> FAULT_TYPE;

    static {
        List<DataType<Type>> paramDataTypes = new ArrayList<DataType<Type>>();
        //noinspection unchecked
        paramDataTypes.add(new DataType(null, String[].class, String[].class));
        INPUT_TYPE = new DataType<List<DataType<Type>>>("idl:input", Object[].class, paramDataTypes);
        OUTPUT_TYPE = new DataType<Type>(null, Object.class, Object.class);
        FAULT_TYPE = Collections.emptyList();
    }

    protected ServiceContract generateContract(Class<?> implClass) {
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        Operation<Type> operation = new Operation<Type>("main", INPUT_TYPE, OUTPUT_TYPE, FAULT_TYPE);
        operations.put("main", operation);
        return new LaunchedServiceContract(operations);
    }

}
