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


/**
 * Represents a component.
 */
public interface Component extends Extensible, AggregatePart {

    /**
     * Returns the component implementation.
     * @return
     */
    ComponentImplementation getComponentImplementation();

    /**
     * Sets the component implementation.
     * @param value
     */
    void setComponentImplementation(ComponentImplementation value);

    /**
     * Returns the configured properties.
     * @return
     */
    List<ConfiguredProperty> getConfiguredProperties();

    /**
     * Returns the named configured property.
     * @param name
     * @return
     */
    ConfiguredProperty getConfiguredProperty(String name);

    /**
     * Returns the configured references.
     * @return
     */
    List<ConfiguredReference> getConfiguredReferences();

    /**
     * Returns the named configured reference.
     * @param name
     * @return
     */
    ConfiguredReference getConfiguredReference(String name);

    /**
     * Returns the configured services.
     * @return
     */
    List<ConfiguredService> getConfiguredServices();

    /**
     * Returns the named configured service.
     * @param name
     * @return
     */
    ConfiguredService getConfiguredService(String name);
	
}
