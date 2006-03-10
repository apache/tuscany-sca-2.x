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

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Property;

/**
 * Implementation of ConfiguredProperty
 */
public class ConfiguredPropertyImpl extends AssemblyModelObjectImpl implements ConfiguredProperty {
    private String name;
    private OverrideOption overrideOption;
    private Object value;
    private Property property;

    /**
     * Constructor
     */
    protected ConfiguredPropertyImpl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#getProperty()
     */
    public Property getProperty() {
        return property;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#setProperty(org.apache.tuscany.model.assembly.Property)
     */
    public void setProperty(Property property) {
        checkNotFrozen();
        this.property = property;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#getValue()
     */
    public Object getValue() {
        return value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        checkNotFrozen();
        this.value = value;
    }

    /*
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#getOverrideOption()
     */
    public OverrideOption getOverrideOption() {
        return overrideOption;
    }
    
    /*
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#setOverrideOption(org.apache.tuscany.model.assembly.OverrideOption)
     */
    public void setOverrideOption(OverrideOption value) {
        checkNotFrozen();
        this.overrideOption=value;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        if (property!=null)
            property.initialize(modelContext);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        if (property!=null)
            property.freeze();
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (property!=null) {
            if (!property.accept(visitor))
                return false;
        }
        
        return true;
    }
    
}
