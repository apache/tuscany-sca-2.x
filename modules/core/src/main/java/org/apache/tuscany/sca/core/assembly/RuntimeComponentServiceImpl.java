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

package org.apache.tuscany.sca.core.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Implementation of a Component Service.
 *
 * @version $Rev$ $Date$
 */
public class RuntimeComponentServiceImpl extends ComponentServiceImpl implements RuntimeComponentService {
    private ArrayList<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    private ArrayList<RuntimeWire> callbackWires = new ArrayList<RuntimeWire>();
    private HashMap<Binding, ServiceBindingProvider> bindingProviders = new HashMap<Binding, ServiceBindingProvider>();
    private HashMap<Binding, List<PolicyProvider>> policyProviders = new HashMap<Binding, List<PolicyProvider>>();

    public RuntimeComponentServiceImpl() {
        super();
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

    public RuntimeWire getRuntimeWire(Binding binding, InterfaceContract interfaceContract) {
        RuntimeWire wire = getRuntimeWire(binding);
        if (wire == null) {
            return null;
        }
        if (interfaceContract != null && interfaceContract != wire.getSource().getInterfaceContract()) {
            try {
                // FIXME: [rfeng] We could avoid clone() using a better comparison of the two interface contracts
                wire = (RuntimeWire)wire.clone();
                wire.getSource().setInterfaceContract(interfaceContract);
                wire.rebuild();
            } catch (CloneNotSupportedException e) {
                throw new ServiceRuntimeException(e);
            }
        }

        return wire;
    }

    public List<RuntimeWire> getCallbackWires() {
        return callbackWires;
    }

    public ServiceBindingProvider getBindingProvider(Binding binding) {
        return bindingProviders.get(binding);
    }

    public void setBindingProvider(Binding binding, ServiceBindingProvider bindingProvider) {
        bindingProviders.put(binding, bindingProvider);
    }

    public Invoker getInvoker(Binding binding, Operation operation) {
        return getInvoker(binding, null, operation);
    }

    public Invoker getInvoker(Binding binding, InterfaceContract interfaceContract, Operation operation) {
        InvocationChain chain = getInvocationChain(binding, interfaceContract, operation);
        if (chain != null) {
            return chain.getHeadInvoker();
        } else {
            return null;
        }
    }

    public InvocationChain getInvocationChain(Binding binding, InterfaceContract interfaceContract, Operation operation) {
        RuntimeWire wire = getRuntimeWire(binding);
        if (wire == null) {
            return null;
        }
        if (interfaceContract != null && interfaceContract != wire.getSource().getInterfaceContract()) {
            try {
                // FIXME: [rfeng] We could avoid clone() using a better comparison of the two interface contracts
                wire = (RuntimeWire)wire.clone();
                wire.getSource().setInterfaceContract(interfaceContract);
                wire.rebuild();
            } catch (CloneNotSupportedException e) {
                throw new ServiceRuntimeException(e);
            }
        }
        return wire.getInvocationChain(operation);
    }

    public InvocationChain getInvocationChain(Binding binding, Operation operation) {
        return getInvocationChain(binding, null, operation);
    }

    /**
     * @see org.apache.tuscany.sca.assembly.impl.ComponentServiceImpl#clone()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        RuntimeComponentServiceImpl clone = (RuntimeComponentServiceImpl)super.clone();
        clone.bindingProviders = (HashMap<Binding, ServiceBindingProvider>)bindingProviders.clone();
        clone.wires = (ArrayList<RuntimeWire>)wires.clone();
        clone.callbackWires = (ArrayList<RuntimeWire>)callbackWires.clone();
        clone.policyProviders = (HashMap<Binding, List<PolicyProvider>>)policyProviders.clone();
        return clone;
    }

    public void addPolicyProvider(Binding binding, PolicyProvider policyProvider) {
        List<PolicyProvider> providers = policyProviders.get(binding);
        if (providers == null) {
            providers = new ArrayList<PolicyProvider>();
            policyProviders.put(binding, providers);
        }
        providers.add(policyProvider);
    }

    public List<PolicyProvider> getPolicyProviders(Binding binding) {
        return policyProviders.get(binding);
    }

    @Override
    public String toString() {
        return getName();
    }
}
