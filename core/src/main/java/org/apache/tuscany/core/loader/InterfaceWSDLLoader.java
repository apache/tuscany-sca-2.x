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

import java.io.IOException;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.WSDLServiceContract;
import org.apache.tuscany.spi.services.wsdl.WSDLDefinitionRegistry;

/**
 * Loads a WSDL interface definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class InterfaceWSDLLoader extends LoaderExtension {
    private static final String WSDLI = "http://www.w3.org/2006/01/wsdl-instance";
    private static final String WSDLI_LOCATION = "wsdlLocation";

    private WSDLDefinitionRegistry wsdlRegistry;
    public static final QName INTERFACE_WSDL = new QName(XML_NAMESPACE_1_0, "interface.wsdl");

    @Autowire
    public void setWsdlRegistry(WSDLDefinitionRegistry wsdlRegistry) {
        this.wsdlRegistry = wsdlRegistry;
    }

    public QName getXMLType() {
        return INTERFACE_WSDL;
    }

    public WSDLServiceContract load(CompositeComponent parent,
                                    XMLStreamReader reader,
                                    DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert INTERFACE_WSDL.equals(reader.getName());
        WSDLServiceContract serviceContract = new WSDLServiceContract();
        serviceContract.setInteractionScope(StAXUtil.interactionScope(reader.getAttributeValue(null, "scope")));

        String location = reader.getAttributeValue(WSDLI, WSDLI_LOCATION);
        if (location != null) {
            try {
                wsdlRegistry.loadDefinition(location, deploymentContext.getClassLoader());
            } catch (IOException e) {
                LoaderException le = new LoaderException(e);
                le.setIdentifier(location);
                throw le;
            } catch (WSDLException e) {
                LoaderException le = new LoaderException(e);
                le.setIdentifier(location);
                throw le;
            }
        }

        String portTypeURI = reader.getAttributeValue(null, "interface");
        if (portTypeURI != null) {
            serviceContract.setPortType(getPortType(portTypeURI));
        }

        portTypeURI = reader.getAttributeValue(null, "callbackInterface");
        if (portTypeURI != null) {
            serviceContract.setCallbackPortType(getPortType(portTypeURI));
        }
        LoaderUtil.skipToEndElement(reader);
        return serviceContract;
    }

    protected PortType getPortType(String uri) {
        // fixme support WSDL 2.0 XPointer references and possible XML Schema QNames
        int index = uri.indexOf('#');
        String namespace = uri.substring(0, index);
        String name = uri.substring(index + 1);
        QName qname = new QName(namespace, name);
        return wsdlRegistry.getPortType(qname);
    }
}
