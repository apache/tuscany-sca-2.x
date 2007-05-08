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
import org.apache.tuscany.assembly.impl.ComponentReferenceImpl;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.provider.ReferenceBindingProvider;

public class RuntimeComponentReferenceImpl extends ComponentReferenceImpl implements RuntimeComponentReference {
    private List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    private Map<Binding, ReferenceBindingProvider> bindingProviders = new HashMap<Binding, ReferenceBindingProvider>();

    public void addRuntimeWire(RuntimeWire wire) {
        wires.add(wire);
    }

    public List<RuntimeWire> getRuntimeWires() {
        return wires;
    }

    public RuntimeWire getRuntimeWire(Binding binding) {
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding() == binding) {
                return wire;
            }
        }
        return null;
    }
    
    public ReferenceBindingProvider getBindingProvider(Binding binding) {
        return bindingProviders.get(binding);
    }
    
    public void setBindingProvider(Binding binding, ReferenceBindingProvider bindingProvider) {
        bindingProviders.put(binding, bindingProvider);
    }

}
