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

package org.apache.tuscany.sca.core.runtime;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeSCAServiceBindingProvider implements ServiceBindingProvider2 {

    private RuntimeComponentService service;
    private boolean started = false;

    public RuntimeSCAServiceBindingProvider(RuntimeComponent component,
                                            RuntimeComponentService service,
                                            SCABinding binding) {
        this.service = service;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

    public Invoker createCallbackInvoker(Operation operation) {
        return new RuntimeSCABindingInvoker();
    }

    public void start() {
        if (started) {
            return;
        } else {
            started = true;
        }
        for (RuntimeWire sourceWire : service.getCallbackWires()) {
            if (sourceWire.getTarget().getBinding() instanceof SCABinding) {
                EndpointReference source = sourceWire.getSource();
                if (source != null) {
                    RuntimeComponentReference reference = (RuntimeComponentReference)source.getContract();
                    if (reference != null) { // a hard-wired callback
                        Binding refBinding = source.getBinding();
                        RuntimeWire targetWire = reference.getRuntimeWire(refBinding);
                        for (InvocationChain sourceChain : sourceWire.getCallbackInvocationChains()) {
                            InvocationChain targetChain =
                                reference.getCallbackInvocationChain(refBinding, sourceChain.getTargetOperation());
                            if (targetChain != null) {
                                ((Interceptor)sourceChain.getTailInvoker()).setNext(targetChain.getHeadInvoker());
                                sourceChain.setTargetOperation(targetChain.getSourceOperation());
                            } else {
                                throw new RuntimeException(
                                                           "Incompatible operations for source and target callback wires");
                            }
                        }
                        sourceWire.getSource().setInterfaceContract(targetWire.getTarget().getInterfaceContract());
                    }
                }
            }
        }
    }

    public void stop() {
    }

}
