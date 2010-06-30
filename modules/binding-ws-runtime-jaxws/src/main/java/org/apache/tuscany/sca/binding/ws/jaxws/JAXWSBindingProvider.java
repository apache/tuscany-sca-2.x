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

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@WebServiceProvider
@ServiceMode(Mode.MESSAGE)
public class JAXWSBindingProvider implements Provider<SOAPMessage> {
    public static final String WSA_FINAL_NAMESPACE = "http://www.w3.org/2005/08/addressing";
    public static final QName QNAME_WSA_ADDRESS = new QName(WSA_FINAL_NAMESPACE, "Address");
    public static final QName QNAME_WSA_FROM = new QName(WSA_FINAL_NAMESPACE, "From");
    public static final QName QNAME_WSA_REPLYTO = new QName(WSA_FINAL_NAMESPACE, "ReplyTo");
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS = new QName(WSA_FINAL_NAMESPACE, "ReferenceParameters");
    public static final QName QNAME_WSA_MESSAGEID = new QName(WSA_FINAL_NAMESPACE, "MessageID");

    private MessageFactory messageFactory;
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;
    private javax.xml.soap.MessageFactory soapMessageFactory;
    private SOAPFactory soapFactory;
    
    @Resource
    private WebServiceContext context;
    private RuntimeAssemblyFactory assemblyFactory;
    private WebServiceBindingFactory webServiceBindingFactory;  
    
    public JAXWSBindingProvider(){
        // to keep Axis2 JAXWS implementation happy
    }

    public JAXWSBindingProvider(RuntimeEndpoint endpoint,
                                ServletHost servletHost,
                                FactoryExtensionPoint modelFactories,
                                DataBindingExtensionPoint dataBindings, String defaultPort) {

        this.messageFactory = modelFactories.getFactory(MessageFactory.class);

        this.soapMessageFactory = modelFactories.getFactory(javax.xml.soap.MessageFactory.class);
        this.soapFactory = modelFactories.getFactory(SOAPFactory.class);
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        this.webServiceBindingFactory = (WebServiceBindingFactory)modelFactories.getFactory(WebServiceBindingFactory.class);

        // soapMessageFactory = javax.xml.soap.MessageFactory.newInstance();
        // soapFactory = SOAPFactory.newInstance();

        this.endpoint = endpoint;
        this.wsBinding = (WebServiceBinding)endpoint.getBinding();

        // A WSDL document should always be present in the binding
        if (wsBinding.getGeneratedWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + endpoint.getURI());
        }

        // Set to use the DOM data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(Node.class.getName());
        
        // Can we safely assume there is only one port because you configure
        // a binding in the following ways: 
        // 1/ default             - one port generated = host domain : host port / structural path 
        // 2/ uri="absolute addr" - one port generated = host domain : uri port  / uri path
        // 3/ uri="relative addr" - one port generated = host domain : host port / structural path / relative path
        // 4/ wsdl.binding        - one port generated = host domain : host port / structural path 
        // 5/ wsdl.port           - one port generated = host domain : port port / port path
        // 6/ wsa:Address         - one port generated = host domain : address port / address path
        // 7/ 4 + 6               - as 6

