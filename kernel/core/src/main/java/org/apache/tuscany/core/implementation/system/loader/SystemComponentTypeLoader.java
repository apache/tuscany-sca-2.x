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
package org.apache.tuscany.core.implementation.system.loader;

import java.net.URL;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.Component;
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
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Loads a system component type
 *
 * @version $Rev$ $Date$
 */
public class SystemComponentTypeLoader extends ComponentTypeLoaderExtension<SystemImplementation> {
    private Introspector introspector;

    public SystemComponentTypeLoader() {
    }

    public SystemComponentTypeLoader(Introspector introspector) {
        this.introspector = introspector;
    }

    public SystemComponentTypeLoader(LoaderRegistry loaderRegistry, Introspector introspector) {
        super(loaderRegistry);
        this.introspector = introspector;
    }

    //FIXME autowire to support multiple interfaces
    @Autowire
    public void setIntrospector(IntrospectionRegistry introspector) {
        this.introspector = introspector;
    }

    public void load(Component parent,
                     SystemImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {
        Class<?> implClass = implementation.getImplementationClass();
        URL sidefile = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        PojoComponentType componentType;
        if (sidefile == null) {
            componentType = loadByIntrospection(parent, implementation, deploymentContext);
        } else {
            componentType = loadFromSidefile(sidefile, deploymentContext);
        }
        // this means system components are always composite scoped
        componentType.setImplementationScope(Scope.COMPOSITE);
        implementation.setComponentType(componentType);
    }

    protected Class<SystemImplementation> getImplementationClass() {
        return SystemImplementation.class;
    }

    protected PojoComponentType loadByIntrospection(Component parent,
                                                    SystemImplementation implementation,
                                                    DeploymentContext deploymentContext) throws ProcessingException {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Class<?> implClass = implementation.getImplementationClass();
        introspector.introspect(parent, implClass, componentType, deploymentContext);
        return componentType;
    }


    protected PojoComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(null, null, url, PojoComponentType.class, deploymentContext);
    }
}
