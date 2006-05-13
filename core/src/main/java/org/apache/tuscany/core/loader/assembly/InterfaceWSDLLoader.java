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
package org.apache.tuscany.core.loader.assembly;

import java.io.IOException;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.MissingInterfaceException;
import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;

/**
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class InterfaceWSDLLoader extends AbstractLoader {
    private static final String WSDLI = "http://www.w3.org/2006/01/wsdl-instance";
    private static final String WSDLI_LOCATION = "wsdlLocation";

    private WSDLDefinitionRegistry wsdlRegistry;

    @Autowire
    public void setWsdlRegistry(WSDLDefinitionRegistry wsdlRegistry) {
        this.wsdlRegistry = wsdlRegistry;
    }

    public QName getXMLType() {
        return AssemblyConstants.INTERFACE_WSDL;
    }

    public WSDLServiceContract load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        assert AssemblyConstants.INTERFACE_WSDL.equals(reader.getName());
        WSDLServiceContract serviceContract = factory.createWSDLServiceContract();
        serviceContract.setScope(Scope.INSTANCE);

        String location = reader.getAttributeValue(WSDLI, WSDLI_LOCATION);
        if (location != null) {
            try {
                wsdlRegistry.loadDefinition(location, loaderContext.getResourceLoader());
            } catch (IOException e) {
                throw new MissingInterfaceException(e);
            } catch (WSDLException e) {
                throw new MissingInterfaceException(e);
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
        StAXUtil.skipToEndElement(reader);
        return serviceContract;
    }

    protected PortType getPortType(String uri) throws MissingInterfaceException {
        
        // We currently support two syntaxes for specifying a WSDL portType:
        // namespace#portTypeName, this is what we supported in the initial contribution, we will
        // deprecate this after M1
        // namespace#wsdl.interface(portTypeName), this is the WSDL 2.0 syntax
        
        int index = uri.indexOf('#');
        String namespace = uri.substring(0, index);
        String fragment = uri.substring(index + 1);
        String localName;
        if (fragment.startsWith("wsdl.interface(") && fragment.endsWith(")")) {
            localName = fragment.substring(15, fragment.length()-1);
        } else {
            localName = fragment;
        }
        QName qname = new QName(namespace, localName);
        PortType portType = wsdlRegistry.getPortType(qname);
        if (portType == null) {
            throw new MissingInterfaceException(uri);
        }
        return portType;
    }
}
