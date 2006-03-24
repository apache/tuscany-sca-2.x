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
 * A description of a property that can be used to configure a component.
 */
public interface Property extends Extensible {
    // todo should we decalre this as Property<T> where T would be the type of this Property?

    /**
     * Returns the property name.
     * @return the property name
     */
    String getName();

    /**
     * Sets the property name.
     * @param name the property name
     */
    void setName(String name);

    /**
     * Returns the default value of the property.
     * @return the default value of ths property
     */
    Object getDefaultValue();

    /**
     * Sets the default value of the property.
     * @param value the default value of ths property
     */
    void setDefaultValue(Object value);

    /**
     * Returns true if the property allows multiple values.
     * @return true if the property allows multiple values
     */
    boolean isMany();

    /**
     * Sets whether or not the property allows multiple values.
     * @param value true if the property should allow multiple values
     */
    void setMany(boolean value);

    /**
     * Returns true if a value must be supplied for the property.
     * @return true is a value must be supplied for the property
     */
    boolean isRequired();

    /**
     * Sets whether a value must be supplied for the property.
     * For ease of use, it is recommended that a meaningful default value should
     * be supplied for all properties; users should only be required to specify
     * a value if there is no reasonable default.
     *
     * @param value set to true to require that a value be supplied for uses of this property
     */
    void setRequired(boolean value);

    /**
     * Returns the type of this property as used by the runtime.
     * @return the type of this property as used by the runtime
     */
    Class<?> getType();

    /**
     * Sets the type of this property as used by the runtime
     * @param value the type of this property as used by the runtime
     */
    void setType(Class<?> value);

}
