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
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.xml.ConfiguredOperationProcessor;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.assembly.xml.PolicyAttachPointProcessor;
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
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * This is the StAXArtifactProcessor for the Web Services Binding.
 *
 * @version $Rev$ $Date$
 */
public class WebServiceBindingProcessor implements StAXArtifactProcessor<WebServiceBinding>, WebServiceConstants {

    private final static Logger logger = Logger.getLogger(WebServiceBindingProcessor.class.getName());    

    private WSDLFactory wsdlFactory;
    private WebServiceBindingFactory wsFactory;
    private PolicyFactory policyFactory;
    private PolicyAttachPointProcessor policyProcessor;
    private IntentAttachPointTypeFactory intentAttachPointTypeFactory;
    private ConfiguredOperationProcessor configuredOperationProcessor;

    public WebServiceBindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.wsFactory = modelFactories.getFactory(WebServiceBindingFactory.class);
        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
        this.intentAttachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        this.configuredOperationProcessor = new ConfiguredOperationProcessor(modelFactories);
    }

    public WebServiceBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        // Read a <binding.ws>
        WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
        /*IntentAttachPointType bindingType = intentAttachPointTypeFactory.createBindingType();
        bindingType.setName(getArtifactType());
        bindingType.setUnresolved(true);
        ((PolicySetAttachPoint)wsBinding).setType(bindingType);*/
        wsBinding.setUnresolved(true);

        // Read policies
        policyProcessor.readPolicies(wsBinding, reader);

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
        Boolean wsdlElementIsBinding = null;
        String wsdlElement = reader.getAttributeValue(null, WSDL_ELEMENT);
        if (wsdlElement != null) {
            int index = wsdlElement.indexOf('#');
            if (index == -1) {
                throw new ContributionReadException("Invalid WebService binding wsdlElement attribute: " + wsdlElement);
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

                // Read a wsdl.binding
                localName = localName.substring("wsdl.binding(".length(), localName.length() - 1);
                wsBinding.setBindingName(new QName(namespace, localName));

                wsdlElementIsBinding = true;

            } else {
                throw new ContributionReadException("Invalid WebService binding wsdlElement attribute: " + wsdlElement);
            }
        }

        // Read wsdlLocation
        wsBinding.setLocation(reader.getAttributeValue(WSDLI_NS, WSDL_LOCATION));

        ConfiguredOperation confOp = null;
        // Skip to end element
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case START_ELEMENT: {
                    if (END_POINT_REFERENCE.equals(reader.getName().getLocalPart())) {
                        if (wsdlElement != null && (wsdlElementIsBinding == null || !wsdlElementIsBinding)) {
                            throw new ContributionReadException(
                                                                wsdlElement + " must use wsdl.binding when using wsa:EndpointReference");
                        }
                        wsBinding.setEndPointReference(EndPointReferenceHelper.readEndPointReference(reader));
                    } else if (Constants.OPERATION_QNAME.equals(reader.getName())) {
                        confOp = configuredOperationProcessor.read(reader);
                        if (confOp != null) {
                            ((OperationsConfigurator)wsBinding).getConfiguredOperations().add(confOp);
                        }
                    }
                }
                    break;

            }

            if (event == END_ELEMENT && BINDING_WS_QNAME.equals(reader.getName())) {
                break;
            }
        }
        return wsBinding;
    }

    protected void processEndPointReference(XMLStreamReader reader, WebServiceBinding wsBinding) {
    }

    public void write(WebServiceBinding wsBinding, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {

        // Write a <binding.ws>
        policyProcessor.writePolicyPrefixes(wsBinding, writer);
        writer.writeStartElement(Constants.SCA10_NS, BINDING_WS);
        policyProcessor.writePolicyAttributes(wsBinding, writer);

        // Write binding name
        if (wsBinding.getName() != null) {
            writer.writeAttribute(NAME, wsBinding.getName());
        }

        // Write binding URI
        if (wsBinding.getURI() != null) {
            writer.writeAttribute(URI, wsBinding.getURI());
        }

        // Write wsdlElement attribute
        if (wsBinding.getPortName() != null) {

            // Write namespace#wsdl.port(service/port)
            String wsdlElement =
                wsBinding.getServiceName().getNamespaceURI() + "#wsdl.port("
                    + wsBinding.getServiceName().getLocalPart()
                    + "/"
                    + wsBinding.getPortName()
                    + ")";
            writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

        } else if (wsBinding.getEndpointName() != null) {

            // Write namespace#wsdl.endpoint(service/endpoint)
            String wsdlElement =
                wsBinding.getServiceName().getNamespaceURI() + "#wsdl.endpoint("
                    + wsBinding.getServiceName().getLocalPart()
                    + "/"
                    + wsBinding.getEndpointName()
                    + ")";
            writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

        } else if (wsBinding.getBindingName() != null) {

            // Write namespace#wsdl.binding(binding)
            String wsdlElement =
                wsBinding.getBindingName().getNamespaceURI() + "#wsdl.binding("
                    + wsBinding.getBindingName().getLocalPart()
                    + ")";
            writer.writeAttribute(WSDL_ELEMENT, wsdlElement);

        } else if (wsBinding.getServiceName() != null) {

            // Write namespace#wsdl.service(service)
            String wsdlElement =
                wsBinding.getServiceName().getNamespaceURI() + "#wsdl.service("
                    + wsBinding.getServiceName().getLocalPart()
                    + ")";
            writer.writeAttribute(WSDL_ELEMENT, wsdlElement);
        }

        // Write location
        if (wsBinding.getLocation() != null) {
            writer.writeAttribute(WSDLI_NS, WSDL_LOCATION, wsBinding.getLocation());
        }

        if (wsBinding.getEndPointReference() != null) {
            EndPointReferenceHelper.writeEndPointReference(wsBinding.getEndPointReference(), writer);
        }

        writer.writeEndElement();
    }

    public void resolve(WebServiceBinding model, ModelResolver resolver) throws ContributionResolveException {
        WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
        wsdlDefinition.setUnresolved(true);
        wsdlDefinition.setNamespace(model.getNamespace());
        WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, wsdlDefinition);

        if (!resolved.isUnresolved()) {
            wsdlDefinition.setDefinition(resolved.getDefinition());
            wsdlDefinition.setLocation(resolved.getLocation());
            wsdlDefinition.getXmlSchemas().addAll(resolved.getXmlSchemas());
            wsdlDefinition.setUnresolved(false);
            model.setDefinition(wsdlDefinition);
            if (model.getBindingName() != null) {
                WSDLObject<Binding> binding = wsdlDefinition.getWSDLObject(Binding.class, model.getBindingName());
                if (binding != null) {
                    wsdlDefinition.setDefinition(binding.getDefinition());
                    model.setBinding(binding.getElement());
                }
            }
            if (model.getServiceName() != null) {
                WSDLObject<Service> service = wsdlDefinition.getWSDLObject(Service.class, model.getServiceName());
                if (service != null) {
                    wsdlDefinition.setDefinition(service.getDefinition());
                    model.setService(service.getElement());
                    if (model.getPortName() != null) {
                        Port port = service.getElement().getPort(model.getPortName());
                        model.setPort(port);
                        model.setBinding(port.getBinding());
                    }
                }
            }

            PortType portType = getPortType(model);
            if (portType != null) {
                WSDLInterfaceContract interfaceContract = wsdlFactory.createWSDLInterfaceContract();
                WSDLInterface wsdlInterface = null;
                try {
                    wsdlInterface = wsdlFactory.createWSDLInterface(portType, wsdlDefinition, resolver);
                } catch (InvalidInterfaceException e) {
                    logger.log(Level.WARNING, "Exception creating interface from WSDL for binding: " + model.getName(), e); 
                }
                interfaceContract.setInterface(wsdlInterface);
                model.setBindingInterfaceContract(interfaceContract);
            }
        }
        policyProcessor.resolvePolicies(model, resolver);
        OperationsConfigurator opCongigurator = (OperationsConfigurator)model;
        for (ConfiguredOperation confOp : opCongigurator.getConfiguredOperations()) {
            policyProcessor.resolvePolicies(confOp, resolver);
        }
    }

    private PortType getPortType(WebServiceBinding model) {
        PortType portType = null;
        if (model.getPort() != null) {
            portType = model.getPort().getBinding().getPortType();
        } else if (model.getEndpoint() != null) {
            portType = model.getPort().getBinding().getPortType();
        } else if (model.getBinding() != null) {
            portType = model.getBinding().getPortType();
        } else if (model.getService() != null) {
            // FIXME: How to find the compatible port?
            Map ports = model.getService().getPorts();
            if (!ports.isEmpty()) {
                Port port = (Port)ports.values().iterator().next();
                portType = port.getBinding().getPortType();
            }
        }
        return portType;
    }

    public QName getArtifactType() {
        return WebServiceConstants.BINDING_WS_QNAME;
    }

    public Class<WebServiceBinding> getModelType() {
        return WebServiceBinding.class;
    }

}
