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
package org.apache.tuscany.model.assembly.sdo.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.osoa.sca.model.ComponentType;
import org.osoa.sca.model.DocumentRoot;
import org.osoa.sca.model.Module;
import org.osoa.sca.model.ModuleFragment;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;
import org.apache.tuscany.model.assembly.sdo.AssemblyResource;

/**
 */
public class AssemblyResourceImpl extends XMLResourceImpl implements AssemblyResource {

    /**
     * @param uri
     */
    public AssemblyResourceImpl(URI uri) {
        super(uri);
    }

    /**
     * Returns the document root
     *
     * @return
     */
    private DocumentRoot getDocumentRoot() {
        return (DocumentRoot) EcoreUtil.getObjectByType(getContents(), AssemblyPackage.eINSTANCE.getDocumentRoot());
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyResource#getModuleElement()
     */
    public Module getModuleElement() {
        DocumentRoot documentRoot = getDocumentRoot();
        if (documentRoot != null)
            return documentRoot.getModule();
        else
            return null;
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyResource#getModuleFragmentElement()
     */
    public ModuleFragment getModuleFragmentElement() {
        DocumentRoot documentRoot = getDocumentRoot();
        if (documentRoot != null)
            return documentRoot.getModuleFragment();
        else
            return null;
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyResource#getComponentTypeElement()
     */
    public ComponentType getComponentTypeElement() {
        DocumentRoot documentRoot = getDocumentRoot();
        if (documentRoot != null)
            return documentRoot.getComponentType();
        else
            return null;
    }
}
