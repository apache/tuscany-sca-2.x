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

import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.SCABindingImpl;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.provider.ReferenceBindingProvider;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeSCABindingProvider extends SCABindingImpl implements SCABinding, ReferenceBindingProvider {
    
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    
    public RuntimeSCABindingProvider(RuntimeComponent component, RuntimeComponentReference reference) {
        this.component = component;
        this.reference = reference;
    }
    
    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        return null;
    }

    public void start() {
    }

    public void stop() {
    }

}
