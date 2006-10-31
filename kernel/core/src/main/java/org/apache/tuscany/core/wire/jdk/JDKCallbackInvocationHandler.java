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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

import org.apache.tuscany.spi.component.WorkContext;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findOperation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.AbstractOutboundInvocationHandler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;


/**
 * Responsible for invoking on an outbound wire associated with a callback. The handler retrieves the correct outbound
 * callback wire from the work context.
 * <p/>
 * TODO cache target invoker
 *
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends AbstractOutboundInvocationHandler
    implements WireInvocationHandler, InvocationHandler {

    private WorkContext context;
    private InboundWire inboundWire;

    public JDKCallbackInvocationHandler(WorkContext context, InboundWire inboundWire) {
        this.context = context;
        this.inboundWire = inboundWire;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object correlationId = context.getCurrentCorrelationId();
        context.setCurrentCorrelationId(null);
        Stack<Object> callbackRoutingChain = context.getCurrentCallbackRoutingChain();
        context.setCurrentCallbackRoutingChain(null);
        if (callbackRoutingChain == null) {
            throw new AssertionError("Missing stack of from addresses");
        }
        Object targetAddress = callbackRoutingChain.pop();
        if (targetAddress == null) {
            throw new AssertionError("Popped a null from address from stack");
        }
        //TODO optimize as this is slow in local invocations
        Map<Operation<?>, OutboundInvocationChain> sourceCallbackInvocationChains =
            inboundWire.getSourceCallbackInvocationChains(targetAddress);
        Operation operation = findOperation(method, sourceCallbackInvocationChains.keySet());
        OutboundInvocationChain chain = sourceCallbackInvocationChains.get(operation);
        TargetInvoker invoker = chain.getTargetInvoker();
        return invoke(chain, invoker, args, correlationId, callbackRoutingChain);
    }


    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }
}
