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
package org.apache.tuscany.core.deployer;

import java.net.URL;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * An holder that can be used during the load process to store information that is not part of the logical assembly
 * model. This should be regarded as transient and references to this context should not be stored inside the model.
 *
 * @version $Rev$ $Date$
 */
public class ChildDeploymentContext implements DeploymentContext {
    private final DeploymentContext parent;
    private final ClassLoader classLoader;
    private final URL scdlLocation;

    /**
     * Constructor specifying the loader for application resources.
     *
     * @param parent the parent of this context
     * @param classLoader the loader for application resources
     * @param scdlLocation the location of the SCDL being deployed
     */
    public ChildDeploymentContext(DeploymentContext parent, ClassLoader classLoader, URL scdlLocation) {
        assert parent != null;
        this.parent = parent;
        this.classLoader = classLoader;
        this.scdlLocation = scdlLocation;
    }

    public DeploymentContext getParent() {
        return parent;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public XMLInputFactory getXmlFactory() {
        return parent.getXmlFactory();
    }

    public ScopeContainer getModuleScope() {
        return parent.getModuleScope();
    }

    public URL getScdlLocation() {
        return scdlLocation;
    }
}
