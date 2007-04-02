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

package org.apache.tuscany.assembly.model.impl;

import org.apache.tuscany.assembly.model.AbstractProperty;
import org.apache.tuscany.assembly.model.AbstractReference;
import org.apache.tuscany.assembly.model.AbstractService;
import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.Callback;
import org.apache.tuscany.assembly.model.Component;
import org.apache.tuscany.assembly.model.ComponentProperty;
import org.apache.tuscany.assembly.model.ComponentReference;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.ComponentType;
import org.apache.tuscany.assembly.model.Composite;
import org.apache.tuscany.assembly.model.CompositeReference;
import org.apache.tuscany.assembly.model.CompositeService;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Reference;
import org.apache.tuscany.assembly.model.SCABinding;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.assembly.model.Wire;
import org.apache.tuscany.sca.idl.Operation;
import org.apache.tuscany.sca.idl.impl.OperationImpl;

/**
 * A factory for the assembly model.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultAssemblyFactory implements AssemblyFactory {

    public AbstractProperty createAbstractProperty() {
        return new AbstractPropertyImpl();
    }

    public AbstractReference createAbstractReference() {
        return new AbstractReferenceImpl();
    }

    public AbstractService createAbstractService() {
        return new AbstractServiceImpl();
    }

    public Callback createCallback() {
        return new CallbackImpl();
    }

    public Component createComponent() {
        return new ComponentImpl();
    }

    public ComponentProperty createComponentProperty() {
        return new ComponentPropertyImpl();
    }

    public ComponentReference createComponentReference() {
        return new ComponentReferenceImpl();
    }

    public ComponentService createComponentService() {
        return new ComponentServiceImpl();
    }

    public ComponentType createComponentType() {
        return new ComponentTypeImpl();
    }

    public Composite createComposite() {
        return new CompositeImpl();
    }

    public CompositeReference createCompositeReference() {
        return new CompositeReferenceImpl();
    }

    public CompositeService createCompositeService() {
        return new CompositeServiceImpl();
    }

    public ConstrainingType createConstrainingType() {
        return new ConstrainingTypeImpl();
    }

    public Property createProperty() {
        return new PropertyImpl();
    }

    public Reference createReference() {
        return new ReferenceImpl();
    }

    public Service createService() {
        return new ServiceImpl();
    }

    public Wire createWire() {
        return new WireImpl();
    }

    public SCABinding createSCABinding() {
        return new SCABindingImpl();
    }

    public Operation createOperation() {
        return new OperationImpl();
    }
}
