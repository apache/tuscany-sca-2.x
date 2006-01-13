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
package org.apache.tuscany.model.types.java.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import org.apache.tuscany.model.assembly.AssemblyLoader;

/**
 */
public class JavaResourceImpl extends ResourceImpl {

    /**
     * Constructor
     */
    public JavaResourceImpl(URI uri) {
        super(uri);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#load(java.util.Map)
     */
    public void load(Map options) throws IOException {
        if (!isLoaded) {
            load(null, options);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#doLoad(java.io.InputStream, java.util.Map)
     */
    protected void doLoad(InputStream inputStream, Map options) throws IOException {

        // Create a new JavaPackage
        String packageName = uri.host();
        AssemblyLoader loader = (AssemblyLoader) getResourceSet();
        EPackage javaPackage = new JavaPackageImpl(loader.getAssemblyModelContext(), packageName);
        getContents().add(javaPackage);

        // Add it to the package registry
        getResourceSet().getPackageRegistry().put(uri.toString(), javaPackage);
    }

}
