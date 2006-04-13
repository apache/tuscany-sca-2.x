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

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Binding;

/**
 * An implementation of Binding.
 */
public class BindingImpl extends ExtensibleImpl implements Binding {
    
    private String uri;

    protected BindingImpl() {
    }
    
    public String getURI() {
        return uri;
    }
    
    public void setURI(String value) {
        checkNotFrozen();
        uri=value;
    }
    
    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
    }

    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        return true;
    }
    
}
