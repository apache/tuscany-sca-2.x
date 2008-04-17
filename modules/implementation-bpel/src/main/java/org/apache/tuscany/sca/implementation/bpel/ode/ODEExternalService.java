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

import java.util.concurrent.Callable;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.utils.DOMUtils;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @version $Rev$ $Date$ 
 */
public class ODEExternalService {
    private static final Log __log = LogFactory.getLog(ODEExternalService.class);

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
                    if (!success)
                        return;

                    // The invocation must happen in a separate thread, holding
                    // on the afterCompletion
                    // blocks other operations that could have been listed there
                    // as well.
                    _server.getExecutor().submit(new Callable<Object>() {
                        public Object call() throws Exception {
                            try {
                                // do execution
                                RuntimeComponent tuscanyRuntimeComponent = _server.getTuscanyRuntimeComponent("HelloWorldService");

                                RuntimeComponentReference runtimeComponentReference =
                                    (RuntimeComponentReference)tuscanyRuntimeComponent.getReferences().get(0);
                                RuntimeWire runtimeWire =
                                    runtimeComponentReference.getRuntimeWire(runtimeComponentReference.getBindings().get(0));

                                // convert operations
                                Operation operation =
                                    findOperation(partnerRoleMessageExchange.getOperation().getName(), runtimeComponentReference);

                                /*
                                 * This is how a request looks like (payload is
                                 * wrapped with extra info) <?xml version="1.0"
                                 * encoding="UTF-8"?> <message> <parameters>
                                 * <getGreetings xmlns="http://greetings">
                                 * <message xmlns="http://helloworld">Luciano</message>
                                 * </getGreetings> </parameters> </message>
                                 */
                                Element msg = partnerRoleMessageExchange.getRequest().getMessage();
                                if (msg != null) {
                                    String xml = DOMUtils.domToString(msg);
                                    System.out.println(">>> " + xml);

                                    String payload =
                                        DOMUtils.domToString(getPayload(partnerRoleMessageExchange.getRequest()));
                                    System.out.println(">>> " + payload);

                                    Object[] args = new Object[] {getPayload(partnerRoleMessageExchange.getRequest())};

                                    Object result = null;
                                    boolean success = false;

                                    try {
                                        result = runtimeWire.invoke(operation, args);
                                        success = true;
                                    } catch (Exception e) {
                                        partnerRoleMessageExchange.replyWithFailure(MessageExchange.FailureType.OTHER,
                                                                                    e.getMessage(),
                                                                                    null);
                                    }

                                    // partnerRoleMessageExchange.getResponse().setMessage(null);
                                    System.out.println(">>> Result : " + DOMUtils.domToString((Element)result));

                                    if (!success) {
                                        return null;
                                    }

                                    // two way invocation
                                    // process results based on type of message
                                    // invocation

                                    // Message response =
                                    // createResponseMessage(partnerRoleMessageExchange,
                                    // (Element) result);
                                    // partnerRoleMessageExchange.reply(response);
                                    replyTwoWayInvocation(partnerRoleMessageExchange.getMessageExchangeId(),
                                                          (Element)result);
                                }

                            } catch (Throwable t) {
                                // some error
                                String errmsg =
                                    "Error sending message (mex=" + partnerRoleMessageExchange + "): " + t.getMessage();
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
     * Find the SCA Reference operation
     * 
     * @param operationName
     * @param runtimeComponentReference
     * @return
     */
    private Operation findOperation(String operationName, RuntimeComponentReference runtimeComponentReference) {
    	Operation reseultOperation = null;
    	
    	for(Operation operation : runtimeComponentReference.getInterfaceContract().getInterface().getOperations()) {
    		if (operationName.equalsIgnoreCase(operation.getName())) {
    			reseultOperation = operation;
    			break;
    		}
    	}
    	return reseultOperation;
    }
    
    /**
     * Get paylod from a given ODEMessage
     * @param odeMessage
     * @return
     */
    private Element getPayload(Message odeMessage) {
    	Element payload = null;
    	Element parameters = odeMessage.getPart("parameters");
    	
    	if( parameters != null && parameters.hasChildNodes()) {
    		payload = (Element) parameters.getFirstChild().getFirstChild();
    	}
    	
    	return payload;
    }
    

    private void replyTwoWayInvocation(final String odeMexId, final Element result) {
    	try {
    		_server.getScheduler().execIsolatedTransaction( new Callable<Void>() {
    			public Void call() throws Exception {
    				PartnerRoleMessageExchange odeMex = null;
    				try {
    					odeMex = (PartnerRoleMessageExchange)  _server.getBpelServer().getEngine().getMessageExchange(odeMexId);
    					if (odeMex != null) {
    						Message response = createResponseMessage(odeMex, (Element) result);
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
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    }

    private Message createResponseMessage(PartnerRoleMessageExchange partnerRoleMessageExchange, Element invocationResult) {
    	Document dom = DOMUtils.newDocument();
        
        Element contentMessage = dom.createElement("message");
        Element contentPart = dom.createElement(partnerRoleMessageExchange.getOperation().getOutput().getName());
        
        contentPart.appendChild(dom.importNode(invocationResult, true));
        contentMessage.appendChild(contentPart);
        dom.appendChild(contentMessage);
        
        System.out.println("::result message:: " + DOMUtils.domToString(dom.getDocumentElement()));

		QName id = partnerRoleMessageExchange.getOperation().getOutput().getMessage().getQName();
		Message response = partnerRoleMessageExchange.createMessage(id);
		response.setMessage(dom.getDocumentElement());
                
        return response;
    }

}
