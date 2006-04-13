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

import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Part;

/**
 * Implementation of Part.
 */
public abstract class PartImpl extends ExtensibleImpl implements Part {
    private Composite composite;
    private String name;

    private Object contextFactory;

    protected PartImpl() {
    }

    public String getName() {
        return name;
    }
    
    public void setName(String value) {
        checkNotFrozen();
        name=value;
    }
    
    public Composite getComposite() {
        return composite;
    }

    public void setComposite(Composite composite) {
        checkNotFrozen();
        this.composite=composite;
    }

    public Object getContextFactory() {
        return contextFactory;
    }
    
    public void setContextFactory(Object contextFactory) {
        checkNotFrozen();
        this.contextFactory=contextFactory;
    }

}
