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

package org.apache.tuscany.sca.binding.sca.provider;

import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;


/**
 * @version $Rev$ $Date$
 */
public class SCABindingInvoker extends InterceptorAsyncImpl {
    private InvocationChain chain;
    private Mediator mediator;
    private Operation sourceOperation;
    private Operation targetOperation;
    private boolean passByValue;
    private RuntimeEndpointReference epr;
    private RuntimeEndpoint ep;

    /**
     * Construct a SCABindingInvoker that delegates to the service invocaiton chain
     */
    public SCABindingInvoker(InvocationChain chain, Operation sourceOperation, Mediator mediator, boolean passByValue, RuntimeEndpointReference epr) {
        super();
        this.chain = chain;
        this.mediator = mediator;
        this.sourceOperation = sourceOperation;
        this.targetOperation = chain.getTargetOperation();
        this.passByValue = passByValue;
        this.epr = epr;
        this.ep = (RuntimeEndpoint)epr.getTargetEndpoint();
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#getNext()
     */
    public Invoker getNext() {
        return chain.getHeadInvoker(Phase.SERVICE_POLICY);
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#setNext(org.apache.tuscany.sca.invocation.Invoker)
     */
    public void setNext(Invoker next) {
        // NOOP
    }
    
    public Message processRequest(Message msg){
        if (passByValue) {
            msg.setBody(mediator.copyInput(msg.getBody(), sourceOperation, targetOperation));
        }
        
        ep.getInvocationChains();
        if ( !ep.getCallbackEndpointReferences().isEmpty() ) {
            RuntimeEndpointReference asyncEPR = (RuntimeEndpointReference) ep.getCallbackEndpointReferences().get(0);
            // Place a link to the callback EPR into the message headers...
            msg.getHeaders().put("ASYNC_CALLBACK", asyncEPR );
        }
        
        return msg;
    }
    
    public Message processResponse(Message msg){
        if (passByValue) {
            // Note source and target operation swapped so result is in source class loader
            if (msg.isFault()) {
                msg.setFaultBody(mediator.copyFault(msg.getBody(), sourceOperation, targetOperation));
            } else {
                if (sourceOperation.getOutputType() != null) {
                    msg.setBody(mediator.copyOutput(msg.getBody(), sourceOperation, targetOperation));
                }
            }
        }
        
        return msg;
    }

}
