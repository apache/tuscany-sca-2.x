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
package org.apache.tuscany.implementation.java.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReactivationException;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.AbstractInvocationHandler;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.NoRegisteredCallbackException;


/**
 * Responsible for dispatching to a callback through a wire.
 * <p/>
 * TODO cache target invoker
 * @Deprecated
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends AbstractInvocationHandler
    implements InvocationHandler, Externalizable, SCAExternalizable {
    /**
     * 
     */
    private static final long serialVersionUID = -3350283555825935609L;
    private transient WorkContext context;
    private transient Map<URI, Wire> wires;
    private List<String> sourceWireNames;

    /**
     * Constructor used for deserialization only
     */
    public JDKCallbackInvocationHandler() {
        sourceWireNames = new ArrayList<String>();
        wires = new HashMap<URI, Wire>();
    }

    public JDKCallbackInvocationHandler(List<Wire> wireList, WorkContext context) {
        this.context = context;
        this.wires = new HashMap<URI, Wire>();
        for (Wire wire : wireList) {
            wires.put(wire.getSourceUri(), wire);
        }
        sourceWireNames = new ArrayList<String>();
        for (URI uri : wires.keySet()) {
            sourceWireNames.add(uri.getFragment());
        }
    }

    @SuppressWarnings({"unchecked"})
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
        LinkedList<URI> callbackUris = context.getCallbackUris();
        assert callbackUris != null;
        URI targetAddress = callbackUris.getLast();
        assert targetAddress != null;
        Wire wire = wires.get(targetAddress);
        assert wire != null;
        Map<Operation, InvocationChain> chains = wire.getCallbackInvocationChains();
        Operation operation = JavaInterfaceUtil.findOperation(method, chains.keySet());
        InvocationChain chain = chains.get(operation);
        TargetInvoker invoker = chain.getTargetInvoker();
        Object correlationId = context.getCorrelationId();
        context.setCorrelationId(null);
        try {
            return invoke(chain, invoker, args, correlationId, callbackUris, context);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof NoRegisteredCallbackException) {
                throw t;
            }
            throw e;
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        int i = sourceWireNames.size() - 1;
        out.writeInt(i);
        for (String name : sourceWireNames) {
            out.writeObject(name);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int num = in.readInt();
        for (int i = 0; i <= num; i++) {
            sourceWireNames.add((String) in.readObject());
        }
    }

    public void setWorkContext(WorkContext context) {
        this.context = context;
    }

    public void reactivate() throws ReactivationException {
        AtomicComponent owner = context.getCurrentAtomicComponent();
        if (owner == null) {
            throw new ReactivationException("Current atomic component not set on work context");
        }
        for (String name : sourceWireNames) {
            // TODO JFM support multiplicity, remove get(0)
            Wire wire = owner.getWires(name).get(0);
            wires.put(wire.getSourceUri(), wire);

        }
    }
}
