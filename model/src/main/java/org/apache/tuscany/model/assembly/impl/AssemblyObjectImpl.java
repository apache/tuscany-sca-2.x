/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.AssemblyVisitor;

/**
 * A base class for assembly model objects.
 */
public abstract class AssemblyObjectImpl implements AssemblyObject {

    private boolean frozen;
    private boolean initialized;
    
    protected AssemblyObjectImpl() {
    }

    public boolean accept(AssemblyVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Walk a visitor through a collection of model objects.
     * @param collection
     * @param visitor
     */
    protected boolean accept(Collection collection, AssemblyVisitor visitor) {
        for (Object member : collection) {
            if (member instanceof AssemblyObject) {
                if (!((AssemblyObject)member).accept(visitor))
                    return false;
            }
        }
        return true;
    }
    
    public void freeze() {
        if (!frozen)
            frozen=true;
    }

    /**
     * Returns true if the object is frozen
     */
    protected boolean isFrozen() {
        return frozen;
    }
    
    /**
     * Freeze a list and its members
     */
    protected <T> List<T> freeze(List<T> list) {
        list=Collections.unmodifiableList(list);
        for (Object member : list) {
            if (member instanceof AssemblyObject) {
                ((AssemblyObject)member).freeze();
            }
        }
        return list;
    }
    
    /**
     * Check that the current model object can be modified.
     * @throws IllegalStateException
     */
    protected void checkNotFrozen() {
        if (frozen)
            throw new IllegalStateException("Attempt to modify a frozen assembly model");
    }
    
    public void initialize(AssemblyContext modelContext) {
        if (!initialized)
            initialized=true;
    }
    
    /**
     * Returns true if the object is initialized
     */
    protected boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Initialize members of a collection
     */
    protected void initialize(Collection collection, AssemblyContext modelContext) {
        for (Object member : collection) {
            if (member instanceof AssemblyObject) {
                ((AssemblyObject)member).initialize(modelContext);
            }
        }
    }
    
    /**
     * Check that the current model object is initialized.
     * @throws IllegalStateException
     */
    protected void checkInitialized() {
        if (!initialized)
            throw new IllegalStateException("Attempt to use an uninitialized assembly model");
    }
    
}
