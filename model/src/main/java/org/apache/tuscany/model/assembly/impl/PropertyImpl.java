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

    protected PropertyImpl() {
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isMany() {
        return many;
    }

    public boolean isRequired() {
        return required;
    }

    public void setDefaultValue(Object value) {
        checkNotFrozen();
        defaultValue = value;
    }

    public void setMany(boolean value) {
        checkNotFrozen();
        many = value;
    }

    public void setName(String value) {
        checkNotFrozen();
        name = value;
    }

    public void setRequired(boolean value) {
        checkNotFrozen();
        required = value;
    }

    public void setType(Class<?> value) {
        checkNotFrozen();
        type = value;
    }
}
