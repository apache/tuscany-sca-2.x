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
 * A representation of the model object '<em><b>Component Type</b></em>'.
 */
public interface ComponentType extends ExtensibleModelObject {

    /**
     * Returns the value of the '<em><b>Services</b></em>' containment reference list.
     */
    List<Service> getServices();

    /**
     * Returns the named TService.
     *
     * @param name
     * @return
     */
    Service getService(String name);

    /**
     * Returns the value of the '<em><b>References</b></em>' containment reference list.
     */
    List<Reference> getReferences();

    /**
     * Returns the named TReference.
     *
     * @param name
     * @return
     */
    Reference getReference(String name);

    /**
     * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
     */
    List<Property> getProperties();

    /**
     * Returns the named TProperty.
     *
     * @param name
     * @return
     */
    Property getProperty(String name);

} // ComponentType
