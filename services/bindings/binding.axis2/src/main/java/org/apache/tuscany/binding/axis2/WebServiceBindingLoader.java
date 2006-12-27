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
package org.apache.tuscany.binding.axis2;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import java.io.IOException;
import java.util.Collection;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ModelObject;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Scope;

/**
 * Parses a <code>WebServiceBindingDefinition</code> entry in an assembly XML file
 * 
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@SuppressWarnings("deprecation")
public class WebServiceBindingLoader extends LoaderExtension<WebServiceBindingDefinition> {
    public static final QName BINDING_WS = new QName(XML_NAMESPACE_1_0, "binding.ws");

    private WSDLDefinitionRegistry wsdlDefinitionRegistry;

    @Constructor( { "loaderRegistry", "wsdlDefinitionRegistry" })
    public WebServiceBindingLoader(@Autowire LoaderRegistry loaderRegistry, 
            @Autowire WSDLDefinitionRegistry wsdlDefinitionRegistry) {
        super(loaderRegistry);
        this.wsdlDefinitionRegistry = wsdlDefinitionRegistry;
    }

    public QName getXMLType() {
        return BINDING_WS;
    }

    public WebServiceBindingDefinition load(CompositeComponent parent, ModelObject object, XMLStreamReader reader,
                                  DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        // not sure what uri was here ? String uri = reader.getAttributeValue(null, "uri");
        String uri = null;
        String endpoint = reader.getAttributeValue(null, "endpoint");
        String wsdlLocation = reader.getAttributeValue(null, "location");
        LoaderUtil.skipToEndElement(reader);
        try {
            return createBinding(uri, endpoint, wsdlLocation, deploymentContext);
        } catch (Exception e) {
            throw new LoaderException(e);
        }

    }

    @SuppressWarnings("unchecked")
    private WebServiceBindingDefinition createBinding(String uri, String endpoint, String wsdlLocation, DeploymentContext deploymentContext)
        throws WSDLException, IOException, LoaderException {
        // Get the WSDL port namespace and name
        if (uri == null && endpoint != null) {
            int h = endpoint.indexOf('#');
            String serviceName;
            String portName;

            String namespace = endpoint.substring(0, h);
            String fragment = endpoint.substring(h + 1);
            if (fragment.startsWith("wsdl.endpoint(") && fragment.endsWith(")")) {
                fragment = fragment.substring(14, fragment.length() - 1);
                int slash = fragment.indexOf('/');
                if (slash != -1) {
                    serviceName = fragment.substring(0, slash);
                    portName = fragment.substring(slash + 1);
                } else {
                    serviceName = null;
                    portName = fragment;
                }
            } else {
                serviceName = null;
                portName = fragment;
            }
            // FIXME need to find out how to get wsdl and what context to use --- terrible hack attack!
            if (null == wsdlLocation) {
                throw new Axis2BindingRunTimeException(
                        "Failed to determine wsdl location on binding. Try specifying 'location' attribute on  binding.");
            }    
            Definition definition =
                    wsdlDefinitionRegistry.loadDefinition(namespace+" "+wsdlLocation, deploymentContext.getClassLoader());

            Port thePort = null;
            Service service = null;
            // Find the port with the given name
            for (Service serv : (Collection<Service>) definition.getServices().values()) {
                QName sqn = serv.getQName();
                if (serviceName != null && !serviceName.equals(sqn.getLocalPart())) {
                    continue;
                }

                Port p = serv.getPort(portName);
                if (p != null) {
                    service = serv;
                    thePort = p;
                    break;
                }
            }
            if (thePort == null) {
                throw new IllegalArgumentException("Cannot find WSDL port " + endpoint);

            }
            return new WebServiceBindingDefinition(definition, thePort, uri, endpoint, service);
        }
        // FIXME: Find the first port?
        throw new LoaderException("Web Service endpoint cannot be resolved: " + endpoint);

    }

}