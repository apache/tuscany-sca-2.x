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

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 */
public class AssemblyModelContextImpl implements AssemblyModelContext {

    private AssemblyFactory assemblyFactory;
    private AssemblyModelLoader assemblyLoader;
    private ResourceLoader resourceLoader;

    /**
     * Constructor
     *
     * @param resourceLoader
     */
    public AssemblyModelContextImpl(AssemblyFactory assemblyFactory, AssemblyModelLoader assemblyLoader, ResourceLoader resourceLoader) {
        if (assemblyFactory!=null)
            this.assemblyFactory = assemblyFactory;
        else
            this.assemblyFactory = new AssemblyFactoryImpl();
        this.resourceLoader = resourceLoader;
        this.assemblyLoader = assemblyLoader;
        if (assemblyLoader!=null)
            assemblyLoader.setModelContext(this);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getAssemblyFactory()
     */
    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getResourceLoader()
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getAssemblyLoader()
     */
    public AssemblyModelLoader getAssemblyLoader() {
        return assemblyLoader;
    }
}
