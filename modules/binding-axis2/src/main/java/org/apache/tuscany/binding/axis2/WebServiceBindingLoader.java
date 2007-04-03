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

import static org.osoa.sca.Constants.SCA_NS;

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
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Parses a <code>WebServiceBindingDefinition</code> entry in an assembly XML file
 * 
 * TODO: TUSCANY-1153 support <wsa:EndpointReference>
 * 
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@SuppressWarnings("deprecation")
public class WebServiceBindingLoader extends LoaderExtension<WebServiceBindingDefinition> {
    public static final QName BINDING_WS = new QName(SCA_NS, "binding.ws");

    private WSDLDefinitionRegistry wsdlDefinitionRegistry;

    @Constructor( { "loaderRegistry", "wsdlDefinitionRegistry" })
    public WebServiceBindingLoader(@Reference LoaderRegistry loaderRegistry, 
                                   @Reference WSDLDefinitionRegistry wsdlDefinitionRegistry) {
        super(loaderRegistry);
        this.wsdlDefinitionRegistry = wsdlDefinitionRegistry;
    }

    public QName getXMLType() {
        return BINDING_WS;
    }

    public WebServiceBindingDefinition load(ModelObject object, XMLStreamReader reader,
                                  DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        String uri = reader.getAttributeValue(null, "uri");
        String wsdlElement = reader.getAttributeValue(null, "wsdlElement");
        String wsdliLocation = reader.getAttributeValue(null, "wsdlLocation");

        // TODO: keep these old attributes for now for backward compatability
        String endpoint = reader.getAttributeValue(null, "endpoint");
        String wsdlLocation = reader.getAttributeValue(null, "location");

        // TODO: support wsa:endpointreference
        
        LoaderUtil.skipToEndElement(reader);

        WebServiceBindingDefinition wsBinding;
        if (endpoint != null && endpoint.length() > 0) {
            // TODO: support these old attributes for now for backward compatability
            try {
                wsBinding = createBindingOld(uri, endpoint, wsdlLocation, deploymentContext);
            } catch (Exception e) {
                throw new LoaderException(e);
            }
        } else {
            wsBinding = createWSBinding(wsdlElement, wsdliLocation, uri, deploymentContext);
        }
        
        return wsBinding;
    }

    protected WebServiceBindingDefinition createWSBinding(String wsdlElement, String wsdliLocation, String uri, DeploymentContext deploymentContext) throws LoaderException {
        String ns = null;
        String serviceName = null;
        String portName = null;
        String bindingName = null;

        if (wsdlElement != null && wsdlElement.length() > 0) {

            ns = getWSDLNamespace(wsdlElement);

            String uriValue = getWSDLElementURIValue(wsdlElement, "wsdl.service");
            if (uriValue != null) {
                serviceName = uriValue;
            } else {
                uriValue = getWSDLElementURIValue(wsdlElement, "wsdl.port");
                if (uriValue != null) {
                    int i = uriValue.lastIndexOf('/');
                    if (i == -1) {
                        throw new IllegalArgumentException("Missing '/' seperator between service and port in wsdl.port() in wsdlElement attribute");
                    } 
                    serviceName = uriValue.substring(0, i);
                    portName = uriValue.substring(i+1);
                } else {
                    uriValue = getWSDLElementURIValue(wsdlElement, "wsdl.endpoint");
                    if (uriValue != null) {
                        throw new IllegalArgumentException("WSDL 2.0 not supported for '#wsdl.endpoint' in wsdlElement attribute");
                    } 
                    uriValue = getWSDLElementURIValue(wsdlElement, "wsdl.binding");
                    if (uriValue == null) {
                        throw new IllegalArgumentException("missing '#wsdl.service' or '#wsdl.port' or '#wsdl.endpoint'or '#wsdl.binding' in wsdlElement attribute");
                    }
                    bindingName = uriValue;
                }
            }
        }

        Definition definition = null;
        if (wsdliLocation != null && wsdliLocation.length() > 0) {
            try {
                definition = wsdlDefinitionRegistry.loadDefinition(wsdliLocation, deploymentContext.getClassLoader());
            } catch (Exception e) {
                throw new LoaderException("Exception loading WSDL", e);
            }
        } else if (ns != null ){
            definition = wsdlDefinitionRegistry.getDefinition(ns);
        }
        
        WebServiceBindingDefinition wsBinding = new WebServiceBindingDefinition(ns, definition, serviceName, portName, bindingName, uri);

        return wsBinding;
    }
    
    protected String getWSDLElementURIValue(String wsdlElement, String type) { 
        String value = null;
        String fullType = "#" + type + "(";
        int i = wsdlElement.indexOf(fullType);
        if (i > -1) {
            int j = wsdlElement.indexOf(')',i);
            if (j < 0) {
                throw new IllegalArgumentException("missing closing bracket ')' on " + fullType + " in wsdlElement attribute");
            }
            value = wsdlElement.substring(i + fullType.length(), j);
        }
        return value;
    }

    protected String getWSDLNamespace(String wsdlElement) {
        String ns = null;
        if (wsdlElement != null && wsdlElement.length() > 0) {
            int i = wsdlElement.indexOf('#');
            if (i < 0) {
                throw new IllegalArgumentException("missing '#' namespace delimiter in wsdlElement attribute");
            }
            if (i == 0) {
                throw new IllegalArgumentException("no namespace in wsdlElement attribute");
            }
            ns = wsdlElement.substring(0, i);
        }
        return ns;
    }

    @SuppressWarnings("unchecked")
    private WebServiceBindingDefinition createBindingOld(String uri, String endpoint, String wsdlLocation, DeploymentContext deploymentContext)
        throws WSDLException, IOException, LoaderException {
        // Get the WSDL port namespace and name
        if (endpoint != null) {
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