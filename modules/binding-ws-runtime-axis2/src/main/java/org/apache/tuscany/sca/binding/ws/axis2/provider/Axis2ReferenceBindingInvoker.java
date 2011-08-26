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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.EndpointReferenceHelper;
import org.apache.axis2.addressing.wsdl.WSDL11ActionHelper;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.axis2.context.WSAxis2BindingContext;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.AsyncResponseInvoker;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;


/**
 * Axis2BindingInvoker creates an Axis2 OperationClient to pass down the 
 * binding chain
 *
 * @version $Rev$ $Date$
 */
public class Axis2ReferenceBindingInvoker implements Invoker {
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM, AddressingConstants.WSA_DEFAULT_PREFIX);   
    public static final QName QNAME_WSA_TO =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_TO, AddressingConstants.WSA_DEFAULT_PREFIX);
    public static final QName QNAME_WSA_ACTION =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_ACTION, AddressingConstants.WSA_DEFAULT_PREFIX);
    public static final QName QNAME_WSA_RELATESTO =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_RELATES_TO, AddressingConstants.WSA_DEFAULT_PREFIX);   
    public static final QName QNAME_WSA_MESSAGEID =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_MESSAGE_ID, AddressingConstants.WSA_DEFAULT_PREFIX);
    
    public static final QName QNAME_CALLACK_EP_URI = new QName(org.apache.tuscany.sca.assembly.xml.Constants.SCA11_TUSCANY_NS, "CALLBACK_EP_URI");

    private RuntimeEndpointReference endpointReference;
    private ServiceClient serviceClient;
    private QName wsdlOperationName;
    private Options options;
    private SOAPFactory soapFactory;    
    private WebServiceBinding wsBinding;
    
    public Axis2ReferenceBindingInvoker(RuntimeEndpointReference endpointReference, 
                               ServiceClient serviceClient,
                               QName wsdlOperationName,
                               Options options,
                               SOAPFactory soapFactory,
                               WebServiceBinding wsBinding) {
        this.endpointReference = endpointReference;
        this.serviceClient = serviceClient;
        this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.soapFactory = soapFactory;
        this.wsBinding = wsBinding;
    }
   
    public Message invoke(Message msg) {
        try {
            final OperationClient operationClient = createOperationClient(msg);
            WSAxis2BindingContext bindingContext = new WSAxis2BindingContext();
            bindingContext.setAxisOperationClient(operationClient);
            bindingContext.setAxisOutMessageContext(operationClient.getMessageContext("Out"));
            // set in the transport invoker when the response is received
            //bindingContext.setAxisInMessageContext(operationClient.getMessageContext("In"));
            msg.setBindingContext(bindingContext);
            
            msg = endpointReference.getBindingInvocationChain().getHeadInvoker().invoke(msg);
            
            if (wsBinding.isRpcLiteral()){   
                // remove the wrapping element containing
                // the operation response name
                OMElement operationResponseElement = msg.getBody();
                if (operationResponseElement != null){
                    msg.setBody(operationResponseElement.getChildElements().next());
                }
            }
             
        } catch (AxisFault e) {
            if (e.getDetail() != null ) {
                FaultException f = new FaultException(e.getMessage(), e.getDetail(), e);
                f.setFaultName(e.getDetail().getQName());
                msg.setFaultBody(f);
            } else {
                msg.setFaultBody(e);
            }
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }       

        return msg;
    }

    @SuppressWarnings("deprecation")
    protected OperationClient createOperationClient(Message msg) throws AxisFault {
        SOAPEnvelope env = soapFactory.getDefaultEnvelope();
        Object[] args = (Object[])msg.getBody();
        if (args != null && args.length > 0) {
            
            if (wsBinding.isRpcLiteral()){               
                // create the wrapping element containing
                // the operation name
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
                            for (Object ext : bOp.getBindingInput().getExtensibilityElements()){
                                if (ext instanceof javax.wsdl.extensions.soap.SOAPBody){
                                    wrapperNamespace = ((javax.wsdl.extensions.soap.SOAPBody)ext).getNamespaceURI();
                                    break loopend;
                                }
                            }
                        }
                    }
                }
                
                if (wrapperNamespace == null){
                    wrapperNamespace =  wsBinding.getUserSpecifiedWSDLDefinition().getNamespace();
                }
                
                QName operationQName = new QName(wrapperNamespace,
                                                 msg.getOperation().getName());
                OMElement operationNameElement = factory.createOMElement(operationQName);
                
                // add the parameters as children of the operation name element
                for (Object bc : args) {
                    if (bc instanceof OMElement) {
                        operationNameElement.addChild((OMElement)bc);
                    } else {
                        throw new IllegalArgumentException( "Can't handle mixed payloads between OMElements and other types for endpoint reference " + endpointReference);
                    }
                }
                
                SOAPBody body = env.getBody();
                body.addChild(operationNameElement);
                
            } else if (wsBinding.isRpcEncoded()){
                throw new ServiceRuntimeException("rpc/encoded WSDL style not supported for endpoint reference " + endpointReference);
            } else if (wsBinding.isDocEncoded()){
                throw new ServiceRuntimeException("doc/encoded WSDL style not supported for endpoint reference " + endpointReference);
           // } else if (wsBinding.isDocLiteralUnwrapped()){
           //     throw new ServiceRuntimeException("doc/literal/unwrapped WSDL style not supported for endpoint reference " + endpointReference);
            } else if (wsBinding.isDocLiteralWrapped() ||
                       wsBinding.isDocLiteralUnwrapped()){
                // it's doc/lit
                SOAPBody body = env.getBody();
                for (Object bc : args) {
                    if (bc instanceof OMElement) {
                        body.addChild((OMElement)bc);
                    } else {
                        throw new IllegalArgumentException( "Can't handle mixed payloads between OMElements and other types for endpoint reference " + endpointReference);
                    }
                }
            } else {
                throw new ServiceRuntimeException("Unrecognized WSDL style for endpoint reference " + endpointReference);
            }
        }
        
        final MessageContext requestMC = new MessageContext();
        requestMC.setEnvelope(env);

        // Axis2 operationClients can not be shared so create a new one for each request
        final OperationClient operationClient = serviceClient.createClient(wsdlOperationName);
        operationClient.setOptions(options);
        
        Endpoint callbackEndpoint;
        AsyncResponseInvoker<String> respInvoker = (AsyncResponseInvoker<String>) msg.getHeaders().get(Constants.ASYNC_RESPONSE_INVOKER);
        if( respInvoker != null ) {
        	callbackEndpoint = createAsyncResponseEndpoint( msg, respInvoker );
        	msg.setTo(callbackEndpoint);
        } else {
        	callbackEndpoint = msg.getFrom().getCallbackEndpoint();
        } // end if 
        
        SOAPEnvelope sev = requestMC.getEnvelope();
        SOAPHeader sh = sev.getHeader();
        
        // Add WS-Addressing header for the invocation of a bidirectional service
        if (callbackEndpoint != null) {
            // Load the actual callback endpoint URI into an Axis EPR ready to form the content of the wsa:From header
            EndpointReference fromEPR = new EndpointReference(callbackEndpoint.getBinding().getURI());
            
            // pass the callback structure URI as a reference parameter
            // this allows callback endpoints to be looked up via the registry when
            // the ws binding is being used as a delegate from the sca binding
            fromEPR.addReferenceParameter(QNAME_CALLACK_EP_URI, callbackEndpoint.getURI());
           
            addWSAFromHeader( sh, fromEPR );
            addWSAActionHeader( sh );
            addWSAMessageIDHeader( sh, (String)msg.getHeaders().get("MESSAGE_ID"));
            
            requestMC.setFrom(fromEPR);
        } // end if 
        
        String toAddress = getToAddress( msg );
        requestMC.setTo( new EndpointReference(toAddress) ); 
        
        // For callback references, add wsa:To, wsa:Action and wsa:RelatesTo headers
        if( isInvocationForCallback( msg ) ) {
        	addWSAToHeader( sh, toAddress, msg );
        	addWSAActionHeader( sh );
        	addWSARelatesTo( sh, msg );
        } // end if 
        
        // Allow privileged access to read properties. Requires PropertiesPermission read in security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws AxisFault {
                    operationClient.addMessageContext(requestMC);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (AxisFault)e.getException();
        }
        return operationClient;
    } // end method createOperationClient
    
    /**
     * Create an Async Response Endpoint
     * @param msg - the Tuscany message
     * @param respInvoker - the AsyncResponseInvoker for the async response
     * @return - an Endpoint which embodies the callback address
     */
    private Endpoint createAsyncResponseEndpoint(Message msg,
			AsyncResponseInvoker<String> respInvoker) {
		String callbackAddress = respInvoker.getResponseTargetAddress();
		if( callbackAddress == null ) return null;
		
		// Get the necessary factories
		ExtensionPointRegistry registry = endpointReference.getCompositeContext().getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        RuntimeAssemblyFactory assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        WebServiceBindingFactory webServiceBindingFactory = (WebServiceBindingFactory)modelFactories.getFactory(WebServiceBindingFactory.class);
		
        // Create the endpoint
        RuntimeEndpoint callbackEndpoint = (RuntimeEndpoint)assemblyFactory.createEndpoint();
        // Add a binding
        WebServiceBinding cbBinding = webServiceBindingFactory.createWebServiceBinding();
        cbBinding.setURI(callbackAddress);
        callbackEndpoint.setBinding(cbBinding);
        // Embed the response Address URI
        callbackEndpoint.setURI(callbackAddress);
        callbackEndpoint.setUnresolved(true);
		return callbackEndpoint;
	} // end method createAsyncResponseEndpoint

	private String getToAddress( Message msg ) throws ServiceRuntimeException {
    	String address = null;
    	
        // if target endpoint was not specified when this invoker was created, 
        // use dynamically specified target endpoint passed in with the message
        if (options.getTo() == null) {
            Endpoint ep = msg.getTo();
            if (ep != null && ep.getBinding() != null) {
                address = ep.getBinding().getURI();
            } else {
                throw new ServiceRuntimeException("[BWS20025] Unable to determine destination endpoint for endpoint reference " + endpointReference);
            }
        } else {
        	address = options.getTo().getAddress(); 
        }
        
    	return address;
    } // end method getToAddress
    
    /**
     * Add wsa:From SOAP header to the message
     * @param sh - the SOAP header for the message
     * @param fromEPR - the (Axis2) EPR to include in the wsa:From
     * @throws AxisFault - if an error occurs setting the wsa:From into the header
     */
    private void addWSAFromHeader( SOAPHeader sh, EndpointReference fromEPR ) throws AxisFault {
        OMElement epr = EndpointReferenceHelper.toOM(sh.getOMFactory(),
			                                         fromEPR,
			                                         QNAME_WSA_FROM,
			                                         AddressingConstants.Final.WSA_NAMESPACE);
        sh.addChild(epr);

    } // end method addWSAFromHeader
    
    /**
     * Add wsa:MessageID SOAP header to the message
     * @param sh - the SOAP header for the message
     * @param msgID - the message ID
     * @throws AxisFault - if an error occurs setting the wsa:From into the header
     */
    private void addWSAMessageIDHeader( SOAPHeader sh, String msgID ) throws AxisFault {
    	if( msgID == null ) return;
        OMElement idHeader = sh.getOMFactory().createOMElement(QNAME_WSA_MESSAGEID);
        idHeader.setText( msgID );
        
        sh.addChild(idHeader);
    } // end method addWSAMessageIDHeader
    
    private static String WS_REF_PARMS = "WS_REFERENCE_PARAMETERS";
    /**
     * Add wsa:To SOAP header to the message - also handles ReferenceParameters, if present
     * @param sh - the SOAP header for the message
     * @param address - the address to use
     * @param msg - the Tuscany message
     */
    private void addWSAToHeader( SOAPHeader sh, String address, Message msg ) {
    	if( address == null ) return;
    	
        // Create wsa:To header which is required by ws-addressing spec
        OMElement wsaToOM = sh.getOMFactory().createOMElement(QNAME_WSA_TO);
        wsaToOM.setText( address );
        sh.addChild(wsaToOM);
        
        if( msg == null ) return;
        
        // Deal with Reference Parameters, if present - copy to the header without the wsa:ReferenceParameters wrapper
        OMElement refParms = (OMElement) msg.getHeaders().get(WS_REF_PARMS);
        if( refParms != null ) {
        	Iterator<?> children = refParms.getChildren();
        	while( children.hasNext() ) {
        		OMNode node = (OMNode) children.next();
        		sh.addChild(node);
        	}
        } // end if 
        
    } // end method addWSAActionHeader
        
    
    private void addWSAActionHeader( SOAPHeader sh ) {
        // Create wsa:Action header which is required by ws-addressing spec
        String action = options.getAction();

        if (action == null) {
            PortType portType = ((WSDLInterface)wsBinding.getBindingInterfaceContract().getInterface()).getPortType();
            Operation op = portType.getOperation(wsdlOperationName.getLocalPart(), null, null);
            action = WSDL11ActionHelper.getActionFromInputElement(wsBinding.getGeneratedWSDLDocument(), portType, op, op.getInput());
        }

        OMElement actionOM = sh.getOMFactory().createOMElement(QNAME_WSA_ACTION);
        actionOM.setText(action == null ? "" : action);
        sh.addChild(actionOM);
    } // end method addWSAActionHeader
 
    private static String WS_MESSAGE_ID = "WS_MESSAGE_ID";
    protected static String SCA_CALLBACK_REL = "http://docs.oasis-open.org/opencsa/sca-bindings/ws/callback";
    /**
     * Adds a wsa:RelatesTo SOAP header if the incoming invocation had a wsa:MessageID SOAP header present
     * - note that OASIS SCA requires that the RelationshipType attribute is set to a particular SCA value
     * @param sh - the SOAP headers
     * @param msg - the message
     */
    private void addWSARelatesTo( SOAPHeader sh, Message msg ) {
    	String idValue = (String) msg.getHeaders().get("RELATES_TO");
    	if( idValue != null ){
            OMElement relatesToOM = sh.getOMFactory().createOMElement( QNAME_WSA_RELATESTO );
            OMAttribute relType = sh.getOMFactory().createOMAttribute("RelationshipType", null, SCA_CALLBACK_REL);
            relatesToOM.addAttribute( relType );
            relatesToOM.setText( idValue );
            sh.addChild( relatesToOM );
    	}
    } // end method addWSARelatesTo
    
    /**
     * Indicates if the invocation is for the callback of a bidirectional service
     * @param msg the Message
     * @return true if the invocation is for the callback of a bidirectional service, false otherwise
     */
    private boolean isInvocationForCallback( Message msg ) {
    	org.apache.tuscany.sca.assembly.EndpointReference fromEPR = msg.getFrom();
    	if( fromEPR != null ) {
    		ComponentReference ref = fromEPR.getReference();
    		if( ref != null ) return ref.isForCallback();
    	} // end if
    	return false;
    } // end method isInvocationForCallback
    
}
