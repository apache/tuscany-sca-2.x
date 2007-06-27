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
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * @version $Rev$ $Date$
 */
public class ImmutableMessage implements Message {

    public <T> T getBody() {
        return null;
    }

    public String getConversationID() {
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
    }

    public void setConversationID(String arg0) {
    }

    public void setConversationSequence(ConversationSequence arg0) {
    }

    public void setCorrelationID(Object arg0) {
    }

    public <T> void setFaultBody(T arg0) {
    }

    public void setFrom(EndpointReference arg0) {
    }

    public void setMessageID(Object arg0) {
    }

    public void setTo(EndpointReference arg0) {
    }

    public void setWire(RuntimeWire arg0) {
    }


}
