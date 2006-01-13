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

import org.eclipse.emf.common.util.URI;

import org.apache.tuscany.model.util.ConfiguredResourceSetImpl;
import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyLoader;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.sdo.AssemblyResource;

/**
 */
public class AssemblyLoaderImpl extends ConfiguredResourceSetImpl implements AssemblyLoader {

    private AssemblyModelContext modelContext;

    /**
     * Constructor
     */
    public AssemblyLoaderImpl(AssemblyModelContext modelContext, ResourceLoader resourceLoader) {
        super(resourceLoader);
        this.modelContext = modelContext;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyLoader#getModule(java.lang.String)
     */
    public Module getModule(String uri) {
        AssemblyResource resource = (AssemblyResource) getResource(URI.createURI(uri), true);
        Module module = (Module) resource.getModuleElement();
        return module;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyLoader#getModuleFragment(java.lang.String)
     */
    public ModuleFragment getModuleFragment(String uri) {
        AssemblyResource resource = (AssemblyResource) getResource(URI.createURI(uri), true);
        ModuleFragment moduleFragment = (ModuleFragment) resource.getModuleFragmentElement();
        return moduleFragment;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyLoader#getComponentType(java.lang.String)
     */
    public ComponentType getComponentType(String uri) {
        AssemblyResource resource = (AssemblyResource) getResource(URI.createURI(uri), true);
        ComponentType componentType = (ComponentType) resource.getComponentTypeElement();
        return componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyLoader#getAssemblyModelContext()
     */
    public AssemblyModelContext getAssemblyModelContext() {
        return modelContext;
    }

}
