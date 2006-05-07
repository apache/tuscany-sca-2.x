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
package org.apache.tuscany.core.system.assembly.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.system.assembly.SystemModule;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.model.assembly.impl.CompositeImpl;

/**
 * An implementation of Module.
 */
public class SystemModuleImpl extends CompositeImpl implements SystemModule {

    private List<ModuleFragment> moduleFragments = new ArrayList<ModuleFragment>();
    private Map<String, ModuleFragment> moduleFragmentsMap;
    private ComponentType componentType;
    private Object contextFactory;

    /**
     * Constructor
     */
    protected SystemModuleImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Implementation#getComponentType()
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Implementation#setComponentType(org.apache.tuscany.model.assembly.ComponentType)
     */
    public void setComponentType(ComponentType componentType) {
        checkNotFrozen();
        this.componentType = componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Module#getModuleFragments()
     */
    public List<ModuleFragment> getModuleFragments() {
        return moduleFragments;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Module#getModuleFragment(java.lang.String)
     */
    public ModuleFragment getModuleFragment(String name) {
        checkInitialized();
        return moduleFragmentsMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyObject#initialize(org.apache.tuscany.model.assembly.AssemblyContext)
     */
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

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyObject#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();

        // Freeze component type and module fragments
        if (componentType != null)
            componentType.freeze();
        moduleFragments = Collections.unmodifiableList(moduleFragments);
        freeze(moduleFragments);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ContextFactoryHolder#getContextFactory()
     */
    public Object getContextFactory() {
        return contextFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ContextFactoryHolder#setContextFactory(java.lang.Object)
     */
    public void setContextFactory(Object configuration) {
        checkNotFrozen();
        this.contextFactory = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.CompositeImpl#accept(org.apache.tuscany.model.assembly.AssemblyVisitor)
     */
    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;

        if (componentType != null) {
            if (!componentType.accept(visitor))
                return false;
        }

        return accept(moduleFragments, visitor);

    }

    public Class<?> getImplementationClass() {
        return SystemCompositeContextImpl.class; // FIXME hack
    }

    public void setImplementationClass(Class<?> clazz) {
        // do nothing
    }

}
