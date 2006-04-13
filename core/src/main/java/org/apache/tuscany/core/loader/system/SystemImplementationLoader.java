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
package org.apache.tuscany.core.loader.system;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.osoa.sca.annotations.Scope;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class SystemImplementationLoader extends AbstractLoader {
    public static final QName SYSTEM_IMPLEMENTATION = new QName("http://org.apache.tuscany/xmlns/system/0.9", "implementation.system");

    private ComponentTypeIntrospector introspector;

    @Autowire
    public void setIntrospector(ComponentTypeIntrospector introspector) {
        this.introspector = introspector;
    }

    public QName getXMLType() {
        return SYSTEM_IMPLEMENTATION;
    }

    public Class<SystemImplementation> getModelType() {
        return SystemImplementation.class;
    }

    public SystemImplementation load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert SYSTEM_IMPLEMENTATION.equals(reader.getName());
        SystemImplementation implementation = factory.createSystemImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass;
        try {
            implementationClass = resourceLoader.loadClass(implClass);
            implementation.setImplementationClass(implementationClass);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationLoadException(e);
        }

        // todo we should allow componentType sidefiles for system implementations
        implementation.setComponentInfo(introspector.introspect(implementationClass));

        StAXUtil.skipToEndElement(reader);
        return implementation;
    }
}
