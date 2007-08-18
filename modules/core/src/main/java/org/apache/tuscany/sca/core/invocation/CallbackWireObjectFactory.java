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

import java.util.List;

import org.apache.tuscany.sca.factory.ObjectCreationException;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Returns proxy instance for a wire callback
 *
 * @version $Rev$ $Date$
 */
public class CallbackWireObjectFactory<B> extends WireObjectFactory<B> {
    private List<RuntimeWire> wires;
    private EndpointReference resolvedEndpoint;

    public CallbackWireObjectFactory(Class<B> interfaze, ProxyFactory proxyService, List<RuntimeWire> wires) {
        super(interfaze, null, proxyService);
        this.interfaze = interfaze;
        this.proxyFactory = proxyService;
        this.wires = wires;
    }

    public void resolveTarget() {
        Message msgContext = ThreadMessageContext.getMessageContext();
        wire = selectCallbackWire(msgContext, wires);
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("No callback wire found for " + msgContext.getFrom().getURI());
        }
        resolvedEndpoint = msgContext.getFrom();
    }

    public B getInstance() throws ObjectCreationException {
        if (wire != null) {
            // wire and endpoint already resolved, so return a pre-wired proxy
            return proxyFactory.createProxy(interfaze, wire, null, resolvedEndpoint);
        } else {
            // wire not yet selected, so return a proxy that resolves the target dynamically
            return proxyFactory.createCallbackProxy(interfaze, wires);
        }
     }
    public static RuntimeWire selectCallbackWire(Message msgContext, List<RuntimeWire> wires) {
        EndpointReference from = msgContext.getFrom();
        if (from == null) {
            return null;
        }

        //FIXME: need a cache for better performance.  This requires making this
        // method non-static, which means changing the signature of createCallbackProxy().

        // first choice is wire with matching desination endpoint
        for (RuntimeWire wire : wires) {
            if (from.getURI().equals(wire.getTarget().getURI())) {
                return wire;
            }
        }

        // no exact match, so find callback binding with same name as service binding
        EndpointReference to = msgContext.getTo();
        if (to == null) {
            //FIXME: need better exception
            throw new RuntimeException("Destination for forward call is not available");
        }
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding().getName().equals(to.getBinding().getName())) {
                //FIXME: need better way to represent dynamic wire
                if (wire.getTarget().getURI().equals("/")) {  // dynamic wire
                    return wire;
                }
                //FIXME: no dynamic wire, so should attempt to create a static wire 
            }
        }

        // no match so far, so find callback binding with same type as service binding
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding().getClass() == to.getBinding().getClass()) {
                //FIXME: need better way to represent dynamic wire
                if (wire.getTarget().getURI().equals("/")) {  // dynamic wire
                    return wire;
                }
                //FIXME: no dynamic wire, so should attempt to create a static wire 
            }
        }

        // no suitable callback wire was found
        return null;
    }

}
