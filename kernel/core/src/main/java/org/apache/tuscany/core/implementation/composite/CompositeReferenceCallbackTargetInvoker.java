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

import org.apache.tuscany.core.wire.PojoTargetInvoker;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.WireService;

public class CompositeReferenceCallbackTargetInvoker extends PojoTargetInvoker {

    private ServiceContract<?> contract;
    private InboundWire inboundWire;
    private WireService wireService;
    private WorkContext workContext;

    
    public CompositeReferenceCallbackTargetInvoker(Method operation,
            ServiceContract contract,
            InboundWire inboundWire,
            WireService wireService,
            WorkContext workContext) {
        super(operation);
        this.contract = contract;
        this.inboundWire = inboundWire;
        this.wireService = wireService;
        this.workContext = workContext;
    }

    public CompositeReferenceCallbackTargetInvoker clone() throws CloneNotSupportedException {
        CompositeReferenceCallbackTargetInvoker invoker = (CompositeReferenceCallbackTargetInvoker) super.clone();
        invoker.contract = this.contract;
        invoker.inboundWire = this.inboundWire;
        invoker.wireService = this.wireService;
        invoker.workContext = this.workContext;
        return invoker;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
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

    /*
    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            workContext.setCurrentMessageId(msg.getMessageId());
            workContext.setCurrentCorrelationId(msg.getCorrelationId());
            Object resp = invokeTarget(msg.getBody());
            return (Message) resp;
        } catch (InvocationTargetException e) {
            // FIXME need to log exceptions
            e.printStackTrace();
            return null;
        } catch (Throwable e) {
            // FIXME need to log exceptions
            e.printStackTrace();
            return null;
        }
    }
    */

    protected Object getInstance() throws TargetException {
        Object instance = wireService.createCallbackProxy(contract, inboundWire);
        return instance;
    }
}
