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
package org.apache.tuscany.container.java;

import java.net.URL;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentTypeLoader extends ComponentTypeLoaderExtension<JavaImplementation> {

    public JavaComponentTypeLoader() {
        super();
    }

    public void load(JavaImplementation implementation, DeploymentContext deploymentContext) throws LoaderException {
        Class<?> implClass = implementation.getImplementationClass();
        URL resource = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        PojoComponentType componentType;
        if (resource == null) {
            componentType = loadByIntrospection(implementation);
        } else {
            componentType = loadFromSidefile(resource, deploymentContext);
        }
        implementation.setComponentType(componentType);
    }

    protected PojoComponentType loadByIntrospection(JavaImplementation implementation) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class<JavaImplementation> getImplementationClass() {
        return JavaImplementation.class;
    }

    protected PojoComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(url, PojoComponentType.class, deploymentContext);
    }
}
