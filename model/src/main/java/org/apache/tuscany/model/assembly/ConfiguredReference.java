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
 * A configured reference associated with a particular usage.
 * Each configuredReference represents a configured version of an logical
 * reference defined in the ComponentType. If the logical reference
 * has a multiplicity greater than 1 (0..n or 1..n) then the configured
 * reference many have multiple targets.
 */
public interface ConfiguredReference extends ConfiguredPort<Reference> {

    /**
     * List of URIs for the targets of this reference.
     *
     * @return the list of URIs for the targets of this reference
     */
    List<String> getTargets();

    /**
     * Returns the list of configured services that are wired to this configured reference.
     *
     * @return the list of configured services that are wired to this configured reference
     */
    List<ConfiguredService> getTargetConfiguredServices();

}
