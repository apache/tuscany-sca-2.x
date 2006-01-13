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

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.Property;

public class PojoConfiguredProperty implements ConfiguredProperty {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoConfiguredProperty() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private Component component;

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        check();
        this.component = component;
    }

    private Property property;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        check();
        this.property = property;
    }

    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        check();
        this.value = value;
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        component.freeze();
        property.freeze();
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (!component.accept(visitor)) {
            return false;
        }
        if (!property.accept(visitor)) {
            return false;
        }
        return true;
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }
}
