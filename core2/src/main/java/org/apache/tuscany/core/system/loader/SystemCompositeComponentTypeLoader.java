/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.CompositeComponentType;

/**
 * @version $Rev$ $Date$
 */
public class SystemCompositeComponentTypeLoader extends ComponentTypeLoaderExtension<SystemCompositeImplementation> {
    public SystemCompositeComponentTypeLoader() {
    }

    public SystemCompositeComponentTypeLoader(LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    protected Class<SystemCompositeImplementation> getImplementationClass() {
        return SystemCompositeImplementation.class;
    }

    public void load(SystemCompositeImplementation implementation, DeploymentContext deploymentContext) throws LoaderException {
        URL scdlLocation = implementation.getScdlLocation();
        CompositeComponentType componentType = loadFromSidefile(scdlLocation, deploymentContext);
        implementation.setComponentType(componentType);
    }


    protected CompositeComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(url, CompositeComponentType.class, deploymentContext);
    }
}
