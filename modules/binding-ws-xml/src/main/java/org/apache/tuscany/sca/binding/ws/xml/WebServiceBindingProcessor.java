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

package org.apache.tuscany.sca.binding.ws.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.DefaultWSDLInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.WSDLInterfaceIntrospector;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class WebServiceBindingProcessor implements
    StAXArtifactProcessor<WebServiceBinding>, WebServiceConstants {

    private WSDLFactory wsdlFactory;
    private WSDLInterfaceIntrospector introspector;
    private WebServiceBindingFactory wsFactory;
    private PolicyFactory policyFactory;

    public WebServiceBindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.wsFactory = new DefaultWebServiceBindingFactory();
        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.introspector = new DefaultWSDLInterfaceIntrospector(wsdlFactory);
    }

    public WebServiceBinding read(XMLStreamReader reader) throws ContributionReadException {
        try {

            // Read a <binding.ws>
            WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
            wsBinding.setUnresolved(true);

            // Read policies
            readPolicies(wsBinding, reader);
            
            // Read the binding name
            String name = reader.getAttributeValue(null, NAME);
            if (name != null) {
                wsBinding.setName(name);
            }

            // Read URI
            String uri = reader.getAttributeValue(null, URI);
            if (uri != null) {
                wsBinding.setURI(uri);
            }

            // Read a qname in the form:
            // namespace#wsdl.???(name)
            String wsdlElement = reader.getAttributeValue(null, WSDL_ELEMENT);
            if (wsdlElement != null) {
                int index = wsdlElement.indexOf('#');
                if (index == -1) {
                    throw new ContributionReadException(
                                                        "Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                }
                String namespace = wsdlElement.substring(0, index);
                wsBinding.setNamespace(namespace);
                String localName = wsdlElement.substring(index + 1);
                if (localName.startsWith("wsdl.service")) {

                    // Read a wsdl.service
                    localName = localName.substring("wsdl.service(".length(), localName.length() - 1);
                    wsBinding.setServiceName(new QName(namespace, localName));

                } else if (localName.startsWith("wsdl.port")) {

                    // Read a wsdl.port
                    localName = localName.substring("wsdl.port(".length(), localName.length() - 1);
                    int s = localName.indexOf('/');
                    if (s == -1) {
                        throw new ContributionReadException(
                                                            "Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                    }
                    wsBinding.setServiceName(new QName(namespace, localName.substring(0, s)));
                    wsBinding.setPortName(localName.substring(s + 1));

                } else if (localName.startsWith("wsdl.endpoint")) {

                    // Read a wsdl.endpoint
                    localName = localName.substring("wsdl.endpoint(".length(), localName.length() - 1);
                    int s = localName.indexOf('/');
                    if (s == -1) {
                        throw new ContributionReadException(
                                                            "Invalid WebService binding wsdlElement attribute: " + wsdlElement);
                    }
                    wsBinding.setServiceName(new QName(namespace, localName.substring(0, s)));
                    wsBinding.setEndpointName(localName.substring(s + 1));

                } else if (localName.startsWith("wsdl.binding")) {

                    // Read a wsdl.service
                    localName = localName.substring("wsdl.binding(".length(), localName.length() - 1);
                    wsBinding.setBindingName(new QName(namespace, localName));

                } else {
                    throw new ContributionReadException(
                                                        "Invalid WebService binding wsdlElement attribute: " + wsdlElement);
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
                String wsdlElement = wsBinding.getServiceName().getNamespaceURI() + "#wsdl.port("
                                     + wsBinding.getServiceName().getLocalPart()
                                     + "/"
                                     + wsBinding.getPortName()
                                     + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

            } else if (wsBinding.getEndpointName() != null) {

                // Write namespace#wsdl.endpoint(service/endpoint)
                String wsdlElement = wsBinding.getServiceName().getNamespaceURI() + "#wsdl.endpoint("
                                     + wsBinding.getServiceName().getLocalPart()
                                     + "/"
                                     + wsBinding.getEndpointName()
                                     + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

            } else if (wsBinding.getBindingName() != null) {

                // Write namespace#wsdl.binding(binding)
                String wsdlElement = wsBinding.getBindingName().getNamespaceURI() + "#wsdl.binding("
                                     + wsBinding.getBindingName().getLocalPart()
                                     + ")";
                writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

            } else if (wsBinding.getServiceName() != null) {

                // Write namespace#wsdl.service(service)
                String wsdlElement = wsBinding.getServiceName().getNamespaceURI() + "#wsdl.service("
                                     + wsBinding.getServiceName().getLocalPart()
                                     + ")";
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

    public void resolve(WebServiceBinding model, ModelResolver resolver) throws ContributionResolveException {
        WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
        wsdlDefinition.setUnresolved(true);
        wsdlDefinition.setNamespace(model.getNamespace());
        wsdlDefinition = resolver.resolveModel(WSDLDefinition.class, wsdlDefinition);
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
                    Port port = service.getPort(model.getPortName());
                    model.setPort(port);
                    model.setBinding(port.getBinding());
                }
            }

            PortType portType = getPortType(model);
            if (portType != null) {
                WSDLInterfaceContract interfaceContract = wsdlFactory.createWSDLInterfaceContract();
                WSDLInterface wsdlInterface;
                try {
                    wsdlInterface = introspector.introspect(portType,
                                                                          wsdlDefinition.getInlinedSchemas(),
                                                                          resolver);
                } catch (InvalidInterfaceException e) {
                    throw new ContributionResolveException(e);
                }
                if (!model.isCallback()) {
                    interfaceContract.setInterface(wsdlInterface);
                } else {
                    interfaceContract.setCallbackInterface(wsdlInterface);
                }
                model.setBindingInterfaceContract(interfaceContract);
            }
        }
    }

    private PortType getPortType(WebServiceBinding model) {
        PortType portType = null;
        if (model.getService() != null) {
            // FIXME: How to find the compatible port?
            Map ports = model.getService().getPorts();
            if (!ports.isEmpty()) {
                Port port = (Port)ports.values().iterator().next();
                portType = port.getBinding().getPortType();
            }
        } else if (model.getPort() != null) {
            portType = model.getPort().getBinding().getPortType();
        } else if (model.getEndpoint() != null) {
            portType = model.getPort().getBinding().getPortType();
        } else if (model.getBinding() != null) {
            portType = model.getBinding().getPortType();
        }
        return portType;
    }

    public QName getArtifactType() {
        return WebServiceConstants.BINDING_WS_QNAME;
    }

    public Class<WebServiceBinding> getModelType() {
        return WebServiceBinding.class;
    }

    /**
     * Reads policy intents and policy sets.
     * @param attachPoint
     * @param reader
     */
    private void readPolicies(PolicySetAttachPoint attachPoint, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = attachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                requiredIntents.add(intent);
            }
        }

        value = reader.getAttributeValue(null, Constants.POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = attachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
                policySets.add(policySet);
            }
        }
    }
    
    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
    private QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

}
