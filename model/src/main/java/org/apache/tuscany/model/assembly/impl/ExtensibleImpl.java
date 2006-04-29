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

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Extensible;

/**
 * An implementation of Extensible.
 */
public abstract class ExtensibleImpl extends AssemblyObjectImpl implements Extensible {

    private List<Object> extensibilityElements = new ArrayList<Object>();
    private List<Object> extensibilityAttributes = new ArrayList<Object>();

    protected ExtensibleImpl() {
    }

    public List<Object> getExtensibilityElements() {
        return extensibilityElements;
    }

    public List<Object> getExtensibilityAttributes() {
        return extensibilityAttributes;
    }

    public void initialize(AssemblyContext modelContext) {
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

    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;

        if (!accept(extensibilityElements, visitor))
            return false;
        return accept(extensibilityAttributes, visitor);

    }

}
