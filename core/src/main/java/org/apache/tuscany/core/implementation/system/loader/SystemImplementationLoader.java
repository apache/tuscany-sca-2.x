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
package org.apache.tuscany.core.implementation.system.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.annotation.Autowire;

/**
 * Loads information for a system implementation
 *
 * @version $Rev: 416156 $ $Date: 2006-06-21 16:02:33 -0700 (Wed, 21 Jun 2006) $
 */
public class SystemImplementationLoader extends LoaderExtension<SystemImplementation> {
    public static final QName SYSTEM_IMPLEMENTATION =
        new QName("http://tuscany.apache.org/xmlns/system/1.0-chianti-SNAPSHOT", "implementation.system");

    @Constructor({"registry"})
    public SystemImplementationLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public SystemImplementation load(CompositeComponent parent,
                                     XMLStreamReader reader,
                                     DeploymentContext deploymentContext
    )
        throws XMLStreamException, LoaderException {
        assert SYSTEM_IMPLEMENTATION.equals(reader.getName());
        SystemImplementation implementation = new SystemImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = LoaderUtil.loadClass(implClass, deploymentContext.getClassLoader());
        implementation.setImplementationClass(implementationClass);
        registry.loadComponentType(parent, implementation, deploymentContext);
        LoaderUtil.skipToEndElement(reader);
        return implementation;
    }

    public QName getXMLType() {
        return SYSTEM_IMPLEMENTATION;
    }

}
