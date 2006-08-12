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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.idl.java.JavaServiceContract;

/**
 * Loads a Java interface definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class InterfaceJavaLoader extends LoaderExtension<ServiceContract> {
    public static final QName INTERFACE_JAVA = new QName(XML_NAMESPACE_1_0, "interface.java");

    @Constructor({"registry"})
    public InterfaceJavaLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return INTERFACE_JAVA;
    }

    public ServiceContract load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert INTERFACE_JAVA.equals(reader.getName());
        ServiceContract serviceContract = new JavaServiceContract();
        serviceContract.setInteractionScope(StAXUtil.interactionScope(reader.getAttributeValue(null, "scope")));
        String name = reader.getAttributeValue(null, "interface");
        if (name == null) {
            // allow "class" as well as seems to be a common mistake
            name = reader.getAttributeValue(null, "class");
        }
        serviceContract.setInterfaceName(name);
        serviceContract.setInterfaceClass(LoaderUtil.loadClass(name, deploymentContext.getClassLoader()));

        name = reader.getAttributeValue(null, "callbackInterface");
        serviceContract.setCallbackName(name);
        if (name != null) {
            serviceContract.setCallbackClass(LoaderUtil.loadClass(name, deploymentContext.getClassLoader()));
        }
        LoaderUtil.skipToEndElement(reader);
        return serviceContract;
    }
}
