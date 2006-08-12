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
package org.apache.tuscany.idl.wsdl;

import java.io.IOException;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
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
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.services.wsdl.WSDLDefinitionRegistry;

/**
 * Loads a WSDL interface definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class InterfaceWSDLLoader extends LoaderExtension {
    public static final QName INTERFACE_WSDL = new QName(XML_NAMESPACE_1_0, "interface.wsdl");
    private static final String WSDLI = "http://www.w3.org/2006/01/wsdl-instance";
    private static final String WSDLI_LOCATION = "wsdlLocation";

    private WSDLDefinitionRegistry wsdlRegistry;

    @Constructor({"registry"})
    public InterfaceWSDLLoader(@Autowire LoaderRegistry registry,
                               @Autowire WSDLDefinitionRegistry wsdlRegistry) {
        super(registry);
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

        String interfaceURI = reader.getAttributeValue(null, "interface");
        if (interfaceURI == null) {
            throw new InvalidValueException("interface");
        }

        String callbackURI = reader.getAttributeValue(null, "callbackInterface");
        String wsdlLocation = reader.getAttributeValue(WSDLI, WSDLI_LOCATION);
        // FIXME set the interaction scope
//        serviceContract.setInteractionScope(StAXUtil.interactionScope(reader.getAttributeValue(null, "scope")));
        LoaderUtil.skipToEndElement(reader);

        if (wsdlLocation != null) {
            try {
                wsdlRegistry.loadDefinition(wsdlLocation, deploymentContext.getClassLoader());
            } catch (IOException e) {
                LoaderException le = new LoaderException(e);
                le.setIdentifier(wsdlLocation);
                throw le;
            } catch (WSDLException e) {
                LoaderException le = new LoaderException(e);
                le.setIdentifier(wsdlLocation);
                throw le;
            }
        }

        WSDLServiceContract serviceContract = new WSDLServiceContract();
        serviceContract.setPortType(getPortType(interfaceURI));
        if (callbackURI != null) {
            serviceContract.setCallbackPortType(getPortType(callbackURI));
        }
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
