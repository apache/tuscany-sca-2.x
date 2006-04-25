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
 * The configured value of a {@link Property}.
 */
public interface ConfiguredProperty extends AssemblyObject {

    //FIXME remove the name attribute?
    /**
     * Returns the name of the property being configured.
     * @return the name of the property being configured
     */
    String getName();

    /**
     * Set the name of the property being configured.
     * @param name the name of the property being configured
     */
    void setName(String name);

    /**
     * Returns the {@link Property} whose value is being set.
     * @return the {@link Property} whose value is being set
     */
    Property getProperty();

    /**
     * Sets the {@link Property} whose value is being set.
     * @param property the {@link Property} whose value is being set
     */
    void setProperty(Property property);

    /**
     * Returns the value being set for this usage of the {@link Property}.
     * @return the value being set for this usage of the {@link Property}
     */
    Object getValue();

    /**
     * Sets the value being set for this usage of the {@link Property}.
     * @param value the value being set for this usage of the {@link Property}
     */
    void setValue(Object value);

    /**
     * Returns the override option that determines if any configuration for this property
     * that is contained in this composite can be overridden by configuration supplied from outside.
     */
    OverrideOption getOverrideOption();

    /**
     * Set the override option that determines if any configuration for this property
     * that is contained in this composite can be overridden by configuration supplied from outside.
     *
     * @param value the option that determines how property configuration can be overriden
     */
    void setOverrideOption(OverrideOption value);
}
