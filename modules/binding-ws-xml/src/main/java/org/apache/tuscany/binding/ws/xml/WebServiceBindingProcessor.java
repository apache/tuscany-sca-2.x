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

package org.apache.tuscany.binding.ws.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.BaseArtifactProcessor;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.binding.ws.impl.DefaultWebServiceBindingFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.interfacedef.wsdl.impl.DefaultWSDLFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;

public class WebServiceBindingProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor<WebServiceBinding>, WebServiceConstants {

    private WebServiceBindingFactory wsFactory;

    public WebServiceBindingProcessor(AssemblyFactory assemblyFactory,
                                      PolicyFactory policyFactory,
                                      WebServiceBindingFactory wsFactory) {
        super(assemblyFactory, policyFactory, null);
        this.wsFactory = wsFactory;
    }
    
    public WebServiceBindingProcessor() {
        this(new DefaultAssemblyFactory(), new DefaultPolicyFactory(), new DefaultWebServiceBindingFactory());
    }

    public WebServiceBinding read(XMLStreamReader reader) throws ContributionReadException {
        try {
    
            // Read a <binding.ws>
            WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
            wsBinding.setUnresolved(true);
            
            // Read policies
            readPolicies(wsBinding, reader);

            // Read URI
            wsBinding.setURI(reader.getAttributeValue(null, Constants.URI));
            
            // Read a qname in the form:
            // namespace#wsdl.???(name)
            String wsdlElement = reader.getAttributeValue(null, WSDL_ELEMENT);
            if (wsdlElement != null) {
                int index = wsdlElement.indexOf('#');
                if (index == -1) {
                    throw new ContributionReadException("Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                }
                String namespace = wsdlElement.substring(0, index);
                String name = wsdlElement.substring(index + 1);
                if (name.startsWith("wsdl.service")) {
                    
                    // Read a wsdl.service
                    name = name.substring("wsdl.service(".length(), name.length() - 1);
                    wsBinding.setServiceName(new QName(namespace, name));
                    
                } else if (name.startsWith("wsdl.port")) {
                    
                    // Read a wsdl.port
                    name = name.substring("wsdl.port(".length(), name.length() - 1);
                    int s = name.indexOf('/');
                    if (s == -1) {
                        throw new ContributionReadException("Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                    }
                    wsBinding.setServiceName(new QName(namespace, name.substring(0, s)));
                    wsBinding.setPortName(name.substring(s+1));
                    
                } else if (name.startsWith("wsdl.endpoint")) {
                    
                    // Read a wsdl.endpoint
                    name = name.substring("wsdl.endpoint(".length(), name.length() - 1);
                    int s = name.indexOf('/');
                    if (s == -1) {
                        throw new ContributionReadException("Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                    }
                    wsBinding.setServiceName(new QName(namespace, name.substring(0, s)));
                    wsBinding.setEndpointName(name.substring(s+1));
                    
                } else if (name.startsWith("wsdl.binding")) {
                        
                    // Read a wsdl.service
                    name = name.substring("wsdl.binding(".length(), name.length() - 1);
                    wsBinding.setBindingName(new QName(namespace, name));
                    
                } else {
                    throw new ContributionReadException("Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                }
            }
            
            // Read wsdlLocation
            wsBinding.setLocation(reader.getAttributeValue(WSDLI_NS, WSDL_LOCATION));
                
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && BINDING_WS_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return wsBinding;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(WebServiceBinding wsBinding, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write a <binding.ws>
            writer.writeStartElement(Constants.SCA10_NS, BINDING_WS);
            
            // Write binding URI
            if (wsBinding.getURI() != null) {
                writer.writeAttribute(Constants.URI, wsBinding.getURI());
            }

            // Write wsdlElement attribute
            if (wsBinding.getPortName() != null) {

                // Write namespace#wsdl.port(service/port)
                String wsdlElement = wsBinding.getServiceName().getNamespaceURI() +
                    "#wsdl.port(" + wsBinding.getServiceName().getLocalPart() + "/" +
                    wsBinding.getPortName() + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);
                
            } else if (wsBinding.getEndpointName() != null) {

                // Write namespace#wsdl.endpoint(service/endpoint)
                String wsdlElement = wsBinding.getServiceName().getNamespaceURI() +
                    "#wsdl.endpoint(" + wsBinding.getServiceName().getLocalPart() + "/" +
                    wsBinding.getEndpointName() + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

            } else if (wsBinding.getBindingName() != null) {
    
                // Write namespace#wsdl.binding(binding)
                String wsdlElement = wsBinding.getBindingName().getNamespaceURI() +
                    "#wsdl.binding(" + wsBinding.getBindingName().getLocalPart() + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

            } else if (wsBinding.getServiceName() != null) {
                
                // Write namespace#wsdl.service(service)
                String wsdlElement = wsBinding.getServiceName().getNamespaceURI() +
                    "#wsdl.service(" + wsBinding.getServiceName().getLocalPart() + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);
            }
            
            // Write location
            if (wsBinding.getLocation() != null) {
                writer.writeAttribute(WSDLI_NS, WSDL_LOCATION, wsBinding.getLocation());
            }
            
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(WebServiceBinding model, ArtifactResolver resolver) throws ContributionResolveException {
        WSDLDefinition wsdlDefinition = new DefaultWSDLFactory().createWSDLDefinition();
        wsdlDefinition.setUnresolved(true);
        wsdlDefinition.setNamespace(model.getServiceName().getNamespaceURI());
        wsdlDefinition = resolver.resolve(WSDLDefinition.class, wsdlDefinition);
        if (!wsdlDefinition.isUnresolved()) {
            model.setDefinition(wsdlDefinition);
            Definition definition = wsdlDefinition.getDefinition();
            if (model.getBindingName() != null) {
                model.setBinding(definition.getBinding(model.getBindingName()));
            }
            if (model.getServiceName() != null) {
                Service service = definition.getService(model.getServiceName());
                model.setService(service);
                if (service != null && model.getPortName() != null) {
                    model.setPort(service.getPort(model.getPortName()));
                }
            }
        }
    }
    
    public void wire(WebServiceBinding model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }
    
    public QName getArtifactType() {
        return WebServiceConstants.BINDING_WS_QNAME;
    }
    
    public Class<WebServiceBinding> getModelType() {
        return WebServiceBinding.class;
    }
}
