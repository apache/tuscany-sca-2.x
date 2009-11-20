/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.sca.implementation.bpel.ode;

import java.util.List;
import java.util.concurrent.Callable;

import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.utils.DOMUtils;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper Class to handle invocation to Tuscany Component References
 * 
 * @version $Rev$ $Date$ 
 */
public class ODEExternalService {
    protected final Log __log = LogFactory.getLog(getClass());

    private EmbeddedODEServer _server;
    private Scheduler _sched;
	
    public ODEExternalService(EmbeddedODEServer server) {
        this._server = server;
        this._sched = _server.getScheduler();
    }
	
    public void invoke(final PartnerRoleMessageExchange partnerRoleMessageExchange) {
        boolean isTwoWay =
            partnerRoleMessageExchange.getMessageExchangePattern() == org.apache.ode.bpel.iapi.MessageExchange.MessageExchangePattern.REQUEST_RESPONSE;

        if (isTwoWay) {
            // Defer the invoke until the transaction commits.
            _sched.registerSynchronizer(new Scheduler.Synchronizer() {
                public void beforeCompletion() {

                }

                public void afterCompletion(boolean success) {
                    // If the TX is rolled back, then we don't send the request.
                    if (!success) return;

                    // The invocation must happen in a separate thread, holding on the afterCompletion
                    // blocks other operations that could have been listed there as well.
                    _server.getExecutor().submit(new Callable<Object>() {
                        public Object call() throws Exception {
                            try {
                                // do execution
                                if(! (partnerRoleMessageExchange.getChannel() instanceof TuscanyPRC)) {
                                    throw new IllegalArgumentException("Channel should be an instance of TuscanyPRC");
                                }
                                
                                TuscanyPRC channel = (TuscanyPRC) partnerRoleMessageExchange.getChannel();
                                RuntimeComponent tuscanyRuntimeComponent = _server.getTuscanyRuntimeComponent(channel.getProcessName());

                                // Fetching the reference based on the data held in the PRC / Endpoint
                                String refName = channel.getEndpoint().serviceName.getLocalPart();
                                RuntimeComponentReference runtimeComponentReference = getReferenceByName( tuscanyRuntimeComponent, refName );
                                RuntimeEndpointReference epr = getEndpointReference( runtimeComponentReference, partnerRoleMessageExchange );
                                // convert operations
                                Operation operation =
                                    findOperation(partnerRoleMessageExchange.getOperation().getName(), epr);

                                /*
                                 This is how a request looks like (payload is wrapped with extra info) 
                                   <?xml version="1.0" encoding="UTF-8"?>
                                   <message>
                                     <parameters>
                                       <getGreetings xmlns="http://greetings">
                                         <message xmlns="http://helloworld">Luciano</message>
                                       </getGreetings>
                                     </parameters>
                                   </message>
                                 */
                                Element msg = partnerRoleMessageExchange.getRequest().getMessage();
                                if (msg != null) {
    
                                    if(__log.isDebugEnabled()) {
                                        String xml = DOMUtils.domToString(msg);
                                        String payload = DOMUtils.domToString(getPayload(partnerRoleMessageExchange.getRequest()));
                                        __log.debug("Starting invocation of SCA Reference");
                                        __log.debug(">>> Original message: " + xml);
                                        __log.debug(">>> Payload: " + payload);
                                    } // end if
                                    
                                    Object[] args = new Object[] {getPayload(partnerRoleMessageExchange.getRequest())};

                                    Object result = null;
                                    boolean success = false;

                                    try {
                                        result = epr.invoke(operation, args);
                                        success = true;
                                    } catch (Exception e) {
                                    	e.printStackTrace();
                                        partnerRoleMessageExchange.replyWithFailure(MessageExchange.FailureType.OTHER,
                                                                                    e.getMessage(),
                                                                                    null);
                                    } // end try
                                    
                                    if(__log.isDebugEnabled()) {
                                        __log.debug("SCA Reference invocation finished");
                                        __log.debug(">>> Result : " + DOMUtils.domToString((Element)result));
                                    } // end if 

                                    if (!success) { return null; }

                                    // two way invocation
                                    // process results based on type of message invocation
                                    replyTwoWayInvocation(partnerRoleMessageExchange.getMessageExchangeId(), 
                                                          operation, 
                                                          (Element)result);
                                } // end if

                            } catch (Throwable t) {
                                // some error
                                String errmsg = "Error sending message (mex=" + partnerRoleMessageExchange + "): " + t.getMessage();
                                __log.error(errmsg, t);
                                /*replyWithFailure(partnerRoleMessageExchange.getMessageExchangeId(),
                                                 MessageExchange.FailureType.COMMUNICATION_ERROR,
                                                 errmsg,
                                                 null);*/
                            }
                            return null;
                        }
                    });

                }
            });
            partnerRoleMessageExchange.replyAsync();

        } else {
            /** one-way case * */
            _server.getExecutor().submit(new Callable<Object>() {
                public Object call() throws Exception {
                    // do reply
                    // operationClient.execute(false);
                    return null;
                }
            });
            partnerRoleMessageExchange.replyOneWayOk();
        }
    }
	
