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
package org.apache.tuscany.spi.deployer;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.context.ScopeContext;

/**
 * An holder that can be used during the load process to store information
 * that is not part of the logical assembly model. This should be regarded as transient
 * and references to this context should not be stored inside the model.
 *
 * @version $Rev$ $Date$
 */
public class DeploymentContext {
    private final ClassLoader classLoader;
    private final XMLInputFactory xmlFactory;
    private final ScopeContext moduleScope;

    /**
     * Constructor specifying the loader for application resources.
     *
     * @param classLoader the loader for application resources
     * @param xmlFactory  a factory that can be used to obtain an StAX XMLStreamReader
     * @param moduleScope the scope context representing this deployment's MODULE scope
     */
    public DeploymentContext(ClassLoader classLoader, XMLInputFactory xmlFactory, ScopeContext moduleScope) {
        this.classLoader = classLoader;
        this.xmlFactory = xmlFactory;
        this.moduleScope = moduleScope;
    }

    /**
     * Returns a class loader that can be used to load application resources.
     *
     * @return a class loader that can be used to load application resources
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns a factory that can be used to obtain an StAX XMLStreamReader.
     *
     * @return a factory that can be used to obtain an StAX XMLStreamReader
     */
    public XMLInputFactory getXmlFactory() {
        return xmlFactory;
    }

    /**
     * Returns the ScopeContext for the MODULE scope that will be associated with this deployment unit.
     * @return the ScopeContext for the MODULE scope that will be associated with this deployment unit
     */
    public ScopeContext getModuleScope() {
        return moduleScope;
    }
}
