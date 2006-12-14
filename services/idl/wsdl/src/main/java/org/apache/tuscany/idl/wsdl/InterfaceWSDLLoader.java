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
import java.util.HashMap;
import java.util.Map;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.ModelObject;

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

    private InterfaceWSDLIntrospector introspector;

    @Constructor({"registry", "wsdlRegistry", "introspector"})
    public InterfaceWSDLLoader(@Autowire LoaderRegistry registry,
                               @Autowire WSDLDefinitionRegistry wsdlRegistry,
                               @Autowire InterfaceWSDLIntrospector introspector) {
        super(registry);
        this.wsdlRegistry = wsdlRegistry;
        this.introspector = introspector;
    }

    public QName getXMLType() {
        return INTERFACE_WSDL;
    }

    public WSDLServiceContract load(
        CompositeComponent parent,
        ModelObject object, XMLStreamReader reader,
        DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert INTERFACE_WSDL.equals(reader.getName());

        String interfaceURI = reader.getAttributeValue(null, "interface");
        if (interfaceURI == null) {
            throw new InvalidValueException("interface");
        }

        String callbackURI = reader.getAttributeValue(null, "callbackInterface");
        String wsdlLocation = reader.getAttributeValue(WSDLI, WSDLI_LOCATION);

        Map<Class<?>, ModelObject> extensions = new HashMap<Class<?>, ModelObject>();
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                ModelObject mo = registry.load(parent, null, reader, deploymentContext);
                if (mo != null) {
                    extensions.put(mo.getClass(), mo);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (reader.getName().equals(INTERFACE_WSDL)) {
                    break;
                }
            }
        }
        // FIXME set the interaction scope
        // serviceContract.setInteractionScope(StAXUtil.interactionScope(reader.getAttributeValue(null, "scope")));

        if (wsdlLocation != null) {
            try {
                wsdlRegistry.loadDefinition(wsdlLocation, deploymentContext.getClassLoader());
            } catch (IOException e) {
                throw new LoaderException(wsdlLocation, e);
            } catch (WSDLException e) {
                throw new LoaderException(wsdlLocation, e);
            }
        }

        PortType portType = getPortType(interfaceURI);
        if (portType == null) {
            throw new MissingResourceException(interfaceURI);
        }
        PortType callback = null;
        if (callbackURI != null) {
            callback = getPortType(callbackURI);
        }
        try {
            WSDLServiceContract contract = introspector.introspect(portType, callback);
            DataType<?> dataType = (DataType<?>) extensions.get(DataType.class);
            if (dataType != null) {
                contract.setDataBinding(dataType.getDataBinding());
            }
            contract.getExtensions().putAll(extensions);
            return contract;
        } catch (InvalidServiceContractException e) {
            throw new LoaderException(wsdlLocation, e);
        }
    }

    protected PortType getPortType(String uri) {
        // Syntax: <WSDL-namespace-URI>#wsdl.interface(<portTypeOrInterface-name>)
        int index = uri.indexOf('#');
        String namespace = uri.substring(0, index);
        String name = uri.substring(index + 1);
        name = name.substring("wsdl.interface(".length(), name.length() - 1);
        QName qname = new QName(namespace, name);
        return wsdlRegistry.getPortType(qname);
    }
}
