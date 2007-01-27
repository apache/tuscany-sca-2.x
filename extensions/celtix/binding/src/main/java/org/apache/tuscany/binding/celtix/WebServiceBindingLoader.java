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
package org.apache.tuscany.binding.celtix;


import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.InputSource;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistryImpl.Monitor;

/**
 * Parses a <code>WebServiceBindingDefinition</code> entry in an assembly XML file
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class WebServiceBindingLoader extends LoaderExtension<WebServiceBindingDefinition> {
    public static final QName BINDING_WS = new QName(XML_NAMESPACE_1_0, "binding.ws");

    protected WSDLDefinitionRegistry wsdlRegistry;

    @Constructor({"registry"})
    public WebServiceBindingLoader(@Autowire LoaderRegistry registry) {
        super(registry);

        //FIXME:  this is a  hack, WSDLDefinitionRegistry should not be created here
        if (wsdlRegistry == null) {
            try {
                wsdlRegistry = new WSDLDefinitionRegistryImpl();
                Monitor monitor = new Monitor() {
                    public void readingWSDL(String namespace, URL location) {
                    }

                    public void cachingDefinition(String namespace, URL location) {
                    }
                };

                ((WSDLDefinitionRegistryImpl) wsdlRegistry).setMonitor(monitor);
            } catch (javax.wsdl.WSDLException e) {
                //do nothing
            }
        }
    }

    public QName getXMLType() {
        return BINDING_WS;
    }

    public WebServiceBindingDefinition load(CompositeComponent parent,
											 ModelObject object,
                                  XMLStreamReader reader,
                                  DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        // not sure what uri was here ? String uri = reader.getAttributeValue(null, "uri");
        String uri = null;
        String endpointAttribute = reader.getAttributeValue(null, "endpoint");
        //String portURI = reader.getAttributeValue(null, "port");
        String wsdlLocation = reader.getAttributeValue(null, "location");
        try {
            return createBinding(uri, endpointAttribute, wsdlLocation, deploymentContext);
        } catch (Exception e) {

            throw new LoaderException(e);
        }
    }

    private WebServiceBindingDefinition createBinding(String port, String portURI, String wsdlLocation,
                                            DeploymentContext deploymentContext)
        throws WSDLException, IOException {
        List<Definition> definitions = null;
        // FIXME wsdlRegistry.getDefinitionsForNamespace(portNamespace,
        // resourceLoader);
        // Get the WSDL port namespace and name
        if (port == null && portURI != null) {
            int h = portURI.indexOf('#');
            String portNamespace = portURI.substring(0, h);
            String serviceName;
            String portName;

            String fragment = portURI.substring(h + 1);
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
            // URL wsdlurl = Thread.currentThread().getContextClassLoader().getResource(wsdlLocation);
            if (null == wsdlLocation) {
                throw new RuntimeException("Failed to determin wsdl location on binding. "
                    + "Try specifying 'location' attribute on  binding.");
            }
            URL wsdlurl = deploymentContext.getClassLoader().getResource(wsdlLocation);
            if (wsdlurl == null) {
                throw new RuntimeException("Failed to load wsdl from '" + wsdlLocation + "'");
            }

            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            InputSource input = new InputSource(wsdlurl.openStream());
            Definition wsdlDef = reader.readWSDL(wsdlurl.toString(), input);
            definitions = new LinkedList<Definition>();
            definitions.add(wsdlDef);
            // FIXME all the above needs to better addressed.

            //FIXME: if a global wsdl cached is used, we need to do a registration here
            String namespace = wsdlDef.getTargetNamespace();
            wsdlRegistry.loadDefinition(namespace, wsdlurl);

            Definition definition = null;
            Port thePort = null;
            Service service = null;
            for (Definition def : definitions) {

                // Find the port with the given name
                for (Service serv : (Collection<Service>) def.getServices().values()) {
                    QName sqn = serv.getQName();
                    if (serviceName != null
                        && !serviceName.equals(sqn.getLocalPart())) {
                        continue;
                    }

                    Port p = serv.getPort(portName);
                    if (p != null) {
                        service = serv;
                        definition = def;
                        thePort = p;
                        break;
                    }
                }
            }
            if (thePort == null) {
                throw new IllegalArgumentException("Cannot find WSDL port " + portURI);

            }
            WebServiceBindingDefinition wsBinding = new WebServiceBindingDefinition(definition, thePort, port, portURI, service);
            wsBinding.setWSDLDefinitionRegistry(wsdlRegistry);
            return wsBinding;
        }
        // FIXME - return a broken binding for now
        return new WebServiceBindingDefinition(null, null, null, portURI, null);

    }
}
