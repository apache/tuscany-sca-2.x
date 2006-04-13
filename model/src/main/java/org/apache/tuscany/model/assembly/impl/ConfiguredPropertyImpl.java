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
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Property;

/**
 * Implementation of ConfiguredProperty
 */
public class ConfiguredPropertyImpl extends AssemblyObjectImpl implements ConfiguredProperty {
    private String name;
    private OverrideOption overrideOption;
    private Object value;
    private Property property;

    protected ConfiguredPropertyImpl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        checkNotFrozen();
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        checkNotFrozen();
        this.value = value;
    }

    public OverrideOption getOverrideOption() {
        return overrideOption;
    }
    
    public void setOverrideOption(OverrideOption value) {
        checkNotFrozen();
        this.overrideOption=value;
    }
    
    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        if (property!=null)
            property.initialize(modelContext);
    }
    
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        if (property!=null)
            property.freeze();
    }
    
    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (property!=null) {
            if (!property.accept(visitor))
                return false;
        }
        
        return true;
    }
    
}
