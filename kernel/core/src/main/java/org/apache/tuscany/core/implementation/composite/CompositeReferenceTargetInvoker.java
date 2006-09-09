/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.injection.WireObjectFactory;
import org.apache.tuscany.core.wire.PojoTargetInvoker;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;

public class CompositeReferenceTargetInvoker extends PojoTargetInvoker {

    private InboundWire wire;
    private WireObjectFactory wireFactory;
    private WorkContext workContext;

    
    public CompositeReferenceTargetInvoker(Method operation,
            InboundWire wire,
            WireObjectFactory wireFactory, WorkContext workContext) {
        super(operation);
        this.wire = wire;
        this.wireFactory = wireFactory;
        this.workContext = workContext;
    }

    public CompositeReferenceTargetInvoker clone() throws CloneNotSupportedException {
        CompositeReferenceTargetInvoker invoker = (CompositeReferenceTargetInvoker) super.clone();
        invoker.wire = this.wire;
        invoker.wireFactory = this.wireFactory;
        invoker.workContext = this.workContext;
        return invoker;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            wire.addMapping(msg.getMessageId(), msg.getFromAddress());
            workContext.setCurrentMessageId(msg.getMessageId());
            workContext.setCurrentCorrelationId(msg.getCorrelationId());
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    protected Object getInstance() throws TargetException {
        return wireFactory.getInstance();
    }
}
