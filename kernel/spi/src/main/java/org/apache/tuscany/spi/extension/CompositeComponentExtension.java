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
package org.apache.tuscany.spi.extension;

import java.net.URI;

import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * An extension point for composite components, which new types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeComponentExtension extends AbstractComponentExtension implements RuntimeEventListener {

    protected CompositeComponentExtension(URI name) {
        super(name);
    }

    public Scope getScope() {
        return Scope.SYSTEM;
    }

    public void onEvent(Event event) {
        publish(event);
    }

    public TargetInvoker createTargetInvoker(String name, Operation operation)
        throws TargetInvokerCreationException {
        Service service = getService(name);
        if (service != null) {
            if (service.getServiceBindings().isEmpty()) {
                // for now, throw an assertion exception.
                // We will need to choose bindings during allocation
                throw new AssertionError();
            }
            ServiceBinding binding = service.getServiceBindings().get(0);
            return binding.createTargetInvoker(name, operation);
        }
        Reference reference = getReference(name);
        if (reference != null) {
            if (reference.getReferenceBindings().isEmpty()) {
                // for now, throw an assertion exception.
                // We will need to choose bindings during allocation
                throw new AssertionError();
            }
            ReferenceBinding binding = reference.getReferenceBindings().get(0);
            binding.createTargetInvoker(name, operation);
        }
        return null;
    }

    public TargetInvoker createTargetInvoker(String name, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        Service service = getService(name);
        if (service != null) {
            if (service.getServiceBindings().isEmpty()) {
                // for now, throw an assertion exception.
                // We will need to choose bindings during allocation
                throw new AssertionError();
            }
            ServiceBinding binding = service.getServiceBindings().get(0);
            return binding.createTargetInvoker(name, operation);
        }
        Reference reference = getReference(name);
        if (reference != null) {
            if (reference.getReferenceBindings().isEmpty()) {
                // for now, throw an assertion exception.
                // We will need to choose bindings during allocation
                throw new AssertionError();
            }
            ReferenceBinding binding = reference.getReferenceBindings().get(0);
            binding.createTargetInvoker(name, operation);
        }
        return null;
    }

}
