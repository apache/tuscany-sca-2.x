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
package org.apache.tuscany.sca.core.invocation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;

/**
 * The default implementation of a message flowed through a wire during an invocation
 *
 * @version $Rev $Date$
 */
public class MessageImpl implements Message { 
    private List<Object> headers = new ArrayList<Object>();
    private Object body;
    private Object messageID;
    private boolean isFault;
    private Operation operation;

    private EndpointReference from;
    private Endpoint to;

    public MessageImpl() {
        // TODO - EPR - What to do by default?
        //this.from = new EndpointReferenceImpl("/");
        //this.to = new EndpointReferenceImpl("/");
        this.from = null;
        this.to = null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        return (T)body;
    }

    public <T> void setBody(T body) {
        this.isFault = false;
        this.body = body;
    }

    public Object getMessageID() {
        return messageID;
    }

    public void setMessageID(Object messageId) {
        this.messageID = messageId;
    }

    public boolean isFault() {
        return isFault;
    }

    public void setFaultBody(Object fault) {
        this.isFault = true;
        this.body = fault;
    }

    public EndpointReference getFrom() {
        return from;
    }

    public void setFrom(EndpointReference from) {
        this.from = from;
    }

    public Endpoint getTo() {
        return to;
    }

    public void setTo(Endpoint to) {
        this.to = to;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation op) {
        this.operation = op;
    }
    
    public List<Object> getHeaders() {
        return headers;
    }    

}
