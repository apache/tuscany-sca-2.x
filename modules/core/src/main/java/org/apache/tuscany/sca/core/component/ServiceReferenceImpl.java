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
package org.apache.tuscany.sca.core.component;

import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.osoa.sca.Conversation;
import org.osoa.sca.ServiceReference;

/**
 * Default implementation of a ServiceReference.
 *
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public class ServiceReferenceImpl<B> extends CallableReferenceImpl<B> implements ServiceReference<B> {
    public ServiceReferenceImpl(Class<B> businessInterface, ObjectFactory<B> factory) {
        super(businessInterface, factory);
    }

    public Object getConversationID() {
        Conversation conversation = getConversation();
        Object conversationId = null;
        
        if (conversation != null){
            conversationId = getConversation().getConversationID();
        }
        return conversationId;
    }

    public void setConversationID(Object conversationId) throws IllegalStateException {
        Conversation conversation = getConversation();
        
        if (conversation != null){
            ((ConversationImpl)getConversation()).setConversationID(conversationId);
        } else {
            throw new IllegalStateException("setConversationId called when service in not conversational");
        }
    }

    public void setCallbackID(Object callbackID) {
    }

    public Object getCallback() {
        return null;
    }

    public void setCallback(Object callback) {
    }
}
