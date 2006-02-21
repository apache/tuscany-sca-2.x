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
 * Represents a component type.
 */
public interface ComponentType extends Extensible {

    /**
     * Returns the declared services.
     * @return
     */
    List<Service> getServices();

    /**
     * Returns the named service.
     * @param name
     * @return
     */
    Service getService(String name);

    /**
     * Returns the declared references.
     * @return
     */
    List<Reference> getReferences();

    /**
     * Returns the named reference.
     * @param name
     * @return
     */
    Reference getReference(String name);

    /**
     * Returns the properties declared by the component type.
     * @return
     */
    List<Property> getProperties();

    /**
     * Returns the named property.
     * @param name
     * @return
     */
    Property getProperty(String name);

}
