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

import java.util.List;
import java.util.Map;


/**
 * A component is a configured instance of a generic {@link ComponentImplementation}.
 */
public interface Component extends Extensible, AggregatePart {

    /**
     * Returns the ComponentImplementation for this configured instance.
     * @return the ComponentImplementation for this configured instance
     */
    ComponentImplementation getComponentImplementation();

    /**
     * Sets the ComponentImplementation for this configured instance
     * @param value the ComponentImplementation for this configured instance
     */
    void setComponentImplementation(ComponentImplementation value);

    /**
     * Returns a list of configured property values for this configured instance.
     * These values will be used to initialize the component when it is activated.
     * @return a list of ConfiguredProperty values
     */
    List<ConfiguredProperty> getConfiguredProperties();

    /**
     * Returns the ConfiguredProperty value for the specified property.
     *
     * @param name the name of the Property
     * @return the configured property value for the named property
     */
    ConfiguredProperty getConfiguredProperty(String name);

    /**
     * Returns a live map of this components configured references, keyed by reference name.
     *
     * @return a live map of configured references
     */
    Map<String, ConfiguredReference> getConfiguredReferences();

    /**
     * Returns the ConfiguredReference value for the specified reference.
     * @param name the name of the Property
     * @return the configured reference value for the named reference
     */
    ConfiguredReference getConfiguredReference(String name);

    /**
     * Returns the configured services for the configured instance.
     * @return the configured services for the configured instance
     */
    List<ConfiguredService> getConfiguredServices();

    /**
     * Returns the ConfiguredService value for the specified property.
     * @param name the name of the Property
     * @return the configured service value for the named service
     */
    ConfiguredService getConfiguredService(String name);

}
