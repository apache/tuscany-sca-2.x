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
package org.apache.tuscany.core.context;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.osoa.sca.ModuleContext;

/**
 * Represents an aggregate context that is also a module component. TODO This should probably go away
 * 
 * @version $Rev$ $Date$
 */
public interface TuscanyModuleComponentContext extends AggregateContext, ModuleContext {

    /**
     * Returns the assembly model context.
     * <p>
     * FIXME Should this class be exposed here or as a runtime component? FIXME Ccontains references to EMF
     */
    AssemblyModelContext getAssemblyModelContext();

    /**
     * Returns the module component
     */
    ModuleComponent getModuleComponent();

}
