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
import java.util.List;

import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.SCABindingImpl;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.provider.BindingProviderFactory;
import org.apache.tuscany.provider.ReferenceBindingProvider;
import org.apache.tuscany.provider.ServiceBindingProvider;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeSCABindingImpl extends SCABindingImpl implements SCABinding, BindingProviderFactory {
    private List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    
    public void addWire(RuntimeWire wire) {
        wires.add(wire);
    }

    public List<RuntimeWire> getWires() {
        return wires;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference) {
        return new RuntimeSCABindingProvider(component, reference);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service) {
        return null;
    }
    
}
