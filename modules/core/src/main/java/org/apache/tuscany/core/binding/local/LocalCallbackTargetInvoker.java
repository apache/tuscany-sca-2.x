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
package org.apache.tuscany.core.binding.local;

import java.util.Map;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Dispatches a callback invocation to the callback instance
 *
 * @version $Rev$ $Date$
 */
public class LocalCallbackTargetInvoker extends AbstractLocalTargetInvoker {
    private Operation operation;
    private Wire wire;

    public LocalCallbackTargetInvoker(Operation operation, Wire wire) {
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
        this.wire = wire;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            return invoke(operation, msg);
        } catch (Throwable e) {
            Message faultMsg = new MessageImpl();
            faultMsg.setBodyWithFault(e);
            return faultMsg;
        }
    }

    private Message invoke(Operation operation, Message msg) throws Throwable {
        //TODO optimize as this is slow in local invocations
        Map<Operation, InvocationChain> chains = wire.getCallbackInvocationChains();
        InvocationChain chain = chains.get(operation);
        TargetInvoker invoker = chain.getTargetInvoker();
        return invoke(chain, invoker, msg);
    }

    @Override
    public LocalCallbackTargetInvoker clone() throws CloneNotSupportedException {
        return (LocalCallbackTargetInvoker) super.clone();
    }

}
