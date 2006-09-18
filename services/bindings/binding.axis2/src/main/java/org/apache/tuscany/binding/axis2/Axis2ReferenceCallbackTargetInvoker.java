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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.wire.PojoTargetInvoker;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireService;

public class Axis2ReferenceCallbackTargetInvoker extends PojoTargetInvoker {
    
    private ServiceContract<?> contract;
    private InboundWire inboundWire;
    private WireService wireService;
    private WorkContext workContext;
    private Object correlationId;
    
    public Axis2ReferenceCallbackTargetInvoker(Method operation,
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

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        workContext.setCurrentMessageId(null);
        workContext.setCurrentCorrelationId(correlationId);
        return super.invokeTarget(payload);
    }
    
    public Axis2ReferenceCallbackTargetInvoker clone() throws CloneNotSupportedException {
        Axis2ReferenceCallbackTargetInvoker invoker = (Axis2ReferenceCallbackTargetInvoker) super.clone();
        invoker.contract = this.contract;
        invoker.inboundWire = this.inboundWire;
        invoker.wireService = this.wireService;
        invoker.workContext = this.workContext;
        invoker.correlationId = this.correlationId;
        return invoker;
    }
    
    public void setCorrelationId(Object correlationId) {
        this.correlationId = correlationId;
    }

    protected Object getInstance() throws TargetException {
        return wireService.createCallbackProxy(contract, inboundWire);
    }
}
