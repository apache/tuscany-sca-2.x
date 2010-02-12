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

package org.apache.tuscany.sca.core.databinding.wire;

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Implementation of an interceptor that enforces pass-by-value semantics
 * on operation invocations by copying the operation input and output data.
 *
 * @version $Rev$ $Date$
 */
public class PassByValueInterceptor implements Interceptor {

    private Mediator mediator;
    private Operation operation;
    private Invoker nextInvoker;
    private InvocationChain chain;

    /**
     * Constructs a new PassByValueInterceptor.
     * @param dataBindings databinding extension point
     * @param operation the intercepted operation
     */
    public PassByValueInterceptor(Mediator mediator, InvocationChain chain, Operation operation) {
        this.mediator = mediator;
        this.chain = chain;
        this.operation = operation;
    }

    public Message invoke(Message msg) {
        if (chain.allowsPassByReference()) {
            return nextInvoker.invoke(msg);
        }

        msg.setBody(mediator.copyInput(msg.getBody(), operation));

        Message resultMsg = nextInvoker.invoke(msg);

        if (!resultMsg.isFault() && operation.getOutputType() != null) {
            resultMsg.setBody(mediator.copyOutput(resultMsg.getBody(), operation));
        }

        if (resultMsg.isFault()) {
            resultMsg.setFaultBody(mediator.copyFault(resultMsg.getBody(), operation));
        }
        return resultMsg;
    }

    public Invoker getNext() {
        return nextInvoker;
    }

    public void setNext(Invoker next) {
        this.nextInvoker = next;
    }

}
