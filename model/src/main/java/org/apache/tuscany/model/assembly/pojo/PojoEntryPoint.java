/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.model.assembly.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Interface;

import commonj.sdo.Sequence;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PojoEntryPoint implements EntryPoint {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoEntryPoint() {
        super();
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        check();
        this.name = name;
    }

    private List<Binding> bindings = new ArrayList();

    public List<Binding> getBindings() {
        return bindings;
    }

    public void addBinding(Binding binding) {
        check();
        bindings.add(binding);
    }

    private ConfiguredReference reference;

    public ConfiguredReference getConfiguredReference() {
        check();
        return reference;
    }

    public void setConfiguredReference(ConfiguredReference reference) {
        check();
        this.reference = reference;
    }

    private Aggregate aggregate;

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        check();
        this.aggregate = aggregate;
    }

    private Interface contract;

    public Interface getInterfaceContract() {
        return contract;
    }

    public void setInterfaceContract(Interface contract) {
        check();
        this.contract = contract;
    }

    public Sequence getAny() {
        throw new UnsupportedOperationException();
    }

    public Sequence getAnyAttribute() {
        throw new UnsupportedOperationException();
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        for (Binding binding : bindings) {
            if (!binding.accept(visitor)) {
                return false;
            }
        }
        if (reference != null && !reference.accept(visitor)) {
            return false;
        }
        if (aggregate != null && !aggregate.accept(visitor)) {
            return false;
        }
        if (contract != null && !contract.accept(visitor)) {
            return false;
        }
        return true;
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }
}
