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
package org.apache.tuscany.sca.implementation.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Turns invoke into remote message fan-out
 *
 * @version $Rev$ $Date$
 */
public class NotificationComponentInvoker implements Invoker {

    private static final Message RESPONSE = new ImmutableMessage();
    private List<InvocationChain> subscriberInvocationChains;
    private Operation operation;
    private RuntimeComponent component;

    public NotificationComponentInvoker(Operation operation, RuntimeComponent component) {
        this.subscriberInvocationChains = null;
        this.operation = operation;
        this.component = component;
    }
    
    public Message invoke(Message msg) {
        if (subscriberInvocationChains == null) {
            subscriberInvocationChains = new ArrayList<InvocationChain>();
            for (ComponentReference reference : component.getReferences()) {
                if (reference.getName().indexOf("$self$") >= 0) {
                    continue;
                }
                RuntimeComponentReference rtCompRef = null;
                if (reference instanceof RuntimeComponentReference) {
                    rtCompRef = (RuntimeComponentReference)reference;
                }
                else {
                    throw new RuntimeException("Need a runtime component reference");
                }
                for(RuntimeWire wire : rtCompRef.getRuntimeWires()) {
                    // This is much less efficient now !!
                    List<InvocationChain> chains = wire.getInvocationChains();
                    InvocationChain chain = getInvocationChain(chains, operation);
                    subscriberInvocationChains.add(chain);
                }
            }
        }
        
        // REVIEW Should this be done in separate thread(s)?
        // REVIEW Should separate copies of message be used?
        Object msgBody = msg.getBody();
        
        for (InvocationChain subscriberInvocationChain : subscriberInvocationChains) {
            Invoker chainInvoker = subscriberInvocationChain.getHeadInvoker();
            msg.setBody(msgBody);
            chainInvoker.invoke(msg);
        }
        
        return RESPONSE;
    }
    
    private InvocationChain getInvocationChain(List<InvocationChain> chains, Operation operation) {
        InvocationChain chain = null;
        for (InvocationChain ch : chains) {
            if (ch.getTargetOperation().equals(operation)) {
                chain = ch;
                break;
            }
        }
        if (chain == null) {
            for (InvocationChain ch : chains) {
                if (ch.getTargetOperation().getName().equals(operation.getName())) {
                    chain = ch;
                    break;
                }
            }
            if (chain == null) {
                throw new RuntimeException("Can't find a compatible chain");
            }
        }
        return chain;
    }
}
