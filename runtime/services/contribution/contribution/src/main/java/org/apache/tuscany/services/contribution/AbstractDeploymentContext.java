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
package org.apache.tuscany.services.contribution;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Base class for DeploymentContext implementations.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractDeploymentContext implements DeploymentContext {
    private final URI componentId;
    private boolean autowire;
    private final ClassLoader classLoader;
    private final URL scdlLocation;
    private final Map<URI, Component> components = new HashMap<URI, Component>();

    /**
     * Constructor defining properties of this context.
     *
     * @param classLoader  the classloader for loading application resources
     * @param scdlLocation the location of the SCDL defining this composite
     * @param componentId  the id of the component being deployed
     * @param autowire     if autowire is enabled
     */
    protected AbstractDeploymentContext(ClassLoader classLoader, URL scdlLocation, URI componentId, boolean autowire) {
        this.classLoader = classLoader;
        this.scdlLocation = scdlLocation;
        this.componentId = componentId;
        this.autowire = autowire;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public URL getScdlLocation() {
        return scdlLocation;
    }

    public URI getComponentId() {
        return componentId;
    }

    public boolean isAutowire() {
        return autowire;
    }

    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    @Deprecated
    public Map<URI, Component> getComponents() {
        return components;
    }
}
