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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;

import org.apache.tuscany.core.deployer.ChildDeploymentContext;

/**
 * Loads a composite component type
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentTypeLoader extends ComponentTypeLoaderExtension<CompositeImplementation> {
    public CompositeComponentTypeLoader() {
    }

    public CompositeComponentTypeLoader(LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    protected Class<CompositeImplementation> getImplementationClass() {
        return CompositeImplementation.class;
    }

    public void load(CompositeImplementation implementation, DeploymentContext context) throws LoaderException {
        URL scdlLocation = implementation.getScdlLocation();
        ClassLoader cl = new CompositeClassLoader(null, implementation.getClassLoader());
        URI componentId = URI.create(context.getComponentId().toString() + '/');
        DeploymentContext childContext =
            new ChildDeploymentContext(context, cl, scdlLocation, componentId, context.isAutowire());
        CompositeComponentType componentType = loadFromSidefile(scdlLocation, childContext);
        implementation.setComponentType(componentType);
    }

    protected CompositeComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext)
        throws LoaderException {
        return loaderRegistry.load(null, url, CompositeComponentType.class, deploymentContext);
    }
}
