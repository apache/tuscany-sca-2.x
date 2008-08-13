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
package org.apache.tuscany.sca.core.context;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.conversation.ConversationState;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * Default implementation of a ServiceReference.
 *
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public class ServiceReferenceImpl<B> extends CallableReferenceImpl<B> implements ServiceReference<B> {
    private static final long serialVersionUID = 6763709434194361540L;

    protected transient Object callback;

    /*
     * Public constructor for Externalizable serialization/deserialization
     */
    public ServiceReferenceImpl() {
        super();
    }

    /*
     * Public constructor for use by XMLStreamReader2CallableReference
     */
    public ServiceReferenceImpl(XMLStreamReader xmlReader) throws Exception {
        super(xmlReader);
    }

    /**
     * @param businessInterface
     * @param wire
     * @param proxyFactory
     */
    public ServiceReferenceImpl(Class<B> businessInterface, RuntimeWire wire, ProxyFactory proxyFactory) {
        super(businessInterface, wire, proxyFactory);
    }

    public ServiceReferenceImpl(Class<B> businessInterface,
                                RuntimeComponent component,
                                RuntimeComponentReference reference,
                                ProxyFactory proxyFactory,
                                CompositeActivator compositeActivator) {
        super(businessInterface, component, reference, null, proxyFactory, compositeActivator);
    }

    public ServiceReferenceImpl(Class<B> businessInterface,
                                RuntimeComponent component,
                                RuntimeComponentReference reference,
                                Binding binding,
                                ProxyFactory proxyFactory,
                                CompositeActivator compositeActivator) {
        super(businessInterface, component, reference, binding, proxyFactory, compositeActivator);
    }

    public Object getConversationID() {
        return conversationID;
    }

    public void setConversationID(Object conversationID) throws IllegalStateException {
        if (conversation == null || conversation.getState() != ConversationState.ENDED) {
            this.conversationID = conversationID;
            this.conversation = null;
        } else {
            throw new IllegalStateException("Trying to set the conversationId on a service reference but the state of the conversation " 
                + conversation.getConversationID()
                + " is "
                + conversation.getState());
        }
    }

    public void setCallbackID(Object callbackID) {
        this.callbackID = callbackID;
    }

    public Object getCallback() {
        return callback;
    }

    public void setCallback(Object callback) {
        if (callback != null && !(callback instanceof CallableReference)) {
            //FIXME: need to check if callback object supports the callback interface
            // returned by reference.getInterfaceContract().getCallbackInterface()
        }
        this.callback = callback;
    }

    @Override
    protected ReferenceParameters getReferenceParameters() {
        ReferenceParameters parameters = super.getReferenceParameters();
        if (callback != null) {
            if (callback instanceof ServiceReference) {
                EndpointReference callbackRef = ((CallableReferenceImpl)callback).getEndpointReference();
                parameters.setCallbackReference(callbackRef);
            } else {
                EndpointReference callbackRef = getRuntimeWire().getSource().getCallbackEndpoint();
                parameters.setCallbackReference(callbackRef);
                parameters.setCallbackObjectID(callback);
            }
        }
        return parameters;
    }
}
