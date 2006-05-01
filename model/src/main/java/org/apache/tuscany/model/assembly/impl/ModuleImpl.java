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
package org.apache.tuscany.model.assembly.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.util.NotifyingList;

/**
 * An implementation of Module.
 */
public class ModuleImpl extends CompositeImpl implements Module {

    /**
     * A list of module fragments synchronized with a map
     */
    private class ModuleFragmentList<E extends ModuleFragment> extends NotifyingList<E> {
        protected void added(E element) {
            moduleFragmentsMap.put(element.getName(), element);
        }

        protected void removed(E element) {
            moduleFragmentsMap.remove(element.getName());
        }
    }

    private List<ModuleFragment> moduleFragments = new ModuleFragmentList<ModuleFragment>();
    private Map<String, ModuleFragment> moduleFragmentsMap = new HashMap<String, ModuleFragment>();

    /**
     * Constructor
     */
    protected ModuleImpl() {
    }

    public List<ModuleFragment> getModuleFragments() {
        return moduleFragments;
    }

    public ModuleFragment getModuleFragment(String name) {
        checkInitialized();
        return moduleFragmentsMap.get(name);
    }

    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;

        // Initialize module fragments
        for (ModuleFragment moduleFragment : moduleFragments) {

            // Add all WSDL imports, components, entry points and external services from the module fragments
            getWSDLImports().addAll(moduleFragment.getWSDLImports());
            getComponents().addAll(moduleFragment.getComponents());
            getEntryPoints().addAll(moduleFragment.getEntryPoints());
            getExternalServices().addAll(moduleFragment.getExternalServices());

            // Add all the wires from the module fragments
            getWires().addAll(moduleFragment.getWires());

            moduleFragment.initialize(modelContext);
        }

        // Initialize the composite
        super.initialize(modelContext);
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();

        moduleFragments = freeze(moduleFragments);
    }

    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;

        return accept(moduleFragments, visitor);

    }

}
