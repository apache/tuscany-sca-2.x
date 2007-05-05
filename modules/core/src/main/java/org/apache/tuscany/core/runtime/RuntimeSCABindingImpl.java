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

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.SCABindingImpl;
import org.apache.tuscany.core.ReferenceBindingProvider;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeSCABindingImpl extends SCABindingImpl implements SCABinding, ReferenceBindingProvider {
    private List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    
    public void addWire(RuntimeWire wire) {
        wires.add(wire);
    }

    public Interceptor createInterceptor(Reference reference, Binding binding, Operation operation, boolean isCallback) {
        return null;
    }

    public InterfaceContract getBindingInterfaceContract(ComponentReference reference) {
        return reference.getInterfaceContract();
    }

    public List<RuntimeWire> getWires() {
        return wires;
    }

    public Interceptor createInterceptor(Component component, ComponentReference reference, Operation operation, boolean isCallback) {
        return null;
    }


}
