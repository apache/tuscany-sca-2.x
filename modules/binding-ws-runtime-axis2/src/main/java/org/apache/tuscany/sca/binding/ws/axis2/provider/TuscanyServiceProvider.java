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

package org.apache.tuscany.sca.binding.ws.axis2.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.AddressingFaultsHelper;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

public class TuscanyServiceProvider {
    private static final Logger logger = Logger.getLogger(TuscanyServiceProvider.class.getName());
    
    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REPLYTO =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_REPLY_TO);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);
    public static final QName QNAME_WSA_MESSAGEID =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_MESSAGE_ID);
    
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;
    private MessageFactory messageFactory;
    private FactoryExtensionPoint modelFactories;
    private RuntimeAssemblyFactory assemblyFactory;
    private WebServiceBindingFactory webServiceBindingFactory;
    private Operation operation;
    
    public TuscanyServiceProvider(ExtensionPointRegistry extensionPoints,
                                  RuntimeEndpoint endpoint,
                                  WebServiceBinding wsBinding,
                                  Operation operation) {
        this.endpoint = endpoint;
        this.wsBinding = wsBinding;
        this.operation = operation;
        this.modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.messageFactory = modelFactories.getFactory(MessageFactory.class);
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        this.webServiceBindingFactory = (WebServiceBindingFactory)modelFactories.getFactory(WebServiceBindingFactory.class);
    }
    
    // Special WS_Addressing values
    private static String WS_ADDR_ANONYMOUS = "http://www.w3.org/2005/08/addressing/anonymous";
    private static String WS_ADDR_NONE		= "http://www.w3.org/2005/08/addressing/none";
    /**
     * Check if the received callback address has either of the special WS-Addressing forms which are outlawed by the
     * Web Service Binding specification [BWS50004]
     * @param callbackAddress - the received callback address
     * @param inMC - the Axis message context for the received forward call
     * @throws AxisFault - throws a "OnlyNonAnonymousAddressSupportedFault" if the callback address has either of the special forms
     */
    private void checkCallbackAddress( String callbackAddress, MessageContext inMC ) throws AxisFault {
    	// If the address is anonymous or none, throw a SOAP fault...
    	if( WS_ADDR_ANONYMOUS.equals(callbackAddress) || WS_ADDR_NONE.equals(callbackAddress) ) {
    		AddressingFaultsHelper.triggerOnlyNonAnonymousAddressSupportedFault(inMC, "wsa:From");
    	}
    } // end method checkCallbackAddress
    
    public OMElement invoke(OMElement requestOM, MessageContext inMC) throws InvocationTargetException, AxisFault {
        String callbackAddress = null;
        String callbackID = null;

        // create a message object and set the args as its body
        Message msg = messageFactory.createMessage();
        msg.setOperation(operation);
        msg.setBindingContext(inMC);
        
        if (wsBinding.isRpcLiteral()){               
            // remove the wrapping element containing
            // the operation name
            Iterator iter = requestOM.getChildElements();
            List<OMNode> list = new ArrayList<OMNode>();
            while(iter.hasNext()){
                OMNode node = (OMNode)iter.next();
                list.add(node);
            }
            
            Object[] args = list.toArray();
            msg.setBody(args);
            
        } else if (wsBinding.isRpcEncoded()){
            throw new ServiceRuntimeException("rpc/encoded WSDL style not supported for endpoint " + endpoint);
        } else if (wsBinding.isDocEncoded()){
            throw new ServiceRuntimeException("doc/encoded WSDL style not supported for endpoint " + endpoint);
        //} else if (wsBinding.isDocLiteralUnwrapped()){
           // throw new ServiceRuntimeException("doc/literal/unwrapped WSDL style not supported for endpoint " + endpoint);
        } else if (wsBinding.isDocLiteralWrapped() ||
                   wsBinding.isDocLiteralUnwrapped()){
            Object[] args = new Object[] {requestOM};
            msg.setBody(args);
        } else {
            throw new ServiceRuntimeException("Unrecognized WSDL style for endpoint " + endpoint);
        }        

        //FIXME: can we use the Axis2 addressing support for this?
        SOAPHeader header = inMC.getEnvelope().getHeader();
        if (header != null) {
            callbackAddress = handleCallbackAddress( header, msg );
            // Retrieve other callback-related headers
            handleMessageIDHeader( header, msg );
        } // end if

        // Create a from EPR to hold the details of the callback endpoint
        EndpointReference from = null;
        if (callbackAddress != null ) {
        	// Check for special (& not allowed!) WS_Addressing values
        	checkCallbackAddress( callbackAddress, inMC );
        	//
            from = assemblyFactory.createEndpointReference();
            Endpoint fromEndpoint = assemblyFactory.createEndpoint();
            from.setTargetEndpoint(fromEndpoint);
            from.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
            msg.setFrom(from);
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

        Message response = endpoint.invoke(msg);
        
        if(response.isFault()) {
            throw new InvocationTargetException((Throwable) response.getBody());
        }
        
        OMElement responseOM = response.getBody();
        
        if (wsBinding.isRpcLiteral()){               
            // add the response wrapping element
            OMFactory factory = OMAbstractFactory.getOMFactory();
            String wrapperNamespace = null;
            
            // the rpc style creates a wrapper with a namespace where the namespace is
            // defined on the wsdl binding operation. If no binding is provided by the 
            // user then default to the namespace of the WSDL itself. 
            if (wsBinding.getBinding() != null){
                Iterator iter = wsBinding.getBinding().getBindingOperations().iterator();
                loopend:
                while(iter.hasNext()){
                    BindingOperation bOp = (BindingOperation)iter.next();
                    if (bOp.getName().equals(msg.getOperation().getName())){
                        for (Object ext : bOp.getBindingOutput().getExtensibilityElements()){
                            if (ext instanceof javax.wsdl.extensions.soap.SOAPBody){
                                wrapperNamespace = ((javax.wsdl.extensions.soap.SOAPBody)ext).getNamespaceURI();
                                break loopend;
                            }
                        }
                    }
                }
            }
            
            if (wrapperNamespace == null){
                wrapperNamespace =  wsBinding.getWSDLDefinition().getNamespace();
            }
                      
            QName operationResponseQName = new QName(wrapperNamespace,
                                             msg.getOperation().getName() + "Response");
            OMElement operationResponseElement = factory.createOMElement(operationResponseQName);
            operationResponseElement.addChild(responseOM);
            responseOM = operationResponseElement;
        }
        
        return responseOM;
    } // end method 

    private static String WS_REF_PARMS = "WS_REFERENCE_PARAMETERS";
    private String handleCallbackAddress( SOAPHeader header, Message msg ) {
    	String callbackAddress = null;
        
    	OMElement from = header.getFirstChildWithName(QNAME_WSA_FROM);
    	if( from == null ) from = header.getFirstChildWithName(QNAME_WSA_REPLYTO);
    	
        if (from != null) {
            OMElement callbackAddrElement = from.getFirstChildWithName(QNAME_WSA_ADDRESS);
            if (callbackAddrElement != null) {
                if (endpoint.getService().getInterfaceContract().getCallbackInterface() != null) {
                    callbackAddress = callbackAddrElement.getText();
                }
                OMElement refParms = from.getFirstChildWithName(QNAME_WSA_REFERENCE_PARAMETERS);
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
        OMElement messageID = header.getFirstChildWithName(QNAME_WSA_MESSAGEID);
        if (messageID != null) {
        	String idValue = messageID.getText();
        	// Store the value of the message ID element into the message under "WS_MESSAGE_ID"...
        	msg.getHeaders().put(WS_MESSAGE_ID, idValue);
        } // end if
    } // end method handleMessageID
}
