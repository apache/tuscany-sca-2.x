/**
 *
 * Copyright 2005 The Apache Software Foundation
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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.system.model.SystemImplementation;

/**
 * Loads information for a system implementation
 *
 * @version $Rev$ $Date$
 */
public class SystemImplementationLoader extends LoaderExtension<SystemImplementation> {
    public static final QName SYSTEM_IMPLEMENTATION =
        new QName("http://tuscany.apache.org/xmlns/system/0.9", "implementation.system");

    public SystemImplementationLoader() {
    }

    public SystemImplementationLoader(LoaderRegistry registry) {
        super(registry);
    }

    public SystemImplementation load(XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert SYSTEM_IMPLEMENTATION.equals(reader.getName());
        SystemImplementation implementation = new SystemImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = StAXUtil.loadClass(implClass, deploymentContext.getClassLoader());
        implementation.setImplementationClass(implementationClass);
        registry.loadComponentType(implementation, deploymentContext);
        StAXUtil.skipToEndElement(reader);
        return implementation;
    }

    protected QName getXMLType() {
        return SYSTEM_IMPLEMENTATION;
    }

}
