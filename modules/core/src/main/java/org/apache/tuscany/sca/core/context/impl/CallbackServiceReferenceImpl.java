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
package org.apache.tuscany.sca.core.context.impl;

import java.net.URI;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

public class CallbackServiceReferenceImpl<B> extends ServiceReferenceImpl<B> {
    private RuntimeEndpointReference callbackEPR;
    private List<? extends EndpointReference> callbackEPRs;
    private Endpoint resolvedEndpoint;
    // Holds the ID of the Message that caused the creation of this CallbackServiceReference
    private String msgID;

    /** 
     * Gets the message ID associated with this callback reference
     * @return the message ID
     */
    public String getMsgID() {
		return msgID;
	}

	/*
     * Public constructor for Externalizable serialization/deserialization
     */
    public CallbackServiceReferenceImpl() {
        super();
    }

    public CallbackServiceReferenceImpl(Class<B> interfaze,
                                        List<? extends EndpointReference> callbackEPRs) {
        super(interfaze, null, getCompositeContext(callbackEPRs));
        this.callbackEPRs = callbackEPRs;
        init();
    }
    
    private static CompositeContext getCompositeContext(List<? extends EndpointReference> callbackEPRs) {
        if(!callbackEPRs.isEmpty()) {
            RuntimeEndpointReference epr = (RuntimeEndpointReference) callbackEPRs.get(0);
            return epr.getCompositeContext();
        } 
        return null;
    }

    public void init() {
        Message msgContext = ThreadMessageContext.getMessageContext();
        callbackEPR = selectCallbackEPR(msgContext);
        if (callbackEPR == null) {
            throw new ServiceRuntimeException("No callback binding found for " + msgContext.getTo().toString());
        }
        resolvedEndpoint = msgContext.getFrom().getCallbackEndpoint();
        
        // Capture the Message ID from the message which caused the creation of this CallBackServiceReference
        this.msgID = (String) msgContext.getHeaders().get(Constants.MESSAGE_ID);
    }

    @Override
    protected B createProxy() throws Exception {
        return proxyFactory.createCallbackProxy(this);
    }

    public RuntimeEndpointReference getCallbackEPR() {
        if (resolvedEndpoint == null) {
            return null;
        } else {
            return cloneAndBind(callbackEPR);
        }
    }

    public Endpoint getResolvedEndpoint() {
        return resolvedEndpoint;
    }

    private RuntimeEndpointReference selectCallbackEPR(Message msgContext) {
        // look for callback binding with same name as service binding
        Endpoint to = msgContext.getTo();
        if (to == null) {
            //FIXME: need better exception
            throw new ServiceRuntimeException("Destination for forward call is not available");
        }
        for (EndpointReference epr : callbackEPRs) {
            if (epr.getBinding().getName().equals(to.getBinding().getName())) {
                return (RuntimeEndpointReference) epr;
            }
        }

        // if no match, look for callback binding with same type as service binding
        for (EndpointReference epr : callbackEPRs) {
            if (epr.getBinding().getType().equals(to.getBinding().getType())) {
                return (RuntimeEndpointReference) epr;
            }
        }

        // no suitable callback wire was found
        return null;
    }

    private RuntimeEndpointReference cloneAndBind(RuntimeEndpointReference endpointReference) {
        if (resolvedEndpoint != null) {

            try {
                RuntimeEndpointReference epr = (RuntimeEndpointReference)endpointReference.clone();
                epr.setTargetEndpoint(resolvedEndpoint);
                
                // TUSCANY-3932
                // If it's the default binding then we're going to look the callback endpoint
                // up in the registry. Most remote protocols, which may be used as delegates 
                // or binding.sca will deal in absolution callback address and send the 
                // callback enbdpoint strutural URL separately. In this case flip the binding
                // back to the structure URL. 
                // TODO - all this creation of endpoints by the binding to represent callbacks
                //        is confusing. Code here will change if we tidy it up. 
                if (epr.getBinding().getType().equals(SCABinding.TYPE)){
                    // assume that we're going to look up the callback endpoint in the
                    // registry
                    Message msgContext = ThreadMessageContext.getMessageContext();
                    if (msgContext != null){
                        String callbackEPURI = (String)msgContext.getHeaders().get("CALLBACK_EP_URI");
                        if (callbackEPURI != null){
                            resolvedEndpoint.setURI(callbackEPURI);
                        }
                    }
                }
/*                
                // TUSCANY-3932
                // If the resolved endpoint has a binding with a absolute URI then assume
                // that URL has been passed in in the forward message and really treat it
                // as a resolved endpoint.
                Binding callbackBinding = resolvedEndpoint.getBinding();
                if ( callbackBinding != null){
                    URI callbackBindingURI = null;
                    try {
                        callbackBindingURI = new URI(callbackBinding.getURI());
                    } catch (Exception ex){
                        // ignore it, we test for null next
                    }
                    if (callbackBindingURI != null &&
                        callbackBindingURI.isAbsolute()){
                        epr.setBinding(callbackBinding);
                        // TODO - What else?
                        build(epr);
                        epr.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
                        epr.setUnresolved(false);
                    }
                }
*/
                return epr;
            } catch (CloneNotSupportedException e) {
                // will not happen
                throw new ServiceRuntimeException(e);
            }
        } else {
            return null;
        }
    }
    
    private void build(EndpointReference endpointReference) {
        BindingBuilder builder = builders.getBindingBuilder(endpointReference.getBinding().getType());
        if (builder != null) {
            builder.build(endpointReference.getComponent(),
                          endpointReference.getReference(),
                          endpointReference.getBinding(),
                          new BuilderContext(registry),
                          false);
        }
    }    

}
