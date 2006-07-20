/*
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
package org.apache.tuscany.core.deployer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Base class for DeploymentContext implementations.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractDeploymentContext implements DeploymentContext {
    private final ClassLoader classLoader;
    private final URL scdlLocation;
    private final Map<String, Object> properties = new HashMap<String, Object>();

    protected AbstractDeploymentContext(ClassLoader classLoader, URL scdlLocation) {
        this.classLoader = classLoader;
        this.scdlLocation = scdlLocation;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public URL getScdlLocation() {
        return scdlLocation;
    }

    public Object getExtension(String name) {
        return properties.get(name);
    }

    public void putExtension(String name, Object value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }
}
