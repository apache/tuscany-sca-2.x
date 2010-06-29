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
package org.apache.tuscany.sca.binding.ws.jaxws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.OperationType;
import javax.wsdl.PortType;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Node;

/**
 * Uses JAXWS Dispatch to invoke a remote web service
 * 
 * @version $Rev$ $Date$
 */
public class JAXWSBindingInvoker implements Invoker, DataExchangeSemantics {
    private final static String SCA11_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";

    public static final String WSA_FINAL_NAMESPACE = "http://www.w3.org/2005/08/addressing";
    public static final QName QNAME_WSA_FROM = new QName(WSA_FINAL_NAMESPACE, "From", "wsa");
    public static final QName QNAME_WSA_TO = new QName(WSA_FINAL_NAMESPACE, "To", "wsa");
    public static final QName QNAME_WSA_ACTION = new QName(WSA_FINAL_NAMESPACE, "Action", "wsa");
    public static final QName QNAME_WSA_RELATESTO = new QName(WSA_FINAL_NAMESPACE, "RelatesTo", "wsa");
    private static final QName submissionWSAWNS = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing",
                                                            QNAME_WSA_ACTION.getLocalPart());
    private static final QName finalWSANS = new QName("http://www.w3.org/2005/08/addressing",
                                                      QNAME_WSA_ACTION.getLocalPart());
    private static final QName finalWSAWNS = new QName("http://www.w3.org/2006/05/addressing/wsdl",
                                                       QNAME_WSA_ACTION.getLocalPart());
    private static final QName finalWSAMNS = new QName("http://www.w3.org/2007/05/addressing/metadata",
                                                       QNAME_WSA_ACTION.getLocalPart());

    public static final String TUSCANY_PREFIX = "tuscany";
    public static final QName CALLBACK_ID_REFPARM_QN = new QName(SCA11_TUSCANY_NS, "CallbackID", TUSCANY_PREFIX);
    public static final QName CONVERSATION_ID_REFPARM_QN =
        new QName(SCA11_TUSCANY_NS, "ConversationID", TUSCANY_PREFIX);

    private Dispatch<SOAPMessage> dispatch;
    private MessageFactory messageFactory;
    private Operation operation;
    private WebServiceBinding wsBinding;
    private RuntimeEndpointReference endpointReference;

    public JAXWSBindingInvoker(Operation operation,
                               WebServiceFeature[] features,
                               MessageFactory messageFactory,
                               WebServiceBinding wsBinding,
                               RuntimeEndpointReference endpointReference) {
        this.messageFactory = messageFactory;
        this.operation = operation;
        this.wsBinding = wsBinding;
        this.endpointReference = endpointReference;
        this.dispatch = createDispatch(wsBinding);
    }

    private Dispatch<SOAPMessage> createDispatch(WebServiceBinding wsBinding) {
        URL wsdlLocation = null;
        try {
            wsdlLocation = new URL(wsBinding.getGeneratedWSDLDocument().getDocumentBaseURI());
        } catch (Exception e) {
            try {
                if (wsBinding.getWSDLDefinition().getLocation() != null) {
                    wsdlLocation = wsBinding.getWSDLDefinition().getLocation().toURL();
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        QName serviceName = null;
        QName portName = null;
        Service service = null;

        if (wsdlLocation != null) {
            serviceName = wsBinding.getServiceName();
            portName = new QName(serviceName.getNamespaceURI(), wsBinding.getPortName());
            service = Service.create(wsdlLocation, serviceName);
        } else {
            serviceName = wsBinding.getService().getQName();
            portName = new QName(serviceName.getNamespaceURI(), wsBinding.getPort().getName());
            service = Service.create(serviceName);
            service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, wsBinding.getURI());
        }

        return service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
    }

    public Message invoke(Message msg) {
        try {
            SOAPMessage resp = invokeTarget(msg);
            SOAPBody body = resp.getSOAPBody();
            if (body != null) {
                SOAPFault fault = body.getFault();
                if (fault != null) {
                    // setFault(msg, fault);
                } else {
                    // The 1st child element
                    msg.setBody(body.getChildElements().next());
                }

            }
        } catch (SOAPFaultException e) {
            setFault(msg, e);
        } catch (WebServiceException e) {
            msg.setFaultBody(e);
        } catch (SOAPException e) {
            msg.setFaultBody(e);
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }

        return msg;
    }

    private void setFault(Message msg, SOAPFaultException e) {
        SOAPFault fault = e.getFault();
        Detail detail = fault.getDetail();
        if (detail != null) {
            for (Iterator i = detail.getDetailEntries(); i.hasNext();) {
                DetailEntry entry = (DetailEntry)i.next();
                FaultException fe = new FaultException(e.getMessage(), entry.getFirstChild(), e);
                fe.setFaultName(entry.getElementQName());
                msg.setFaultBody(fe);
            }
        }
    }

    protected String getSOAPAction(String operationName) {
        Binding binding = wsBinding.getBinding();
        if (binding != null) {
            for (Object o : binding.getBindingOperations()) {
                BindingOperation bop = (BindingOperation)o;
                if (bop.getName().equalsIgnoreCase(operationName)) {
                    for (Object o2 : bop.getExtensibilityElements()) {
                        if (o2 instanceof SOAPOperation) {
                            return ((SOAPOperation)o2).getSoapActionURI();
                        } else if (o2 instanceof SOAP12Operation) {
                            return ((SOAP12Operation)o2).getSoapActionURI();
                        }
                    }
                }
            }
        }
        return null;
    }

    protected SOAPMessage invokeTarget(Message msg) throws SOAPException {
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        javax.xml.soap.SOAPEnvelope envelope = soapPart.getEnvelope();

        String action = getSOAPAction(operation.getName());

        setHeaders(envelope.getHeader(), msg, action);

        javax.xml.soap.SOAPBody body = envelope.getBody();
        Object[] args = (Object[])msg.getBody();
        // In the unit test the owner doc is null
        // so explicitly adopt the node instead
        // body.addDocument(((Node)args[0]).getOwnerDocument());
        Node msgNode = body.getOwnerDocument().importNode((Node)args[0], true);
        body.appendChild(msgNode);
        soapMessage.saveChanges();
        if (operation.isNonBlocking()) {
            dispatch.invokeOneWay(soapMessage);
            return null;
        }

        if (action != null) {
            dispatch.getRequestContext().put(Dispatch.SOAPACTION_USE_PROPERTY, true);
            dispatch.getRequestContext().put(Dispatch.SOAPACTION_URI_PROPERTY, action);
        }
        SOAPMessage response = dispatch.invoke(soapMessage);
        return response;
    }

    protected void setHeaders(SOAPHeader sh, Message msg, String action) throws SOAPException {

        Endpoint callbackEndpoint = msg.getFrom().getCallbackEndpoint();

        // add WS-Addressing header for the invocation of a bidirectional
        // service
        // FIXME: is there any way to use the Axis2 addressing support for this?
        if (callbackEndpoint != null) {
            // // Load the actual callback endpoint URI into an Axis EPR ready
            // to form the content of the wsa:From header
            // EndpointReference fromEPR = new
            // EndpointReference(callbackEndpoint.getBinding().getURI());
            //
            // addWSAFromHeader(sh, fromEPR);
            SOAPHeaderElement fromH = sh.addHeaderElement(QNAME_WSA_FROM);
            fromH.setTextContent(callbackEndpoint.getBinding().getURI());

            addWSAActionHeader(sh, action);

            // requestMC.setFrom(fromEPR);
        } // end if

        String toAddress = getToAddress(msg);
        // requestMC.setTo( new EndpointReference(toAddress) );

        if (isInvocationForCallback(msg)) {
            addWSAToHeader(sh, toAddress, msg);
            addWSAActionHeader(sh, action);
            addWSARelatesTo(sh, msg);
        } // end if

    }

    private String getToAddress(Message msg) throws ServiceRuntimeException {
        String address = null;

        // if target endpoint was not specified when this invoker was created,
        // use dynamically specified target endpoint passed in with the message

        String to = getPortLocation(wsBinding);
        if (to == null) {
            Endpoint ep = msg.getTo();
            if (ep != null && ep.getBinding() != null) {
                address = ep.getBinding().getURI();
            } else {
                throw new ServiceRuntimeException(
                                                  "[BWS20025] Unable to determine destination endpoint for endpoint reference " + endpointReference);
            }
        } else {
            address = to;
        }

        return address;
    } // end method getToAddress

    protected String getPortLocation(WebServiceBinding binding) {
        String ep = null;
        if (binding.getPort() != null) {
            List<?> wsdlPortExtensions = binding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    ep = ((SOAPAddress)extension).getLocationURI();
                    break;
                }
                if (extension instanceof SOAP12Address) {
                    SOAP12Address address = (SOAP12Address)extension;
                    ep = address.getLocationURI();
                    break;
                }
            }
        }
        if (ep == null || ep.equals("")) {
            ep = binding.getURI();
        }
        return ep;
    }

    // private void addWSAFromHeader( SOAPHeader sh, EndpointReference fromEPR )
    // throws AxisFault {
    // OMElement epr = EndpointReferenceHelper.toOM(sh.getOMFactory(),
    // fromEPR,
    // QNAME_WSA_FROM,
    // AddressingConstants.Final.WSA_NAMESPACE);
    // sh.addChild(epr);
    //
    // } // end method addWSAFromHeader

    private static String WS_REF_PARMS = "WS_REFERENCE_PARAMETERS";

    private void addWSAToHeader(SOAPHeader sh, String address, Message msg) throws SOAPException {
        // Create wsa:To header which is required by ws-addressing spec
        // OMElement wsaToOM = sh.getOMFactory().createOMElement(QNAME_WSA_TO);
        // wsaToOM.setText( address );
        // sh.addChild(wsaToOM);
        SOAPHeaderElement toH = sh.addHeaderElement(QNAME_WSA_TO);
        toH.setTextContent(address);

        // Deal with Reference Parameters, if present - copy to the header
        // without the wsa:ReferenceParameters wrapper
        // OMElement refParms = (OMElement) msg.getHeaders().get(WS_REF_PARMS);
        // Iterator ces = sh.getChildElements(new QName(WSA_FINAL_NAMESPACE,
        // WS_REF_PARMS));
        Iterator<SOAPElement> ces = sh.getChildElements();
        while (ces.hasNext()) {
            SOAPElement se = ces.next();
            if (WS_REF_PARMS.equals(se.getElementQName().getLocalPart())) {
                // if( refParms != null ) {
                Iterator<SOAPElement> children = se.getChildElements();
                while (children.hasNext()) {
                    SOAPElement node = (SOAPElement)children.next();
                    toH.addChildElement(node);
                }
                // } // end if
            }
        }

    } // end method addWSAActionHeader

    private void addWSAActionHeader(SOAPHeader sh, String action) throws SOAPException {
        // Create wsa:Action header which is required by ws-addressing spec

        if (action == null) {
            PortType portType = ((WSDLInterface)wsBinding.getBindingInterfaceContract().getInterface()).getPortType();
            javax.wsdl.Operation op = portType.getOperation(operation.getName(), null, null);
            action = getActionFromInputElement(wsBinding.getGeneratedWSDLDocument(), portType, op, op.getInput());
        }

        // OMElement actionOM =
        // sh.getOMFactory().createOMElement(QNAME_WSA_ACTION);
        // actionOM.setText(action == null ? "" : action);
        // sh.addChild(actionOM);

        SOAPHeaderElement actionH = sh.addHeaderElement(QNAME_WSA_ACTION);
        actionH.setTextContent(action == null ? "" : action);

    } // end method addWSAActionHeader

    private static String WS_MESSAGE_ID = "WS_MESSAGE_ID";
    protected static String SCA_CALLBACK_REL = "http://docs.oasis-open.org/opencsa/sca-bindings/ws/callback";

    /**
     * Adds a wsa:RelatesTo SOAP header if the incoming invocation had a
     * wsa:MessageID SOAP header present - note that OASIS SCA requires that the
     * RelationshipType attribute is set to a particular SCA value
     * 
     * @param sh - the SOAP headers
     * @param msg - the message
     * @throws SOAPException
     */
    private void addWSARelatesTo(SOAPHeader sh, Message msg) throws SOAPException {
        String idValue = (String)msg.getHeaders().get(WS_MESSAGE_ID);
        if (idValue != null) {
            SOAPHeaderElement relatesToH = sh.addHeaderElement(QNAME_WSA_RELATESTO);
            relatesToH.addAttribute(new QName(null, "RelationshipType"), SCA_CALLBACK_REL);
            relatesToH.setTextContent(idValue);
            // OMElement relatesToOM = sh.getOMFactory().createOMElement(
            // QNAME_WSA_RELATESTO );
            // OMAttribute relType =
            // sh.getOMFactory().createOMAttribute("RelationshipType", null,
            // SCA_CALLBACK_REL);
            // relatesToOM.addAttribute( relType );
            // relatesToOM.setText( idValue );
            // sh.addChild( relatesToOM );
        }
    } // end method addWSARelatesTo

    /**
     * Indicates if the invocation is for the callback of a bidirectional
     * service
     * 
     * @param msg the Message
     * @return true if the invocation is for the callback of a bidirectional
     *         service, false otherwise
     */
    private boolean isInvocationForCallback(Message msg) {
        org.apache.tuscany.sca.assembly.EndpointReference fromEPR = msg.getFrom();
        if (fromEPR != null) {
            ComponentReference ref = fromEPR.getReference();
            if (ref != null)
                return ref.isForCallback();
        } // end if
        return false;
    } // end method isInvocationForCallback

    /**
     * getActionFromInputElement
     * 
     * @param def the wsdl:definitions which contains the wsdl:portType
     * @param wsdl4jPortType the wsdl:portType which contains the wsdl:operation
     * @param op the wsdl:operation which contains the input element
     * @param input the input element to be examined to generate the wsa:Action
     * @return either the wsaw:Action from the input element or an action
     *         generated using the DefaultActionPattern
     */
    public static String getActionFromInputElement(Definition def,
                                                   PortType wsdl4jPortType,
                                                   javax.wsdl.Operation op,
                                                   Input input) {
        String result = getWSAWActionExtensionAttribute(input);
        if (result == null) {
            result = generateActionFromInputElement(def, wsdl4jPortType, op, input);
        }
        return result;
    }

    private static String getWSAWActionExtensionAttribute(AttributeExtensible ae) {
        // Search first for a wsaw:Action using the submission namespace
        Object attribute = ae.getExtensionAttribute(submissionWSAWNS);
        // Then if that did not exist one using the w3c WSAM namespace
        if (attribute == null) {
            attribute = ae.getExtensionAttribute(finalWSAMNS);
        }
        // Then if that did not exist one using the w3c WSAW namespace
        // (for backwards compat reasons)
        if (attribute == null) {
            attribute = ae.getExtensionAttribute(finalWSAWNS);
        }
        // Then finally if that did not exist, try the 2005/08 NS
        // (Included here because it's needed for Apache Muse)
        if (attribute == null) {
            attribute = ae.getExtensionAttribute(finalWSANS);
        }

        // wsdl4j may return a String, QName or a List of either
        // If it is a list, extract the first element
        if (attribute instanceof List) {
            List l = (List)attribute;
            if (l.size() > 0) {
                attribute = l.get(0);
            } else {
                attribute = null;
            }
        }

        // attribute must now be a QName or String or null
        // If it is a QName, take the LocalPart as a String
        if (attribute instanceof QName) {
            QName qn = (QName)attribute;
            attribute = qn.getLocalPart();
        }

        if ((attribute instanceof String)) {
            String result = (String)attribute;
            return result;
        } else {
            return null;
        }
    }

    /**
     * Generate the Action for an Input using the Default Action Pattern
     * <p/>
     * Pattern is defined as [target namespace][delimiter][port type
     * name][delimiter][input name]
     * 
     * @param def is required to obtain the targetNamespace
     * @param wsdl4jPortType is required to obtain the portType name
     * @param op is required to generate the input name if not explicitly
     *            specified
     * @param input is required for its name if specified
     * @return a wsa:Action value based on the Default Action Pattern and the
     *         provided objects
     */
    public static String generateActionFromInputElement(Definition def,
                                                        PortType wsdl4jPortType,
                                                        javax.wsdl.Operation op,
                                                        Input input) {
        // Get the targetNamespace of the wsdl:definitions
        String targetNamespace = def.getTargetNamespace();

        // Determine the delimiter. Per the spec: 'is ":" when the [target
        // namespace] is a URN, otherwise "/".
        // Note that for IRI schemes other than URNs which aren't path-based
        // (i.e. those that outlaw the "/"
        // character), the default action value may not conform to the rules of
        // the IRI scheme. Authors
        // are advised to specify explicit values in the WSDL in this case.'
        String delimiter = SLASH;
        if (targetNamespace.toLowerCase().startsWith(URN)) {
            delimiter = COLON;
        }

        // Get the portType name (as a string to be included in the action)
        String portTypeName = wsdl4jPortType.getQName().getLocalPart();
        // Get the name of the input element (and generate one if none
        // explicitly specified)
        String inputName = getNameFromInputElement(op, input);

        // Append the bits together
        StringBuffer sb = new StringBuffer();
        sb.append(targetNamespace);
        // Deal with the problem that the targetNamespace may or may not have a
        // trailing delimiter
        if (!targetNamespace.endsWith(delimiter)) {
            sb.append(delimiter);
        }
        sb.append(portTypeName);
        sb.append(delimiter);
        sb.append(inputName);

        // Resolve the action from the StringBuffer
        String result = sb.toString();

        return result;
    }

    /**
     * Get the name of the specified Input element using the rules defined in
     * WSDL 1.1 Section 2.4.5 http://www.w3.org/TR/wsdl#_names
     */
    private static String getNameFromInputElement(javax.wsdl.Operation op, Input input) {
        // Get the name from the input element if specified.
        String result = input.getName();

        // If not we'll have to generate it.
        if (result == null) {
            // If Request-Response or Solicit-Response do something special per
            // WSDL 1.1 Section 2.4.5
            OperationType operationType = op.getStyle();
            if (null != operationType) {
                if (operationType.equals(OperationType.REQUEST_RESPONSE)) {
                    result = op.getName() + REQUEST;
                } else if (operationType.equals(OperationType.SOLICIT_RESPONSE)) {
                    result = op.getName() + RESPONSE;
                }
            }
            // If the OperationType was not available for some reason, assume
            // on-way or notification
            if (result == null) {
                result = op.getName();
            }
        }
        return result;
    }

    private static final String URN = "urn";
    private static final String SLASH = "/";
    private static final String COLON = ":";
    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";

    public boolean allowsPassByReference() {
        return true;
    }
}
