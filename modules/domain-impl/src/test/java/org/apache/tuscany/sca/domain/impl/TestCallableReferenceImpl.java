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
package org.apache.tuscany.sca.domain.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

/**
 * Test Callablereference used to test the domain when no nodes are running
 *
 * @param <B> the type of the business interface
 * @version $Rev$ $Date$
 */
public class TestCallableReferenceImpl<B> implements CallableReference<B>, Externalizable {

    protected TestCallableReferenceImpl() {
        super();
    }

    public RuntimeWire getRuntimeWire() {
        return null;
    }

    public B getProxy() throws ObjectCreationException {
        return null;
    }

    public B getService() {
        return (B) new TestNodeManagerServiceImpl();
    }

    public Class<B> getBusinessInterface() {
        return null;
    }

    public boolean isConversational() {
        return false;
    }

    public Conversation getConversation() {
        return null;
    }

    public Object getCallbackID() {
        return null;
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }


    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {

    }
}
