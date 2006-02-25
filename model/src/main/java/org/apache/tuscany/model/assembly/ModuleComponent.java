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
 * Specialization of Component that represents a configured {@link Module}.
 */
public interface ModuleComponent extends Component {
    // todo do we need this or can we just override getComponentImplementation() ?
    /**
     * Returns the module implementing this module component.
     * @return the module implementing this module component
     */
    Module getModuleImplementation();

    /**
     * Set the module implementing this module component.
     * @param module the module implementing this module component
     */
    void setModuleImplementation(Module module);

    /**
     * Returns the uri that uniquely identifies this module component.
     * @return the uri that uniquely identifies this module component
     */
    String getURI();

    /**
     * Sets the uri that uniquely identifies this module component.
     * @param uri the uri that uniquely identifies this module component
     */
    void setURI(String uri);
}
