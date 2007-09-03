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
package org.apache.tuscany.sca.implementation.notification;

import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;

/**
 * @version $Rev$ $Date$
 */
public class ImmutableMessage implements Message {

    public <T> T getBody() {
        return null;
    }

    public Object getConversationID() {
        return null;
    }

    public ConversationSequence getConversationSequence() {
        return null;
    }

    public Object getCorrelationID() {
        return null;
    }

    public EndpointReference getFrom() {
        return null;
    }

    public Object getMessageID() {
        return null;
    }

    public EndpointReference getTo() {
        return null;
    }

    public RuntimeWire getWire() {
        return null;
    }

    public boolean isFault() {
        return false;
    }

    public <T> void setBody(T arg0) {
        throw new UnsupportedOperationException();
    }

    public void setConversationID(Object arg0) {
        throw new UnsupportedOperationException();
    }

    public void setConversationSequence(ConversationSequence arg0) {
        throw new UnsupportedOperationException();
    }

    public void setCorrelationID(Object arg0) {
        throw new UnsupportedOperationException();
    }

    public <T> void setFaultBody(T arg0) {
        throw new UnsupportedOperationException();
    }

    public void setFrom(EndpointReference arg0) {
        throw new UnsupportedOperationException();
    }

    public void setMessageID(Object arg0) {
        throw new UnsupportedOperationException();
    }

    public void setTo(EndpointReference arg0) {
        throw new UnsupportedOperationException();
    }

    public Operation getOperation() {
        return null;
    }

    public void setOperation(Operation op) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Message#getCallableReference()
     */
    public <B> CallableReference<B> getCallableReference() {
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Message#setCallableReference(org.osoa.sca.CallableReference)
     */
    public <B> void setCallableReference(CallableReference<B> callableReference) {
        throw new UnsupportedOperationException();
    }
}
