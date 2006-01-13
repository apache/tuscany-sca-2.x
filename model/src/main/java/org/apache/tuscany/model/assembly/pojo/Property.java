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

import commonj.sdo.Sequence;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class Property implements org.apache.tuscany.model.assembly.Property {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public Property() {
        super();
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        check();
        this.name = name;
    }

    private String propertyDefault;

    public String getDefault() {
        return propertyDefault;
    }

    public void setDefault(String propertyDefault) {
        check();
        this.propertyDefault = propertyDefault;
    }

    private boolean many;
    
    public boolean isMany() {
        return many;
    }

    public void setMany(boolean value) {
        many=value;
    }

    private boolean required;
    
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean value) {
        check();
        required = true;
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
