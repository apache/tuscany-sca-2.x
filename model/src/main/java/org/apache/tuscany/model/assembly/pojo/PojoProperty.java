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
import org.apache.tuscany.model.assembly.Property;

import commonj.sdo.Sequence;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PojoProperty implements Property {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoProperty() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String defaultValue;

    public String getDefault() {
        return defaultValue;
    }

    public void setDefault(String value) {
        check();
        defaultValue = value;
    }

    private boolean many;

    public boolean isMany() {
        return many;
    }

    public void setMany(boolean value) {
        check();
        many = value;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        check();
        this.name = name;
    }

    private boolean required;

    public boolean isRequired() {
        return true;
    }

    public void setRequired(boolean value) {
        check();
        required = value;
    }

    private Object type;

    public Object getType_() {
        return type;
    }

    public void setType(Object type) {
        check();
        this.type = type;
    }

    public Sequence getAny() {
        throw new UnsupportedOperationException();
    }

    public Sequence getAnyAttribute() {
        throw new UnsupportedOperationException();
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        return visitor.visit(this);
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
