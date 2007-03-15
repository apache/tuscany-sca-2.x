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
package org.apache.tuscany.hessian.destination;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.Wire;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import org.apache.tuscany.hessian.Destination;
import org.apache.tuscany.hessian.InvocationException;
import org.apache.tuscany.hessian.TypeNotFoundException;

/**
 * Base implementation of a Destination
 *
 * @version $Rev$ $Date$
 */
public class AbstractDestination implements Destination {
    protected Map<String, ChainHolder> chains;
    private ClassLoader loader;

    protected AbstractDestination(Wire wire, ClassLoader loader) throws TypeNotFoundException {
        this.loader = loader;
        chains = new HashMap<String, ChainHolder>();
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getPhysicalInvocationChains()
            .entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey();
            String operationName = operation.getName();
            InvocationChain chain = entry.getValue();
            List<String> params = operation.getParameters();
            Class<?>[] paramTypes = new Class<?>[params.size()];
            // load the param types in the target's classloader
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                try {
                    paramTypes[i] = loader.loadClass(param);
                } catch (ClassNotFoundException e) {
                    throw new TypeNotFoundException("Operation parameter type not found", operationName, e);
                }
            }
            AbstractDestination.ChainHolder holder =
                new AbstractDestination.ChainHolder(paramTypes, chain);
            chains.put(operationName, holder);
        }
    }

    public void invoke(AbstractHessianInput in, AbstractHessianOutput out) throws InvocationException {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            // ensure serialization is done on the target classloader
            Thread.currentThread().setContextClassLoader(loader);
            in.readCall();
            String m = in.readMethod();
            ChainHolder holder = chains.get(m);
            if (holder == null) {
                out.startReply();
                out.writeFault("OperationNotFound", "The service has no method named: " + m, null);
                out.completeReply();
                return;
            }
            Class<?>[] paramType = holder.types;
            Object[] values = new Object[paramType.length];
            for (int n = 0; n < paramType.length; n++) {
                values[n] = in.readObject(paramType[n]);
            }
            in.completeCall();
            Message msg = new MessageImpl();
            InvocationChain chain = holder.chain;
            Message ret = chain.getHeadInterceptor().invoke(msg);
            out.startReply();
            Object o = ret.getBody();
            out.writeObject(o);
            out.completeReply();
        } catch (IOException e) {
            throw new InvocationException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    protected class ChainHolder {
        Class<?>[] types;
        InvocationChain chain;

        public ChainHolder(Class<?>[] types, InvocationChain chain) {
            this.types = types;
            this.chain = chain;
        }
    }
}
