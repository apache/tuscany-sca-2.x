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
 * /**
 * A representation of the model object '<em><b>Component</b></em>'.
 */
public interface Component extends ExtensibleModelObject, Part {

    /**
     * Returns the value of the '<em><b>Implementation</b></em>' containment reference.
     */
    ComponentImplementation getComponentImplementation();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Component#getImplementation <em>Implementation</em>}' containment reference.
     */
    void setComponentImplementation(ComponentImplementation value);

    /**
     * Returns the value of the '<em><b>Properties</b></em>' containment reference.
     */
    List<ConfiguredProperty> getConfiguredProperties();

    /**
     * Returns the value of the named property.
     *
     * @param name
     * @return
     */
    ConfiguredProperty getConfiguredProperty(String name);

    /**
     * Returns the value of the '<em><b>References</b></em>' containment reference.
     */
    List<ConfiguredReference> getConfiguredReferences();

    /**
     * Returns the named reference value.
     *
     * @param name
     * @return
     */
    ConfiguredReference getConfiguredReference(String name);

    /**
     * Returns the value of the '<em><b>Services</b></em>' containment reference.
     */
    List<ConfiguredService> getConfiguredServices();

    /**
     * Returns the named service endpoint.
     *
     * @param name
     * @return
     */
    ConfiguredService getConfiguredService(String name);
	
}
