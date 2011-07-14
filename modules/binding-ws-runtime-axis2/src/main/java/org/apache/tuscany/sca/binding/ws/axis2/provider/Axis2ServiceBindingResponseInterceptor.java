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


import java.util.Iterator;

import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.context.WSAxis2BindingContext;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Create the SOAP envelope from the response message before the reponse
 * passes back through the binding chain
 */
public class Axis2ServiceBindingResponseInterceptor implements PhasedInterceptor {

    private Invoker next; 
    
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;

    public Axis2ServiceBindingResponseInterceptor(RuntimeEndpoint endpoint) {
        this.endpoint = endpoint;
        this.wsBinding = (WebServiceBinding)endpoint.getBinding();
    }

    public Message invoke(Message msg) {
        
        Message response =  getNext().invoke(msg);
          
        // set up the response envelope here before we return back through the binding chain
        // so that this is symetrical with how the outgoing reference binding chain behaves 
        WSAxis2BindingContext bindingContext = msg.getBindingContext();
        MessageContext responseMC = bindingContext.getAxisOutMessageContext();
        
        if(!response.isFault()) {
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
                    wrapperNamespace =  wsBinding.getUserSpecifiedWSDLDefinition().getNamespace();
                }
                          
                QName operationResponseQName = new QName(wrapperNamespace,
                                                 msg.getOperation().getName() + "Response");
                OMElement operationResponseElement = factory.createOMElement(operationResponseQName);
                operationResponseElement.addChild(responseOM);
                responseOM = operationResponseElement;
            }  
            
            if (null != responseOM ) {
                responseMC.getEnvelope().getBody().addChild(responseOM);
            }
        }       
        
        return response;
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public String getPhase() {
        return Phase.SERVICE_BINDING_POLICY;
    }
    
}