    /**
     * Gets a RuntimeComponentReference of a supplied RuntimeComponent by name
     * @param tuscanyRuntimeComponent - the runtime component
     * @param name - the name of the reference
     * @return - the RuntimeComponentReference with the supplied name - null if there is no reference with that name
     */
    private RuntimeComponentReference getReferenceByName( RuntimeComponent tuscanyRuntimeComponent, String name ) {
    	if( name == null ) return null;
    	for( ComponentReference reference : tuscanyRuntimeComponent.getReferences() ) {
    		if( name.equals(reference.getName()) ) return (RuntimeComponentReference)reference;
    	} // end for
    	return null;
    } // end method getReferenceByName
    
    /**
     * Get the Runtime Wire for the supplied reference
     * @param componentReference - the reference
     * @return - the RuntimeWire - null if it cannot be found
     */
    private RuntimeEndpointReference getEndpointReference( RuntimeComponentReference componentReference,
    									PartnerRoleMessageExchange mex) {
    	if( componentReference.isForCallback() ) {
    		// Where there is a callback, it is necessary to create a specialized wire, based on callback information
    		// present on the forward call
    		
    		// Get the callbackEPR for the callback using the BPEL Process ID and the Reference name
    		// - which is the same name as the service name for a callback
    		Long processID = _server.getProcessIDFromMex(mex.getMessageExchangeId());
    		org.apache.tuscany.sca.assembly.EndpointReference callbackEPR = 
    			_server.getCallbackMetadata(processID, componentReference.getName());
    		RuntimeEndpointReference wire = selectCallbackWire( callbackEPR.getTargetEndpoint(), componentReference );
    		wire = clone_bind( componentReference, callbackEPR.getCallbackEndpoint() );
    		return wire;
    	} else {
    		// No callback case...
    		//TODO - fix the x..n multiplicity case, which needs to select the correct ONE of multiple
    		// EndpointReferences here
    		return (RuntimeEndpointReference) componentReference.getEndpointReferences().get(0);
    	} // end if
    } // end method getEndpointReference
    
    private RuntimeEndpointReference selectCallbackWire(	org.apache.tuscany.sca.assembly.Endpoint endpoint,
    		                                RuntimeComponentReference componentReference) {
        // Look for callback binding with same name as service binding
        if (endpoint == null) {
            throw new RuntimeException("Destination for forward call is not available");
        }
        
        for (EndpointReference epr : componentReference.getEndpointReferences()) {
            if (epr.getBinding().getName().equals(endpoint.getBinding().getName())) {
                return (RuntimeEndpointReference) epr;
            }
        }

        // if no match, look for callback binding with same type as service binding
        for (EndpointReference epr : componentReference.getEndpointReferences()) {
            if (epr.getBinding().getType().equals(endpoint.getBinding().getType())) {
                return (RuntimeEndpointReference) epr;
            }
        }

        // no suitable callback wire was found
        return null;
    } // end method selectCallbackWire
    
