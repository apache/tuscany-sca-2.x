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
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.TypeHelper;

/**
 */
public class AssemblyModelContextImpl implements AssemblyModelContext {

    private final AssemblyFactory assemblyFactory;
    private final AssemblyModelLoader assemblyLoader;
    private final ResourceLoader applicationResourceLoader;
    private final TypeHelper typeHelper;

    public AssemblyModelContextImpl(AssemblyModelLoader assemblyLoader, ResourceLoader resourceLoader) {
        this(new AssemblyFactoryImpl(), assemblyLoader, resourceLoader);
    }

    public AssemblyModelContextImpl(AssemblyFactory assemblyFactory, AssemblyModelLoader assemblyLoader, ResourceLoader artifactLoader) {
        this(assemblyFactory, assemblyLoader, artifactLoader, SDOUtil.createTypeHelper());
    }
    
    public AssemblyModelContextImpl(AssemblyFactory assemblyFactory, AssemblyModelLoader assemblyLoader, ResourceLoader artifactLoader, TypeHelper typeHelper) {
        this.assemblyFactory = assemblyFactory;
        this.assemblyLoader = assemblyLoader;
        this.applicationResourceLoader = artifactLoader;
        this.typeHelper=typeHelper;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getAssemblyFactory()
     */
    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }

    public ResourceLoader getApplicationResourceLoader() {
        return applicationResourceLoader;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getAssemblyLoader()
     */
    public AssemblyModelLoader getAssemblyLoader() {
        return assemblyLoader;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getTypeHelper()
     */
    public TypeHelper getTypeHelper() {
        return typeHelper;
    }
}
