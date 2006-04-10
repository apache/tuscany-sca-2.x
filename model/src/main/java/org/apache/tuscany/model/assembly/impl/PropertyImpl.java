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

import org.apache.tuscany.model.assembly.Property;

/**
 * An implementation of Property.
 */
public class PropertyImpl extends ExtensibleImpl implements Property {

    private Object defaultValue;
    private String name;
    private boolean many;
    private boolean required;
    private Class<?> type;

    /**
     * Constructor
     */
    protected PropertyImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getDefaultValue()
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getType()
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#isMany()
     */
    public boolean isMany() {
        return many;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setDefaultValue(java.lang.Object)
     */
    public void setDefaultValue(Object value) {
        defaultValue = value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setMany(boolean)
     */
    public void setMany(boolean value) {
        checkNotFrozen();
        many = value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setName(java.lang.String)
     */
    public void setName(String value) {
        checkNotFrozen();
        name = value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setRequired(boolean)
     */
    public void setRequired(boolean value) {
        checkNotFrozen();
        required = value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setType(java.lang.Class)
     */
    public void setType(Class<?> value) {
        checkNotFrozen();
        type = value;
    }
}
