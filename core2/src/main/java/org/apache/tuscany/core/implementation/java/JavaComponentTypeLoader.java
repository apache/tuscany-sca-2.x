/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.implementation.java;

import java.net.URL;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;

import org.apache.tuscany.core.implementation.IntrospectionRegistry;
import org.apache.tuscany.core.implementation.Introspector;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentTypeLoader extends ComponentTypeLoaderExtension<JavaImplementation> {
    private Introspector introspector;

    public JavaComponentTypeLoader() {
        super();
    }

    //FIXME autowire to support multiple interfaces
    @Autowire
    public void setIntrospector(IntrospectionRegistry introspector) {
        this.introspector = introspector;
    }

    public void load(CompositeComponent<?> parent,
                     JavaImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {
        Class<?> implClass = implementation.getImplementationClass();
        URL resource = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        PojoComponentType componentType;
        if (resource == null) {
            componentType = loadByIntrospection(parent, implementation, deploymentContext);
        } else {
            componentType = loadFromSidefile(resource, deploymentContext);
        }
        implementation.setComponentType(componentType);
    }

    protected PojoComponentType loadByIntrospection(CompositeComponent<?> parent,
                                                    JavaImplementation implementation,
                                                    DeploymentContext deploymentContext) throws ProcessingException {
        PojoComponentType componentType = new PojoComponentType();
        Class<?> implClass = implementation.getImplementationClass();
        introspector.introspect(parent, implClass, componentType, deploymentContext);
        return componentType;
    }

    @Override
    protected Class<JavaImplementation> getImplementationClass() {
        return JavaImplementation.class;
    }

    protected PojoComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(null, url, PojoComponentType.class, deploymentContext);
    }
}
