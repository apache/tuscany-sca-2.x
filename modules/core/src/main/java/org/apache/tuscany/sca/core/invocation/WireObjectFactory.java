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

import org.apache.tuscany.sca.core.component.ConversationImpl;
import org.apache.tuscany.sca.factory.ObjectCreationException;
import org.apache.tuscany.sca.factory.ObjectFactory;
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
    private Class<T> interfaze;
    private RuntimeWire wire;
    private ProxyFactory proxyService;
    
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
        this.proxyService = proxyService;
        
        // look to see if the target is conversational and if so create 
        // a conversation
        EndpointReference wireTarget = wire.getTarget();
        InterfaceContract contract = wireTarget.getInterfaceContract();
        Interface contractInterface = contract.getInterface();
       
        if (contractInterface != null && contractInterface.isConversational()){
            conversation = new ConversationImpl();          
        }        
    }

    public T getInstance() throws ObjectCreationException {
        return interfaze.cast(proxyService.createProxy(interfaze, wire, conversation));
    }
    
    public Conversation getConversation() {
        return conversation;
    }

}
