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
 * A specialized {@link ComponentImplementation} that defines a modular unit of assembly.
 * A Module denotes the extent of assembly in which pass-by-reference semantics are supported.
 */
public interface Module extends Aggregate, ComponentImplementation {
    /**
     * Returns a list of assembly fragments that combine to form a single module.
     * @return a list of assembly fragments that combine to form a single module
     */
    List<ModuleFragment> getModuleFragments();

    /**
     * Returns the specified assembly fragment.
     * @param name the name of the fragment
     * @return the fragment with the specified name, or null if there is no fragment with that name
     */
    ModuleFragment getModuleFragment(String name);

}
