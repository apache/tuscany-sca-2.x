package org.apache.tuscany.sca.implementation.bpel.ode;

import java.util.concurrent.Callable;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.BpelEngineException;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MessageExchange;
import org.apache.ode.bpel.iapi.MessageExchangeContext;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;
import org.apache.ode.utils.DOMUtils;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Message Exchange Context information
 * 
 * @version $Rev: 573789 $ $Date: 2007-09-07 23:59:49 -0700 (Fri, 07 Sep 2007) $
 */
public class ODEMessageExchangeContext implements MessageExchangeContext {
    private static final Log __log = LogFactory.getLog(ODEMessageExchangeContext.class);
    
    private EmbeddedODEServer _server;

    public ODEMessageExchangeContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public void invokePartner(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        if (__log.isDebugEnabled())
            __log.debug("Invoking a partner operation: " + partnerRoleMessageExchange.getOperationName());
        
        System.out.println(">>> Invoking a partner operation: " + partnerRoleMessageExchange.getOperationName());
        
        ODEExternalService scaService = new ODEExternalService(_server);
        scaService.invoke(partnerRoleMessageExchange);
        
        
//        boolean isTwoWay = partnerRoleMessageExchange.getMessageExchangePattern() == org.apache.ode.bpel.iapi.MessageExchange.MessageExchangePattern.REQUEST_RESPONSE;
//        
//        
//        try {
//            RuntimeComponent tuscanyRuntimeComponent = _server.getTuscanyRuntimeComponent("HelloWorldService");
//            
//            RuntimeComponentReference runtimeComponentReference = (RuntimeComponentReference) tuscanyRuntimeComponent.getReferences().get(0);
//            RuntimeWire runtimeWire = runtimeComponentReference.getRuntimeWire(runtimeComponentReference.getBindings().get(0));
//
//            //convert operations
//            Operation operation = findOperation(partnerRoleMessageExchange.getOperation().getName(), runtimeComponentReference);
//            
//            
//            /* This is how a request looks like (payload is wrapped with extra info)
//            <?xml version="1.0" encoding="UTF-8"?>
//            <message>
//              <parameters>
//                <getGreetings xmlns="http://greetings">
//                  <message xmlns="http://helloworld">Luciano</message>
//                </getGreetings>
//              </parameters>
//            </message>
//            */            
//            Element msg = partnerRoleMessageExchange.getRequest().getMessage();
//            if( msg != null) {
//	            String xml = DOMUtils.domToString(msg);
//	            System.out.println(">>> " + xml);
//	            
//	            String payload = DOMUtils.domToString(getPayload(partnerRoleMessageExchange.getRequest()));
//	            System.out.println(">>> " + payload);
//	            
//	            Object[] args = new Object[] {getPayload(partnerRoleMessageExchange.getRequest())};
//
//            	Object result = null;
//            	boolean success = false;
//            	
//            	try {
//            		result = runtimeWire.invoke(operation, args);
//            		success = true;
//            	} catch(Exception e) {
//            		partnerRoleMessageExchange.replyWithFailure(MessageExchange.FailureType.OTHER, e.getMessage(), null);
//            	}
//            	
//            	//partnerRoleMessageExchange.getResponse().setMessage(null);
//            	System.out.println(">>> Result : " + DOMUtils.domToString((Element)result));
//
//            	if(!success) {
//            		return;
//            	}
//            	
//            	//process results based on type of message invocation
//            	if(isTwoWay) {
//            		//two way invocation
//					//Message response = createResponseMessage(partnerRoleMessageExchange, (Element) result);
//					//partnerRoleMessageExchange.reply(response);
//					
//					replyTwoWayInvocation(partnerRoleMessageExchange.getMessageExchangeId(), (Element) result);
//    					
//            	} else {
//            		//one way invocation
//	            	partnerRoleMessageExchange.replyOneWayOk();
//            	}
//            }
//
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
    }

    public void onAsyncReply(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {
        if (__log.isDebugEnabled())
            __log.debug("Processing an async reply from service " + myRoleMessageExchange.getServiceName());  
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

        if (parameters != null && parameters.hasChildNodes()) {
            payload = (Element)parameters.getFirstChild();
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
