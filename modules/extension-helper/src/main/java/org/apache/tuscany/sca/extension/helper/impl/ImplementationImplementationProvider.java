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

package org.apache.tuscany.sca.extension.helper.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.extension.helper.ImplementationActivator;
import org.apache.tuscany.sca.extension.helper.InvokerFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The ImplementationProvider createInvoker method is called before the start method
 * but the runtime isn't properly setup until the start method is called. This means
 * that Invoker's can't initialize things like the services, references and  properties
 * until start is called. This class tries to get around that by using an Invoker
 * proxy that delays creating the real Invoker till start is called.
 */
public class ImplementationImplementationProvider implements ImplementationProvider {

    ImplementationActivator implementationActivator;
    RuntimeComponent runtimeComponent;
    Implementation impl;
    Object userImpl;
    InvokerFactory factory;

    List<InvokerProxy> invokers = new ArrayList<InvokerProxy>();

    public ImplementationImplementationProvider(ImplementationActivator implementationActivator,
                                                RuntimeComponent rc,
                                                Implementation impl,
                                                Object userImpl) {
        this.implementationActivator = implementationActivator;
        this.runtimeComponent = rc;
        this.impl = impl;
        this.userImpl = userImpl;
    }

    public Invoker createInvoker(RuntimeComponentService arg0, final Operation op) {
        InvokerProxy invoker = new InvokerProxy(op);
        return invoker;
    }

    public Invoker createCallbackInvoker(Operation operation) {
        throw new RuntimeException("TODO: callbacks not yet implemented");
    }

    public void start() {
        factory = implementationActivator.createInvokerFactory(runtimeComponent, impl, userImpl);
    }

    public void stop() {
    }

    class InvokerProxy implements Invoker {
        Invoker invoker;
        Operation op;

        InvokerProxy(Operation op) {
            this.op = op;
        }

        public Message invoke(Message msg) {
            init();
            return invoker.invoke(msg);
        }

        public synchronized void init() {
            if (invoker == null) {
                invoker = factory.createInvoker(op);
            }
        }
    }
}
