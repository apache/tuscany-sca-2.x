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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.core.invocation.CallbackHandler;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Represent the reference to the callback service. This class basically wraps a Tuscany EndpointReference. 
 * The callback EPR is selected based on the are 3 basic scenarios we are trying to cater for
 * 
 * A/ <component name="MyComponent">
 *        <service name="MyService>
 *            blank OR <binding.sca/> 
 *        </service>
 *    </component>
 *    
 * B/ <component name="MyComponent">
 *        <service name="MyService>
 *            <binding.someremotebinding/> 
 *        </service>
 *    </component>
 *    
 * C/ <component name="MyComponent">
 *        <service name="MyService>
 *            some binding
 *            <callback>
 *                <binding.someremotebinding/> 
 *            </callback>
 *        </service>
 *    </component>    
 *    
 *  A - the callback binding will default to binding.sca and the expectation is that 
 *      the callback endpoint will be established by looking it up in the registry
 *      hence the forward call must contain the SCA target name referring to the 
 *      callback service
 *      
 *  B - the callback binding defaults to be the forward binding taking all of its
 *      configuration. The callback target URI is taken from the forward message and 
 *      put into the callback binding URI
 *      
 *  C - the callback binding is as specified by the user. If the user has not specified
 *      a binding URI then the URI from the forward message will be placed in the 
 *      callback binding URI. This may or may not lead to happiness depending on whether
 *      the forward and callback bindings are compatible
 *      
 *  The callback proxy, and this class, is instantiated whenever a new callback proxy is 
 *  required as follows:
 *  
 *    If the service component implementation is STATELESS then each incoming message  
 *    creates a new service instance and hence a new set of callback proxies
 *    
 *    If the service component implementation is COMPOSITE then only a single instance
 *    of the component implementation will exist and the callback proxy will be retrieved
 *    via the RequestContext. 
 *   
 *  Following the Tuscany runtime model for normal references we don't cache callback 
 *  proxies across component implementation instances. Hence there will be one
 *  instance of this class for each callback proxy, however created, and the class
 *  will refer to a single callback service. To put it another way, messages from 
 *  multiple clients (presenting different callback services) will be called back to
 *  via different callback proxies and hence a single instance of this class will 
 *  not be required to handle more than one callback address.  
 * 
 */
public class CallbackServiceReferenceImpl<B> extends ServiceReferenceImpl<B> {
    private static final Logger logger = Logger.getLogger(CallbackServiceReferenceImpl.class.getName());
    private RuntimeEndpointReference callbackEPR;
    private List<? extends EndpointReference> callbackEPRs;
    private Endpoint resolvedEndpoint;
    // Holds the ID of the Message that caused the creation of this CallbackServiceReference
    private String msgID;
    
    // Holds the URI of the target callback service from the Message that caused the 
    // creation of this CallbackServiceReference
    private CallbackHandler callbackHandler;

	/*
     * Public constructor for Externalizable serialization/deserialization
     * TODO - we need to serialize the msgID and callbackURI
     */
    public CallbackServiceReferenceImpl() {
        super();
    }

