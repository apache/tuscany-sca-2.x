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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Extensible;

/**
 * An implementation of Extensible.
 */
public abstract class ExtensibleImpl extends AssemblyModelObjectImpl implements Extensible {
    
    private List<Object> extensibilityElements=new ArrayList<Object>();
    private List<Object> extensibilityAttributes=new ArrayList<Object>();

    /**
     * Constructor
     */
    protected ExtensibleImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Extensible#getExtensibilityElements()
     */
    public List getExtensibilityElements() {
        return extensibilityElements;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Extensible#getExtensibilityAttributes()
     */
    public List getExtensibilityAttributes() {
        return extensibilityAttributes;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize extensibility elements and attributes
        initialize(extensibilityElements, modelContext);
        initialize(extensibilityAttributes, modelContext);
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        // Freeze extensibility elements and attributes
        freeze(extensibilityElements);
        freeze(extensibilityAttributes);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (!accept(extensibilityElements, visitor))
            return false;
        if (!accept(extensibilityAttributes, visitor))
            return false;
        
        return true;
    }

}
