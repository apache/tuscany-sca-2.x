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

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Part;

public class PojoPart implements Part {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoPart() {
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

    private Aggregate aggregate;

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        check();
        this.aggregate = aggregate;
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
        if (aggregate != null) {
            return aggregate.accept(visitor);
        } else {
            return true;
        }
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
