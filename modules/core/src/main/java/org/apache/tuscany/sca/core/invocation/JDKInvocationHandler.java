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

package org.apache.tuscany.sca.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.spi.component.WorkContext;
import org.apache.tuscany.sca.spi.component.WorkContextTunnel;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {
    private Class<?> proxyInterface;
    private RuntimeWire wire;
    private WorkContext workContext;

    // the name of the source reference the wire is attached to, used during
    // deserialization
    private String referenceName;
    // if the associated wire has a callback
    private transient boolean callback;
    // if the associated wire is conversational
    private transient boolean conversational;

    public JDKInvocationHandler(MessageFactory messageFactory, Class<?> proxyInterface, RuntimeWire wire, WorkContext workContext) {
        super(messageFactory, false);
        this.proxyInterface = proxyInterface;
        this.wire = wire;
        this.workContext = workContext;
        init(proxyInterface, wire);
    }

    private void init(Class<?> interfaze, RuntimeWire wire) {
        InterfaceContract contract = wire.getSource().getInterfaceContract();
        this.referenceName = wire.getSource().getComponentReference().getName();
        this.conversational = contract.getInterface().isConversational();
        this.callback = contract.getCallbackInterface() != null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class) && "equals".equals(method.getName())) {
            Object obj = args[0];
            if (obj == null) {
                return false;
            }
            if (!Proxy.isProxyClass(obj.getClass())) {
                return false;
            }
            return equals(Proxy.getInvocationHandler(obj));
        } else if (Object.class.equals(method.getDeclaringClass()) && "hashCode".equals(method.getName())) {
            return hashCode();
        }
        InvocationChain chain = getInvocationChain(method);
        if (chain == null) {
            throw new IllegalArgumentException("No matching opeeration is found: " + method);
        }
        WorkContext workContext = WorkContextTunnel.getThreadWorkContext();

        if (conversational) {
            Object id = workContext.getIdentifier(Scope.CONVERSATION);
            if (id == null) {
                String convIdFromThread = createConversationID();
                workContext.setIdentifier(Scope.CONVERSATION, convIdFromThread);
            }
        }
        LinkedList<URI> list = null;
        if (callback) {
            // set up callback address
            list = workContext.getCallbackUris();
            if (list == null) {
                list = new LinkedList<URI>();
                list.add(URI.create(wire.getSource().getComponent().getURI() + "#"
                                    + wire.getSource().getComponentReference().getName()));
                workContext.setCallbackUris(list);
            }
        }
        // send the invocation down the wire
        Object result = invoke(chain, args, null, list, workContext);

        if (callback) {
            list = workContext.getCallbackUris();
            if (list != null) {
                // pop last address
                list.removeLast();
            }
        }
        return result;
    }

    /**
     * Determines if the given operation matches the given method
     * 
     * @return true if the operation matches, false if does not
     */
    private static boolean match(Operation operation, Method method) {
        Class<?>[] params = method.getParameterTypes();
        DataType<List<DataType>> inputType = operation.getInputType();
        List<DataType> types = inputType.getLogical();
        boolean matched = true;
        if (types.size() == params.length && method.getName().equals(operation.getName())) {
            for (int i = 0; i < params.length; i++) {
                Class<?> clazz = params[i];
                if (!clazz.equals(operation.getInputType().getLogical().get(i).getPhysical())) {
                    matched = false;
                }
            }
        } else {
            matched = false;
        }
        return matched;

    }

    private InvocationChain getInvocationChain(Method method) {
        for (InvocationChain chain : wire.getInvocationChains()) {
            Operation operation = chain.getSourceOperation();
            if (match(operation, method)) {
                return chain;
            }
        }
        return null;
    }

    /**
     * Creates a new conversational id
     * 
     * @return the conversational id
     */
    private String createConversationID() {
        return UUID.randomUUID().toString();
    }
}
