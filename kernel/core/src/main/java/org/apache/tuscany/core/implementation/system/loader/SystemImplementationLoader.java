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
package org.apache.tuscany.core.implementation.system.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.ModelObject;

import org.apache.tuscany.core.implementation.system.model.SystemImplementation;

/**
 * Loads information for a system implementation
 *
 * @version $Rev$ $Date$
 */
public class SystemImplementationLoader extends LoaderExtension<SystemImplementation> {
    public static final QName SYSTEM_IMPLEMENTATION =
        new QName("http://tuscany.apache.org/xmlns/sca/system/2.0-alpha", "implementation.system");

    public SystemImplementationLoader(@Reference LoaderRegistry registry) {
        super(registry);
    }

    public SystemImplementation load(
        ModelObject object, XMLStreamReader reader,
        DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert SYSTEM_IMPLEMENTATION.equals(reader.getName());
        SystemImplementation implementation = new SystemImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = LoaderUtil.loadClass(implClass, deploymentContext.getClassLoader());
        implementation.setImplementationClass(implementationClass);
        while (true) {
            int code = reader.next();
            if (code == XMLStreamConstants.START_ELEMENT) {
                throw new UnrecognizedElementException(reader.getName());
            } else if (code == XMLStreamConstants.END_ELEMENT) {
                return implementation;
            }
        }
    }

    public QName getXMLType() {
        return SYSTEM_IMPLEMENTATION;
    }

}
