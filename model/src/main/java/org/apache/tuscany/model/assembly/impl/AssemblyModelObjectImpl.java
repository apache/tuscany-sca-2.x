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

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;

/**
 * A base class for assembly model objects.
 */
public abstract class AssemblyModelObjectImpl implements AssemblyModelObject {

    private boolean frozen;
    private boolean initialized;
    
    /**
     * Constructor
     */
    protected AssemblyModelObjectImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Walk a visitor through a collection of model objects.
     * @param collection
     * @param visitor
     * @return
     */
    protected boolean accept(Collection collection, AssemblyModelVisitor visitor) {
        for (Object member : collection) {
            if (member instanceof AssemblyModelObject) {
                if (!((AssemblyModelObject)member).accept(visitor))
                    return false;
            }
        }
        return true;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
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
     * Freeze members of a collection
     */
    protected void freeze(Collection collection) {
        for (Object member : collection) {
            if (member instanceof AssemblyModelObject) {
                ((AssemblyModelObject)member).freeze();
            }
        }
    }
    
    /**
     * Check that the current model object can be modified.
     * @throws IllegalStateException
     */
    protected void checkNotFrozen() {
        if (frozen)
            throw new IllegalStateException("Attempt to modify a frozen assembly model");
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
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
    protected void initialize(Collection collection, AssemblyModelContext modelContext) {
        for (Object member : collection) {
            if (member instanceof AssemblyModelObject) {
                ((AssemblyModelObject)member).initialize(modelContext);
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
