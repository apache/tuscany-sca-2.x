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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.spi.component.WorkContext;
import org.osoa.sca.NoRegisteredCallbackException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;
    private transient WorkContext context;
    private transient Map<URI, RuntimeWire> wires;
    private List<String> sourceWireNames;

    /**
     * Constructor used for deserialization only
     */
    public JDKCallbackInvocationHandler(MessageFactory messageFactory) {
        super(messageFactory, false);
        sourceWireNames = new ArrayList<String>();
        wires = new HashMap<URI, RuntimeWire>();
    }

    public JDKCallbackInvocationHandler(MessageFactory messageFactory, List<RuntimeWire> wireList, WorkContext context) {
        super(messageFactory, false);
        this.context = context;
        this.wires = new HashMap<URI, RuntimeWire>();
        for (RuntimeWire wire : wireList) {
            URI uri = URI.create(wire.getSource().getComponent().getURI() + "#"
                                 + wire.getSource().getComponentReference().getName());
            wires.put(uri, wire);
        }
        sourceWireNames = new ArrayList<String>();
        for (URI uri : wires.keySet()) {
            sourceWireNames.add(uri.getFragment());
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
        LinkedList<URI> callbackUris = context.getCallbackUris();
        assert callbackUris != null;
        URI targetAddress = callbackUris.getLast();
        assert targetAddress != null;
        RuntimeWire wire = wires.get(targetAddress);
        assert wire != null;
        List<InvocationChain> chains = wire.getCallbackInvocationChains();
        IdentityHashMap<Operation, InvocationChain> map = new IdentityHashMap<Operation, InvocationChain>();
        for (InvocationChain chain : chains) {
            map.put(chain.getTargetOperation(), chain);
        }
        Operation operation = JavaInterfaceUtil.findOperation(method, map.keySet());
        InvocationChain chain = map.get(operation);
        Object correlationId = context.getCorrelationId();
        context.setCorrelationId(null);
        try {
            return invoke(chain, args, correlationId, callbackUris, context);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof NoRegisteredCallbackException) {
                throw t;
            }
            throw e;
        }
    }

    public void setWorkContext(WorkContext context) {
        this.context = context;
    }
}
