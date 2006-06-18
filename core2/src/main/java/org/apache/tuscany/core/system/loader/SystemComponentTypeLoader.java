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
package org.apache.tuscany.core.system.loader;

import java.net.URL;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.PojoComponentType;

import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Loads a system component type
 *
 * @version $Rev$ $Date$
 */
public class SystemComponentTypeLoader extends ComponentTypeLoaderExtension<SystemImplementation> {
    protected Class<SystemImplementation> getImplementationClass() {
        return SystemImplementation.class;
    }

    public void load(SystemImplementation implementation, DeploymentContext deploymentContext) throws LoaderException {
        Class<?> implClass = implementation.getImplementationClass();
        URL sidefile = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        PojoComponentType componentType;
        if (sidefile == null) {
            componentType = loadByIntrospection(implementation);
        } else {
            componentType = loadFromSidefile(sidefile, deploymentContext);
        }
        implementation.setComponentType(componentType);
    }

    protected PojoComponentType loadByIntrospection(SystemImplementation implementation) {
        return new PojoComponentType();
    }


    protected PojoComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(url, PojoComponentType.class, deploymentContext);
    }
}