    private  RuntimeEndpointReference clone_bind(RuntimeComponentReference reference,
    		org.apache.tuscany.sca.assembly.Endpoint callbackEndpoint) {

		try {
	    	// clone the callback reference ready to configure it for this callback endpoint
			RuntimeComponentReference ref = (RuntimeComponentReference)reference.clone();
			ref.getTargets().clear();
			ref.getBindings().clear();
			ref.getEndpointReferences().clear();
			
			// clone epr
			EndpointReference callbackEndpointReference = (EndpointReference)reference.getEndpointReferences().get(0).clone();
			callbackEndpointReference.setReference(ref);
			callbackEndpointReference.setTargetEndpoint(callbackEndpoint);
			callbackEndpointReference.setUnresolved(true);
			
			// The callback endpoint will be resolved when the wire chains are created
			ref.getEndpointReferences().add(callbackEndpointReference);
			return (RuntimeEndpointReference) ref.getEndpointReferences().get(0);
		} catch ( CloneNotSupportedException e ) {
			return null;
		} // end try clone_bind
		
	} // end method 
	
    /**
     * Find the SCA Reference operation
     * 
     * @param operationName
     * @param runtimeComponentReference
     * @return
     */
    private Operation findOperation(String operationName, RuntimeEndpointReference epr) {
        Operation reseultOperation = null;

        for (Operation operation : epr.getComponentTypeReferenceInterfaceContract().getInterface().getOperations()) {
            if (operationName.equalsIgnoreCase(operation.getName())) {
                reseultOperation = operation;
                break;
            }
        }
        return reseultOperation;
    }
    
    /**
     * Get payload from a given ODEMessage
     * @param odeMessage - the ODE message
     * @return the payload of the Message, as a DOM Element
     */
    private Element getPayload(Message odeMessage) {
        Element payload = null;
        
        // Get the message parts - these correspond to the message parts for the invocation
        // as defined in the WSDL for the service operation being invoked
        List<String> parts = odeMessage.getParts();
        if( parts.size() == 0 ) return null;
        
        // For the present, just deal with the ** FIRST ** part
        // TODO Deal with operations that have messages with multiple parts
        // - that will require returning an array of Elements, one for each part      
        Element part = odeMessage.getPart(parts.get(0));

        // Get the payload which is the First child 
        if (part != null && part.hasChildNodes()) {
            payload = (Element)part.getFirstChild();
        }

        return payload;
    } // end getPayload
    

    private void replyTwoWayInvocation(final String odeMexId, final Operation operation, final Element result) {
        // ODE MEX needs to be invoked in a TX.
        try {
            _server.getScheduler().execIsolatedTransaction(new Callable<Void>() {
                public Void call() throws Exception {
                    PartnerRoleMessageExchange odeMex = null;
                    try {
                        odeMex = (PartnerRoleMessageExchange)_server.getBpelServer().getEngine().getMessageExchange(odeMexId);
                        if (odeMex != null) {
                            Message response = createResponseMessage(odeMex, operation, (Element)result);
                            odeMex.reply(response);
                        }
                    } catch (Exception ex) {
                        String errmsg = "Unable to process response: " + ex.getMessage();
                        if (odeMex != null) {
                            odeMex.replyWithFailure(MessageExchange.FailureType.OTHER, errmsg, null);
                        }
                    }

                    return null;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    	
    }

    private Message createResponseMessage(PartnerRoleMessageExchange partnerRoleMessageExchange,
                                          Operation operation,
                                          Element invocationResult) {
        Document dom = DOMUtils.newDocument();

        String operationName = operation.getName();
        Part bpelOperationOutputPart =
            (Part)((WSDLInterface)operation.getInterface()).getPortType().getOperation(operationName, null, null)
                .getOutput().getMessage().getParts().values().iterator().next();

        Element contentMessage = dom.createElement("message");
        Element contentPart = dom.createElement(bpelOperationOutputPart.getName());

        contentPart.appendChild(dom.importNode(invocationResult, true));
        contentMessage.appendChild(contentPart);
        dom.appendChild(contentMessage);

        if(__log.isDebugEnabled()) {
            __log.debug("Creating result message:");
            __log.debug(">>>" + DOMUtils.domToString(dom.getDocumentElement()));
        }

        QName id = partnerRoleMessageExchange.getOperation().getOutput().getMessage().getQName();
        Message response = partnerRoleMessageExchange.createMessage(id);
        response.setMessage(dom.getDocumentElement());

        return response;
    }

}
