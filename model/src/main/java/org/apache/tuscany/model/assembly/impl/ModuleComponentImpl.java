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

import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * An implementation of ModuleComponent.
 */
public class ModuleComponentImpl extends ComponentImpl implements ModuleComponent {
    
    private String uri;

    /**
     * Constructor
     */
    protected ModuleComponentImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#setModuleImplementation(org.apache.tuscany.model.assembly.Module)
     */
    public void setModuleImplementation(Module module) {
        checkNotFrozen();
        super.setComponentImplementation(module);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#getModuleImplementation()
     */
    public Module getModuleImplementation() {
        return (Module)super.getComponentImplementation();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#getURI()
     */
    public String getURI() {
        return uri;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#setURI(java.lang.String)
     */
    public void setURI(String value) {
        checkNotFrozen();
        uri=value;
    }

}
