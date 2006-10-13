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
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;

/**
 * 
 */
public class CompositeReferenceCallbackTargetInvoker extends AbstractCompositeReferenceTargetInvoker {

    private InboundWire inboundWire;
    private WorkContext workContext;

    public CompositeReferenceCallbackTargetInvoker(Operation operation, InboundWire inboundWire, WorkContext context) {
        super(operation);
        this.inboundWire = inboundWire;
        this.workContext = context;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            workContext.setCurrentMessageId(msg.getMessageId());
            workContext.setCurrentCorrelationId(msg.getCorrelationId());
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    public CompositeReferenceCallbackTargetInvoker clone() throws CloneNotSupportedException {
        return (CompositeReferenceCallbackTargetInvoker) super.clone();
    }

    protected AbstractOperationOutboundInvocationHandler getInvocationHandler() {
        return new OperationCallbackInvocationHandler(workContext, inboundWire);
    }
}
