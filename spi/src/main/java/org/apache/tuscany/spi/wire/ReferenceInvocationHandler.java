/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.spi.wire;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

/**
 * Receives a request from a proxy and performs an invocation on an {@link org.apache.tuscany.spi.wire.OutboundWire} via an {@link
 * OutboundInvocationChain}
 *
 * @version $Rev: 406016 $ $Date: 2006-05-12 22:45:22 -0700 (Fri, 12 May 2006) $
 */
public class ReferenceInvocationHandler implements WireInvocationHandler, InvocationHandler {

    /*
     * an association of an operation to chain holder. The holder contains an invocation chain
     * and a local clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the wire chains will be used.
     */
    private Map<Method, ChainHolder> chains;

    public ReferenceInvocationHandler(Map<Method, OutboundInvocationChain> invocationChains) {
        this.chains = new HashMap<Method, ChainHolder>(invocationChains.size());
        for (Map.Entry<Method, OutboundInvocationChain> entry : invocationChains.entrySet()) {
            this.chains.put(entry.getKey(), new ChainHolder(entry.getValue()));
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Interceptor headInterceptor = null;
        ChainHolder holder = chains.get(method);
        if (holder == null) {
            TargetException e = new TargetException("Operation not configured");
            e.setIdentifier(method.getName());
            throw e;
        }
        OutboundInvocationChain chain = holder.chain;
        if (chain != null) {
            headInterceptor = chain.getHeadInterceptor();
        }

        TargetInvoker invoker;

        if (holder.cachedInvoker == null) {
            assert chain != null;
            if (chain.getTargetInvoker() == null) {
                TargetException e = new TargetException("No target invoker configured for operation");
                e.setIdentifier(chain.getMethod().getName());
                throw e;
            }
            if (chain.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.cachedInvoker = (TargetInvoker) chain.getTargetInvoker().clone();
                invoker = holder.cachedInvoker;
            } else {
                invoker = chain.getTargetInvoker();
            }
        } else {
            assert chain != null;
            invoker = chain.getTargetInvoker();
        }
        if (chain.getTargetRequestChannel() == null && chain.getTargetResponseChannel() == null
                && headInterceptor == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                if (chain.getTargetInvoker() == null) {
                    throw new AssertionError("No target invoker [" + method.getName() + "]");
                }
                return chain.getTargetInvoker().invokeTarget(args);
            } catch (InvocationTargetException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            Message msg = new MessageImpl();
            msg.setTargetInvoker(invoker);
            msg.setBody(args);
            // dispatch the wire down the chain and get the response
            if (chain.getTargetRequestChannel() != null) {
                chain.getTargetRequestChannel().send(msg);
                Object body = msg.getRelatedCallbackMessage().getBody();
                if (body instanceof Throwable) {
                    throw (Throwable) body;
                }
                return body;

            } else if (headInterceptor == null) {
                throw new AssertionError("No target interceptor configured [" + method.getName() + "]");

            } else {
                Message resp = headInterceptor.invoke(msg);
                if (chain.getTargetResponseChannel() != null) {
                    chain.getTargetResponseChannel().send(resp);
                    resp = resp.getRelatedCallbackMessage();
                }
                Object body = resp.getBody();
                if (body instanceof Throwable) {
                    throw (Throwable) body;
                }
                return body;
            }
        }
    }

    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    /**
     * A holder used to associate an wire chain with a local copy of a target invoker that was previously
     * cloned from the chain master
     */
    private class ChainHolder {

        OutboundInvocationChain chain;
        TargetInvoker cachedInvoker;

        public ChainHolder(OutboundInvocationChain config) {
            this.chain = config;
        }

    }

}