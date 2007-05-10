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

package org.apache.tuscany.core.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.provider.ServiceBindingProvider;

public class RuntimeComponentServiceImpl extends ComponentServiceImpl implements RuntimeComponentService {
    private InterfaceContractMapper mapper;

    public RuntimeComponentServiceImpl(InterfaceContractMapper mapper) {
        super();
        this.mapper = mapper;
    }

    private List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    private List<RuntimeWire> callbackWires = new ArrayList<RuntimeWire>();
    private Map<Binding, ServiceBindingProvider> bindingProviders = new HashMap<Binding, ServiceBindingProvider>();

    public void addRuntimeWire(RuntimeWire wire) {
        wires.add(wire);
    }

    public List<RuntimeWire> getRuntimeWires() {
        return wires;
    }

    public RuntimeWire getRuntimeWire(Binding binding) {
        for (RuntimeWire wire : wires) {
            if (wire.getTarget().getBinding() == binding) {
                return wire;
            }
        }
        return null;
    }

    public List<RuntimeWire> getCallbackWires() {
        return callbackWires;
    }

    public void addCallbackWire(RuntimeWire callbackWire) {
        this.callbackWires.add(callbackWire);
    }

    public ServiceBindingProvider getBindingProvider(Binding binding) {
        return bindingProviders.get(binding);
    }

    public void setBindingProvider(Binding binding, ServiceBindingProvider bindingProvider) {
        bindingProviders.put(binding, bindingProvider);
    }

    public Invoker getInvoker(Binding binding, Operation operation) {
        RuntimeWire wire = getRuntimeWire(binding);
        if (wire == null) {
            return null;
        }
        for (InvocationChain chain : wire.getInvocationChains()) {
            Operation op = chain.getTargetOperation();
            if (mapper.isCompatible(operation, op, op.getInterface().isRemotable())) {
                return chain.getHeadInvoker();
            }
        }
        return null;
    }

    public Invoker getCallbackInvoker(Binding binding, Operation operation) {
        for (RuntimeWire wire : callbackWires) {
            if (wire.getTarget().getBinding() == binding) {
                for (InvocationChain chain : wire.getCallbackInvocationChains()) {
                    Operation op = chain.getSourceOperation();
                    if (mapper.isCompatible(operation, op, op.getInterface().isRemotable())) {
                        return chain.getHeadInvoker();
                    }
                }
            }
        }
        return null;
    }
}
