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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;

/**
 * Loads a Java interface definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class InterfaceJavaLoader extends LoaderExtension<JavaServiceContract> {
    public static final QName INTERFACE_JAVA = new QName(XML_NAMESPACE_1_0, "interface.java");

    public InterfaceJavaLoader() {
    }

    public InterfaceJavaLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return INTERFACE_JAVA;
    }

    public JavaServiceContract load(CompositeComponent parent,
                                    XMLStreamReader reader,
                                    DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert INTERFACE_JAVA.equals(reader.getName());
        JavaServiceContract serviceContract = new JavaServiceContract();
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
