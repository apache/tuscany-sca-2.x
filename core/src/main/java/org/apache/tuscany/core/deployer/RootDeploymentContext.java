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
 * A holder that can be used during the load process to store information that is not part of the logical assembly
 * model. This should be regarded as transient and references to this context should not be stored inside the model.
 *
 * @version $Rev$ $Date$
 */
public class RootDeploymentContext extends AbstractDeploymentContext {
    private final XMLInputFactory xmlFactory;
    private final ScopeContainer moduleScope;

    /**
     * Constructor specifying the loader for application resources.
     *
     * @param classLoader the loader for application resources
     * @param xmlFactory  a factory that can be used to obtain an StAX XMLStreamReader
     * @param moduleScope the scope context representing this deployment's MODULE scope
     * @param scdlLocation the location of the SCDL being deployed
     */
    public RootDeploymentContext(ClassLoader classLoader,
                                 XMLInputFactory xmlFactory,
                                 ScopeContainer moduleScope,
                                 URL scdlLocation) {
        super(classLoader, scdlLocation);
        this.xmlFactory = xmlFactory;
        this.moduleScope = moduleScope;
    }

    public DeploymentContext getParent() {
        return null;
    }

    public XMLInputFactory getXmlFactory() {
        return xmlFactory;
    }

    public ScopeContainer getModuleScope() {
        return moduleScope;
    }
}
