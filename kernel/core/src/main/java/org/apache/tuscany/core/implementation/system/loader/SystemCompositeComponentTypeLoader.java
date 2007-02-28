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

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.CompositeComponentType;

import org.apache.tuscany.core.deployer.ChildDeploymentContext;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;

/**
 * Loads a system composite component type
 *
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

    public void load(
        SystemCompositeImplementation implementation,
        DeploymentContext deploymentContext)
        throws LoaderException {
        URL scdlLocation = implementation.getScdlLocation();
        if (scdlLocation == null) {
            throw new LoaderException("SCDL location not found");
        }
        ClassLoader cl = implementation.getClassLoader();
        URI componentId = deploymentContext.getComponentId();
        DeploymentContext childContext = new ChildDeploymentContext(deploymentContext,
            cl,
            scdlLocation,
            componentId,
            deploymentContext.isAutowire());
        CompositeComponentType componentType = loadFromSidefile(scdlLocation, childContext);
        implementation.setComponentType(componentType);
    }


    protected CompositeComponentType loadFromSidefile(URL url, DeploymentContext context) throws LoaderException {
        return loaderRegistry.load(null, url, CompositeComponentType.class, context);
    }
}
