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
 * The implementation for a specific component instance.
 * This interface will typically be extended by component type implementations to indicate the
 * specific implementation to be used by a runtime and to allow for additional runtime configuration
 * properties.
 */
public interface ComponentImplementation extends Extensible, ContextFactoryHolder {

    /**
     * Returns the generic component type corresponding to this implementation.
     * @return the generic component type corresponding to this implementation
     */
    ComponentType getComponentType();
    
    /**
     * Sets the generic component type corresponding to this implementation.
     * @param componentType the generic component type corresponding to this implementation
     */
    void setComponentType(ComponentType componentType);

}
