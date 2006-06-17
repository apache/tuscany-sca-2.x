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

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.JavaServiceContract;

/**
 * Loads a Java interface definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class InterfaceJavaLoader extends LoaderExtension<JavaServiceContract> {
    public InterfaceJavaLoader() {
    }

    public InterfaceJavaLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return AssemblyConstants.INTERFACE_JAVA;
    }

    public JavaServiceContract load(XMLStreamReader reader,
                                    DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert AssemblyConstants.INTERFACE_JAVA.equals(reader.getName());
        JavaServiceContract serviceContract = new JavaServiceContract();
        serviceContract.setInteractionScope(StAXUtil.interactionScope(reader.getAttributeValue(null, "scope")));
        String name = reader.getAttributeValue(null, "interface");
        if (name == null) {
            // allow "class" as well as seems to be a common mistake
            name = reader.getAttributeValue(null, "class");
        }
        serviceContract.setInterfaceName(name);
        serviceContract.setInterfaceClass(StAXUtil.loadClass(name, deploymentContext.getClassLoader()));

        name = reader.getAttributeValue(null, "callbackInterface");
        serviceContract.setCallbackName(name);
        if (name != null) {
            serviceContract.setCallbackClass(StAXUtil.loadClass(name, deploymentContext.getClassLoader()));
        }
        StAXUtil.skipToEndElement(reader);
        return serviceContract;
    }
}
