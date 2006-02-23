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
package org.apache.tuscany.core.system.assembly;

import org.apache.tuscany.model.assembly.AssemblyFactory;

/**
 * A factory for building system assembly model artifacts
 * 
 * @version $Rev$ $Date$
 */
public interface SystemAssemblyFactory extends AssemblyFactory {

    /**
     * Returns an assembly model artifact representing a system component implementation
     */
    SystemImplementation createSystemImplementation(); 

    /**
     * Returns an assembly model artifact representing a system binding
     */
    SystemBinding createSystemBinding();

}
