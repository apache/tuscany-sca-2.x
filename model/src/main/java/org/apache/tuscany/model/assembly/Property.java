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
package org.apache.tuscany.model.assembly;


/**
 * Represents a property definition.
 */
public interface Property extends Extensible {
    
    /**
     * Returns the default value of the property.
     * @return
     */
    Object getDefaultValue();

    /**
     * Sets the default value of the property.
     * @param value
     */
    void setDefaultValue(Object value);

    /**
     * Returns true if the property allows multiple values.
     * @return
     */
    boolean isMany();

    /**
     * Sets whether or not the property allows many values.
     * @param value
     */
    void setMany(boolean value);

    /**
     * Returns the property name.
     * @return
     */
    String getName();

    /**
     * Sets the property name.
     * @param value
     */
    void setName(String value);

    /**
     * Returns true if the property is required.
     * @return
     */
    boolean isRequired();

    /**
     * Sets the whether or not the property is required.
     * @param value
     */
    void setRequired(boolean value);

    /**
     * Returns the property type.
     * @return
     */
    Class getType();

    /**
     * Sets the property type.
     * @param value
     */
    void setType(Class value);

} // Property
