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

import org.apache.tuscany.spi.loader.ComponentTypeLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentTypeLoader implements ComponentTypeLoader<SystemImplementation> {
    public void load(SystemImplementation implementation, DeploymentContext deploymentContext) {
        Class<?> implClass = implementation.getImplementationClass();
        URL resource = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        if (resource == null) {
            loadByIntrospection(implementation);
        } else {
            loadFromSidefile(implementation, resource);
        }
    }

    protected void loadByIntrospection(SystemImplementation implementation) {
    }

    protected void loadFromSidefile(SystemImplementation implementation, URL sidefile) {
        throw new UnsupportedOperationException();
    }
}