    public CallbackServiceReferenceImpl(Class<B> interfaze,
                                        List<? extends EndpointReference> callbackEPRs) {
        super(interfaze, null, getCompositeContext(callbackEPRs));
        this.callbackEPRs = callbackEPRs;
        
        Message msgContext = ThreadMessageContext.getMessageContext();
        
        // Capture the Message ID from the message which caused the creation of this 
        // CallBackServiceReference
        this.msgID = (String) msgContext.getHeaders().get(Constants.MESSAGE_ID);
        
        // Capture the callback URI from the message which caused the creation of this 
        // CallBackServiceReference. This code is more complex that needs be for the time being
        // to cater for bindings that still use the approach of constructing a callback endpoint
        // to model the callback URI. With these changes the binding can just set a CallbackHandler
        // in the forward message to get the same effect. Some bindings don't do that hence
        // the various checks
        this.resolvedEndpoint = null;
        
        if (msgContext.getFrom() != null){
            resolvedEndpoint = msgContext.getFrom().getCallbackEndpoint();
        }
        
        if (resolvedEndpoint != null){
            if (resolvedEndpoint.getBinding() == null){
                this.callbackHandler = new CallbackHandler(resolvedEndpoint.getURI());
            } else if (resolvedEndpoint.getBinding().getType().equals(SCABinding.TYPE)){
                this.callbackHandler = new CallbackHandler(resolvedEndpoint.getURI());
            } else {
                this.callbackHandler = new CallbackHandler(resolvedEndpoint.getBinding().getURI());
            }
        } else { 
            this.callbackHandler = (CallbackHandler)msgContext.getHeaders().get(Constants.CALLBACK);
            
            if (callbackHandler == null){
                this.callbackHandler = new CallbackHandler(null);
            }
        }
        
        if (callbackHandler.getCallbackTargetURI() != null){
            logger.log(Level.FINE, "Selecting callback EPR using address from forward message: " + callbackHandler.getCallbackTargetURI());
        } else {
            logger.log(Level.FINE, "Selecting callback EPR using address but callback URI is null");
        }
        
        // Work out which callback EPR to use
        callbackEPR = selectCallbackEPR(msgContext);
        if (callbackEPR == null) {
            throw new ServiceRuntimeException("No callback binding found for " + msgContext.getTo().toString());
        }
        
        // configure the callback EPR with the callback address
        if (callbackHandler.getCallbackTargetURI() != null) {
            callbackEPR = setCallbackAddress(callbackEPR);
        }
        
        this.resolvedEndpoint = callbackEPR.getTargetEndpoint();
    }
    
    public CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }
    
    /** 
     * Gets the message ID associated with this callback reference. All calls through the proxy backed by 
     * this CallbackServiceReference will use the same msgID
     * 
     * @return the message ID
     */
    public String getMsgID() {
        return msgID;
    }
    
    
    private static CompositeContext getCompositeContext(List<? extends EndpointReference> callbackEPRs) {
        if(!callbackEPRs.isEmpty()) {
            RuntimeEndpointReference epr = (RuntimeEndpointReference) callbackEPRs.get(0);
            return epr.getCompositeContext();
        } 
        return null;
    }

    @Override
    protected B createProxy() throws Exception {
        return proxyFactory.createCallbackProxy(this);
    }

    public RuntimeEndpointReference getCallbackEPR() {
        return callbackEPR;    
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

    private RuntimeEndpointReference setCallbackAddress(RuntimeEndpointReference endpointReference) {
        try {
            
            RuntimeEndpointReference epr = endpointReference;
            
            if (callbackHandler.getCloneCallbackWire()){
                epr = (RuntimeEndpointReference)endpointReference.clone();
            } 
            
            // TUSCANY-3932
            // If it's the default binding then we're going to look the callback endpoint
            // up in the registry. Most remote protocols, which may be used as delegates 
            // for binding.sca, will expect to deal with absolute URLs so flip the 
            // callback endpoint back to force the lookup to happen
            if (epr.getBinding().getType().equals(SCABinding.TYPE)){
                // A/ create a callback endpoint to allow the
                //    callback lookup to take place
                epr.setStatus(EndpointReference.Status.WIRED_TARGET_NOT_FOUND);
               
                // if an endpoint it provided in the forward message use it or
                // if not create one
                if (resolvedEndpoint == null ){
                    RuntimeEndpoint callbackEndpoint = (RuntimeEndpoint)assemblyFactory.createEndpoint();
                    callbackEndpoint.setURI(callbackHandler.getCallbackTargetURI());
                    callbackEndpoint.setUnresolved(true);
                    epr.setTargetEndpoint(callbackEndpoint);
                } else {
                    epr.setTargetEndpoint(resolvedEndpoint);
                }
            } else {
                // B/ and C/ assume that the callback EPR is already resolved
                //           and set the binding URI if one is provided with the
                //           forward message. Some bindings may want to do other
                //           things to determine the callback URI to the 
                //           CallbackHandler will be sent in the callback message
                //           header. This is particularly true if the clone isn't
                //           called above because resetting the URI will not 
                //           be thread safe.
                epr.setStatus(EndpointReference.Status.RESOLVED_BINDING);
                
                if ( callbackHandler.getCallbackTargetURI() != null ){
                    epr.getBinding().setURI(callbackHandler.getCallbackTargetURI());
                } else {
                    // do nothing and rely on whatever the user has configured 
                    // in the SCDL
                }
            }
        
            return epr;
        } catch (CloneNotSupportedException e) {
            // will not happen
            throw new ServiceRuntimeException(e);
        }
    } 
}
