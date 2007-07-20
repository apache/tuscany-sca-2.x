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

import org.apache.tuscany.sca.core.invocation.WireObjectFactory;
import org.apache.tuscany.sca.core.runtime.RuntimeWireImpl;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

/**
 * Base class for implementations of service and callback references.
 * 
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public abstract class CallableReferenceImpl<B> implements CallableReference<B> {
    private final Class<B> businessInterface;
    private final ObjectFactory<B> factory;

    protected CallableReferenceImpl(Class<B> businessInterface, ObjectFactory<B> factory) {
        this.businessInterface = businessInterface;
        this.factory = factory;
    }

    public B getService() {
        return factory.getInstance();
    }

    public Class<B> getBusinessInterface() {
        return businessInterface;
    }

    public boolean isConversational() {
        return false;
    }

    public Conversation getConversation() {
        
        Conversation conversation = ((WireObjectFactory<B>)factory).getConversation();
                
        return conversation;
    }

    public Object getCallbackID() {
        return null;
    }
}
