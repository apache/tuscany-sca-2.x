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
package org.apache.tuscany.sca.core.invocation;

import java.util.UUID;

import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.core.context.ConversationImpl;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.Conversation;

/**
 * Uses a wire to return an object instance
 * 
 * @version $Rev$ $Date$
 */
public class WireObjectFactory<T> implements ObjectFactory<T> {
    protected Class<T> interfaze;
    protected RuntimeWire wire;
    protected ProxyFactory proxyFactory;
    protected Object callbackID;
    
    // if the wire targets a conversational service this holds the conversation state 
    private Conversation conversation = null;    

    /**
     * Constructor.
     * 
     * @param interfaze the interface to inject on the client
     * @param wire the backing wire
     * @param proxyService the wire service to create the proxy
     * @throws NoMethodForOperationException
     */
    public WireObjectFactory(Class<T> interfaze, RuntimeWire wire, ProxyFactory proxyService)
        throws NoMethodForOperationException {
        this.interfaze = interfaze;
        this.wire = wire;
        this.proxyFactory = proxyService;
        
        if (wire != null) {
            EndpointReference wireTarget = wire.getTarget();

            // look to see if the target is conversational and if so create a conversation
            InterfaceContract contract = wireTarget.getInterfaceContract();
            Interface contractInterface = contract.getInterface();

            if (contractInterface != null && contractInterface.isConversational()) {
                conversation = new ConversationImpl();
            }
            
            // if target has a callback interface, create a default callback ID
            Contract targetContract = wireTarget.getContract();
            if (targetContract != null && 
                targetContract.getInterfaceContract() != null &&
                targetContract.getInterfaceContract().getCallbackInterface() != null) {
                callbackID = createCallbackID();
            }
        }
    }

    public T getInstance() throws ObjectCreationException {
        // the callback ID is passed by value, so any subsequent calls to ServiceReference.setCallbackID()
        // won't change the callback ID used by this proxy
        return interfaze.cast(proxyFactory.createProxy(interfaze, wire, conversation, null, callbackID));
    }
    
    public Conversation getConversation() {
        return conversation;
    }

    /**
     * @return the wire
     */
    public RuntimeWire getRuntimeWire() {
        return wire;
    }

    /**
     * @return the proxyFactory
     */
    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    /**
     * @return the callback ID
     */
    public Object getCallbackID() {
        return callbackID;
    }

    /**
     * Customize the callback ID
     *
     * @param the callback ID
     */
    public void setCallbackID(Object callbackID) {
        this.callbackID = callbackID;
    }

    /**
     * Create a callback id
     * 
     * @return the callback id
     */
    private String createCallbackID() {
        return UUID.randomUUID().toString();
    }

}