        // TODO the binding URI will currently have been calculated during build
        // however we don't give the provider a chance to get in and effect the
        // calculation (see above comment). For now just fake the addition of binding 
        // specific processing by adding a root if it's not already present
        if (!wsBinding.getURI().startsWith("http://")) {
            String serviceURI = null;
            
            // look in the port for the location URL
            List wsdlPortExtensions = wsBinding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    serviceURI = ((SOAPAddress) extension).getLocationURI();
                }
            }     
            
            if (serviceURI == null || 
                !serviceURI.startsWith("http://")){
                serviceURI = "http://localhost:" + defaultPort + wsBinding.getURI();
            }
            
            wsBinding.setURI(serviceURI);
        }
        System.out.println("Binding.ws JAXWS provider - Service URI: " + wsBinding.getURI());
    }

    public void start() {
        // TODO - do we need this?
    }

    public void stop() {
        // TODO - do we need this?
    }

    public SOAPMessage invoke(SOAPMessage request) {       
        try {
            // Assuming document-literal-wrapper style
            Node root = request.getSOAPBody().getFirstChild();
            String operationName = root.getLocalName();
            Operation operation = null;
            for (InvocationChain invocationChain : endpoint.getInvocationChains()) {
                if (operationName.equals(invocationChain.getSourceOperation().getName())) {
                    operation = invocationChain.getSourceOperation();
                    break;
                }
            }
            if (operation == null) {
                throw new SOAPException("Operation not found: " + operationName);
            }

            Message requestMsg = messageFactory.createMessage();
            Object[] body = new Object[]{root};
            requestMsg.setBody(body);
            requestMsg.setOperation(operation);
            
            SOAPHeader header = request.getSOAPHeader();
            String callbackAddress = null;
            if (header != null) {
                callbackAddress = handleCallbackAddress( header, requestMsg );
                // Retrieve other callback-related headers
                handleMessageIDHeader( header, requestMsg );
            } // end if

            // Create a from EPR to hold the details of the callback endpoint
            EndpointReference from = null;
            if (callbackAddress != null ) {
                    // Check for special (& not allowed!) WS_Addressing values
                    checkCallbackAddress( callbackAddress, request );
                    //
                from = assemblyFactory.createEndpointReference();
                Endpoint fromEndpoint = assemblyFactory.createEndpoint();
                from.setTargetEndpoint(fromEndpoint);
                from.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
                requestMsg.setFrom(from);
                Endpoint callbackEndpoint = assemblyFactory.createEndpoint();
                //
                WebServiceBinding cbBinding = webServiceBindingFactory.createWebServiceBinding();
                cbBinding.setURI(callbackAddress);
                callbackEndpoint.setBinding(cbBinding);
                //
                callbackEndpoint.setURI(callbackAddress);
                callbackEndpoint.setUnresolved(true);
                from.setCallbackEndpoint(callbackEndpoint);
            }

            Message responseMsg = endpoint.invoke(operation, requestMsg);
            
            SOAPMessage response = soapMessageFactory.createMessage();
            if (responseMsg.isFault()) {
//                ServiceRuntimeException e = responseMsg.getBody();
//                throw e;

                FaultException fe = responseMsg.getBody();
                SOAPFault fault = response.getSOAPBody().addFault(new QName(response.getSOAPBody().getNamespaceURI(), "Server"), fe.getMessage());
                Detail d = fault.addDetail();
                DetailEntry de = d.addDetailEntry(fe.getFaultName());
                SOAPElement dece = de.addChildElement("message");
                if (fe.getMessage() != null) {
                    dece.addTextNode(fe.getMessage());
                }

            } else {
                Element element = responseMsg.getBody();
                response.getSOAPBody().addChildElement(soapFactory.createElement(element));
            }
            return response;
        } catch (SOAPException e) {
            throw new ServiceRuntimeException(e);
        } 
    }
    private static String WS_REF_PARMS = "WS_REFERENCE_PARAMETERS";
    private String handleCallbackAddress( SOAPHeader header, Message msg ) {
        String callbackAddress = null;
        
        Iterator<SOAPElement> it = header.getChildElements(QNAME_WSA_FROM);
        SOAPElement from = it.hasNext() ? it.next() : null;
        if( from == null ) {
            Iterator<SOAPElement> it2 = header.getChildElements(QNAME_WSA_REPLYTO);
            from = it2.hasNext() ? it2.next() : null;
        }
        
        if (from != null) {
            Iterator<SOAPElement> it2 = header.getChildElements(QNAME_WSA_ADDRESS);
            SOAPElement callbackAddrElement = it2.hasNext() ? it2.next() : null;
            if (callbackAddrElement != null) {
                if (endpoint.getService().getInterfaceContract().getCallbackInterface() != null) {
                    callbackAddress = callbackAddrElement.getTextContent();
                }
//                OMElement refParms = from.getFirstChildWithName(QNAME_WSA_REFERENCE_PARAMETERS);
                Iterator<SOAPElement> it3 = header.getChildElements(QNAME_WSA_REFERENCE_PARAMETERS);
                SOAPElement refParms = it3.hasNext() ? it3.next() : null;
                if( refParms != null ) msg.getHeaders().put(WS_REF_PARMS, refParms);
            }
        } // end if
        
        return callbackAddress;
    } // end method handleCallbackAddress
    
    private static String WS_MESSAGE_ID = "WS_MESSAGE_ID";
    /**
     * Handle a SOAP wsa:MessageID header - place the contents into the Tuscany message for use by any callback
     * @param header - the SOAP Headers
     * @param msg - the Tuscany Message
     */
    private void handleMessageIDHeader( SOAPHeader header, Message msg ) {
        if( header == null ) return;
        Iterator<SOAPElement> it = header.getChildElements(QNAME_WSA_MESSAGEID);
        SOAPElement messageID = it.hasNext() ? it.next() : null;
        if (messageID != null) {
                String idValue = messageID.getTextContent();
                // Store the value of the message ID element into the message under "WS_MESSAGE_ID"...
                msg.getHeaders().put(WS_MESSAGE_ID, idValue);
        } // end if
    } // end method handleMessageID
    // Special WS_Addressing values
    private static String WS_ADDR_ANONYMOUS = "http://www.w3.org/2005/08/addressing/anonymous";
    private static String WS_ADDR_NONE          = "http://www.w3.org/2005/08/addressing/none";

    /**
     * Check if the received callback address has either of the special WS-Addressing forms which are outlawed by the
     * Web Service Binding specification [BWS50004]
     * @param callbackAddress - the received callback address
     * @param inMC - the Axis message context for the received forward call
     * @throws AxisFault - throws a "OnlyNonAnonymousAddressSupportedFault" if the callback address has either of the special forms
     */
    private void checkCallbackAddress( String callbackAddress, SOAPMessage request) {
        // If the address is anonymous or none, throw a SOAP fault...
        if( WS_ADDR_ANONYMOUS.equals(callbackAddress) || WS_ADDR_NONE.equals(callbackAddress) ) {
                triggerOnlyNonAnonymousAddressSupportedFault(request, "wsa:From");
        }
    } // end method checkCallbackAddress
    //      wsa:OnlyAnonymousAddressSupported

    //      wsa:OnlyNonAnonymousAddressSupported
    public void triggerOnlyNonAnonymousAddressSupportedFault(SOAPMessage request, String incorrectHeaderName){
// TODO        
//        String namespace = (String)messageContext.getProperty(AddressingConstants.WS_ADDRESSING_VERSION);
//        if (Submission.WSA_NAMESPACE.equals(namespace)) {
//            triggerAddressingFault(messageContext, Final.FAULT_HEADER_PROB_HEADER_QNAME,
//                                   AddressingConstants.WSA_DEFAULT_PREFIX + ":" +
//                                           incorrectHeaderName, Submission.FAULT_INVALID_HEADER,
//                                                                null, AddressingMessages.getMessage(
//                    "spec.submission.FAULT_INVALID_HEADER_REASON"));
//        } else {
//            triggerAddressingFault(messageContext, Final.FAULT_HEADER_PROB_HEADER_QNAME,
//                                   AddressingConstants.WSA_DEFAULT_PREFIX + ":" +
//                                           incorrectHeaderName, Final.FAULT_INVALID_HEADER,
//                                                                Final.FAULT_ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED,
//                                                                AddressingMessages.getMessage(
//                                                                        "spec.final.FAULT_INVALID_HEADER_REASON"));
//        }
    }
}
