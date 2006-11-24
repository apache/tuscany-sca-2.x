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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Map;

import org.apache.tuscany.spi.ReactivationException;
import org.apache.tuscany.spi.SCAExternalizable;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.AtomicComponent;
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
    implements WireInvocationHandler, InvocationHandler, Externalizable, SCAExternalizable {
    private transient WorkContext context;
    private transient InboundWire wire;
    private String serviceName;

    /**
     * Constructor used for deserialization only
     */
    public JDKCallbackInvocationHandler() {
    }

    public JDKCallbackInvocationHandler(InboundWire wire, WorkContext context) {
        this.context = context;
        this.wire = wire;
        this.serviceName = wire.getServiceName();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class)
            && "equals".equals(method.getName())) {
            // TODO implement
            throw new UnsupportedOperationException();
        } else if (Object.class.equals(method.getDeclaringClass())
            && "hashCode".equals(method.getName())) {
            return hashCode();
            // TODO beter hash algorithm
        }
        Object correlationId = context.getCurrentCorrelationId();
        context.setCurrentCorrelationId(null);
        LinkedList<Object> callbackRoutingChain = context.getCurrentCallbackRoutingChain();
        context.setCurrentCallbackRoutingChain(null);
        if (callbackRoutingChain == null) {
            throw new AssertionError("Missing stack of from addresses");
        }
        Object targetAddress = callbackRoutingChain.removeFirst();
        if (targetAddress == null) {
            throw new AssertionError("Popped a null from address from stack");
        }
        //TODO optimize as this is slow in local invocations
        Map<Operation<?>, OutboundInvocationChain> sourceCallbackInvocationChains =
            wire.getSourceCallbackInvocationChains(targetAddress);
        Operation operation = findOperation(method, sourceCallbackInvocationChains.keySet());
        OutboundInvocationChain chain = sourceCallbackInvocationChains.get(operation);
        TargetInvoker invoker = chain.getTargetInvoker();
        return invoke(chain, invoker, args, correlationId, callbackRoutingChain);
    }


    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(serviceName);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        serviceName = (String) in.readObject();
    }

    public void setWorkContext(WorkContext context) {
        this.context = context;
    }

    public void reactivate() throws ReactivationException {
        AtomicComponent owner = context.getCurrentAtomicComponent();
        if (owner == null) {
            throw new ReactivationException("Current atomic component not set on work context");
        }
        wire = owner.getInboundWires().get(serviceName);
    }
}
