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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

public class CallbackServiceReferenceImpl<B> extends ServiceReferenceImpl<B> {
    private RuntimeEndpointReference callbackEPR;
    private List<? extends EndpointReference> callbackEPRs;
    private Endpoint resolvedEndpoint;

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
                return epr;
            } catch (CloneNotSupportedException e) {
                // will not happen
                throw new ServiceRuntimeException(e);
            }
        } else {
            return null;
        }
    }

}
