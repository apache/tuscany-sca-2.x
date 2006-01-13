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
package org.apache.tuscany.model.assembly.sdo;

import org.eclipse.emf.ecore.resource.Resource;
import org.osoa.sca.model.ComponentType;
import org.osoa.sca.model.Module;
import org.osoa.sca.model.ModuleFragment;

/**
 */
public interface AssemblyResource extends Resource {

    /**
     * Returns the module
     *
     * @return
     */
    Module getModuleElement();

    /**
     * Returns the module fragment
     *
     * @return
     */
    ModuleFragment getModuleFragmentElement();

    /**
     * Returns the component type
     *
     * @return
     */
    ComponentType getComponentTypeElement();
	
}
