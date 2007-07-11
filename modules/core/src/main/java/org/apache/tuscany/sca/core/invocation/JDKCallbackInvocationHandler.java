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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.NoRegisteredCallbackException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;
    private transient Map<String, RuntimeWire> wires;

    /**
     * Constructor used for deserialization only
     */
    public JDKCallbackInvocationHandler(MessageFactory messageFactory) {
        super(messageFactory, false);
        wires = new HashMap<String, RuntimeWire>();
    }

    public JDKCallbackInvocationHandler(MessageFactory messageFactory, List<RuntimeWire> wireList) {
        super(messageFactory, false);
        this.wires = new HashMap<String, RuntimeWire>();
        for (RuntimeWire wire : wireList) {
            wires.put(wire.getSource().getURI(), wire);
        }
    }

    @SuppressWarnings( {"unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class) && "equals".equals(method.getName())) {
            // TODO implement
            throw new UnsupportedOperationException();
        } else if (Object.class.equals(method.getDeclaringClass()) && "hashCode".equals(method.getName())) {
            return hashCode();
            // TODO beter hash algorithm
        }
        EndpointReference from = ThreadMessageContext.getMessageContext().getFrom();
        RuntimeWire wire = null;
        if (from != null) {
            wire = wires.get(from.getURI());
        } else { // service with binding
            wire = wires.get(null);
        }
        assert wire != null;
        IdentityHashMap<Operation, InvocationChain> map = wire.getCallbackInvocationMap();
        Operation operation = JavaInterfaceUtil.findOperation(method, map.keySet());
        InvocationChain chain = map.get(operation);
        try {
            return invoke(chain, args, wire);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof NoRegisteredCallbackException) {
                throw t;
            }
            throw e;
        }
    }

}
