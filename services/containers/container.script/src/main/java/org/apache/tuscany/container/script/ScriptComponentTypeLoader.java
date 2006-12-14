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
package org.apache.tuscany.container.script;

import java.net.URL;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentType;

/**
 * ComponentType loader for script components
 */
public class ScriptComponentTypeLoader extends ComponentTypeLoaderExtension<ScriptImplementation> {

    public ScriptComponentTypeLoader() {
    }

    @Override
    protected Class<ScriptImplementation> getImplementationClass() {
        return ScriptImplementation.class;
    }

    public void load(CompositeComponent parent,
                     ScriptImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {
        String sideFile = getSideFileName(implementation.getResourceName());
        URL resource = implementation.getClassLoader().getResource(sideFile);
        ScriptComponentType componentType;
        if (resource == null) {
            throw new MissingSideFileException("Component type side file not found", sideFile);
            // TODO: or else implement introspection
        } else {
            componentType = loadFromSidefile(parent, resource, deploymentContext);
        }
        implementation.setComponentType(componentType);
    }

    @SuppressWarnings("unchecked")
    protected ScriptComponentType loadFromSidefile(CompositeComponent parent,
                                                   URL url,
                                                   DeploymentContext deploymentContext)
        throws LoaderException {
        ScriptComponentType scriptComponentType = new ScriptComponentType();
        return (ScriptComponentType) loaderRegistry
            .load(parent, scriptComponentType, url, ComponentType.class, deploymentContext);
    }

    protected String getSideFileName(String resourceName) {
        int lastDot = resourceName.lastIndexOf('.');
        if (lastDot != -1) {
            resourceName = resourceName.substring(0, lastDot);
        }
        return resourceName + ".componentType";
    }

}
