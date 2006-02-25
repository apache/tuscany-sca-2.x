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
 * A logical definition of a type of component separate from any specific implementation.
 */
public interface ComponentType extends Extensible {

    /**
     * Returns a list of services exposed by this component type.
     * @return a list of services exposed by this component type
     */
    List<Service> getServices();

    /**
     * Returns the specfied service if exposed by this component type.
     * @param name the name of the service
     * @return the service identified by the supplied name, or null if there is no service with that name
     */
    Service getService(String name);

    /**
     * Returns the list of references this component type consumes.
     * @return the list of references this component type consumes
     */
    List<Reference> getReferences();

    /**
     * Returns the specified reference.
     * @param name the name of the reference
     * @return the reference identified by the supplied name, or null if there is no reference with that name
     */
    Reference getReference(String name);

    /**
     * Returns the list of properties that can be used to configure components with this component type.
     * @return the list of properties that can be used to configure components with this component type
     */
    List<Property> getProperties();

    /**
     * Returns the specified property
     * @param name the name of the property
     * @return the property with the supplied name, or null if there is no property with that name
     */
    Property getProperty(String name);

}
