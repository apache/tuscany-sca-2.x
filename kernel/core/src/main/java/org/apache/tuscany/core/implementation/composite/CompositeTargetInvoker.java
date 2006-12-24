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

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Dispatches an invocation through a composite service or reference
 *
 * @version $Rev$ $Date$
 */
public class CompositeTargetInvoker extends AbstractCompositeReferenceTargetInvoker {

    private OutboundInvocationChain chain;
    private Object fromAddress;
    private boolean contractHasCallback;

    public CompositeTargetInvoker(Operation operation,
                                  OutboundWire outboundWire) {
        assert operation != null;
        chain = outboundWire.getInvocationChains().get(operation);
        fromAddress = (outboundWire.getContainer() == null) ? null : outboundWire.getContainer().getName();
        contractHasCallback = outboundWire.getServiceContract().getCallbackClass() != null;
    }

    @Override
    public CompositeTargetInvoker clone() throws CloneNotSupportedException {
        return (CompositeTargetInvoker) super.clone();
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            TargetInvoker invoker = chain.getTargetInvoker();
            // Pushing the from address only needs to happen in the outbound (forward) direction for callbacks
            if (contractHasCallback) {
                msg.pushFromAddress(fromAddress);
            }

            return invoke(chain, invoker, msg);
        } catch (Throwable e) {
            Message faultMsg = new MessageImpl();
            faultMsg.setBodyWithFault(e);
            return faultMsg;
        }
    }
}
