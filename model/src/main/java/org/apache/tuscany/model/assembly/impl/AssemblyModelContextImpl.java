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

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.common.resource.loader.ResourceLoaderFactory;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyLoader;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.types.java.JavaTypeHelper;
import org.apache.tuscany.model.types.java.impl.JavaTypeHelperImpl;
import org.apache.tuscany.model.types.wsdl.WSDLTypeHelper;
import org.apache.tuscany.model.types.wsdl.impl.WSDLTypeHelperImpl;

/**
 */
public class AssemblyModelContextImpl implements AssemblyModelContext {

    private JavaTypeHelper javaTypeHelper;
    private WSDLTypeHelper wsdlTypeHelper;
    private AssemblyFactory assemblyFactory;
    private AssemblyLoader assemblyLoader;
    private ResourceLoader resourceLoader;

    /**
     * Constructor
     *
     * @param resourceLoader
     */
    public AssemblyModelContextImpl(ResourceLoader resourceLoader) {
        initialize(resourceLoader);
    }

    /**
     * Constructor
     */
    public AssemblyModelContextImpl() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        resourceLoader = ResourceLoaderFactory.getResourceLoader(classLoader);
        initialize(resourceLoader);
    }

    /**
     * Initialize
     *
     * @param resourceLoader
     */
    private void initialize(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        javaTypeHelper = new JavaTypeHelperImpl(this);
        wsdlTypeHelper = new WSDLTypeHelperImpl(this);
        assemblyFactory = new AssemblyFactoryImpl();
        assemblyLoader = new AssemblyLoaderImpl(this, resourceLoader);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getJavaTypeHelper()
     */
    public JavaTypeHelper getJavaTypeHelper() {
        return javaTypeHelper;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getWSDLTypeHelper()
     */
    public WSDLTypeHelper getWSDLTypeHelper() {
        return wsdlTypeHelper;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getAssemblyFactory()
     */
    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getAssemblyLoader()
     */
    public AssemblyLoader getAssemblyLoader() {
        return assemblyLoader;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelContext#getResourceLoader()
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
