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
package org.apache.tuscany.model.types.wsdl.impl;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.apache.tuscany.model.assembly.AssemblyConstants;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.types.wsdl.WSDLInterfaceType;
import org.apache.tuscany.model.types.wsdl.WSDLTypeHelper;

/**
 */
public class WSDLTypeHelperImpl implements WSDLTypeHelper {

    private AssemblyModelContext modelContext;

    /**
     * Constructor
     */
    public WSDLTypeHelperImpl(AssemblyModelContext modelContext) {
        super();
        this.modelContext = modelContext;
    }

    /**
     * Returns an InterfaceType for a WSDL portType
     *
     * @param fullyQualifiedClassName
     * @return
     */
    public WSDLInterfaceType getWSDLInterfaceType(String uri) {
        return getWSDLInterfaceType(modelContext.getAssemblyFactory().createQName(uri));
    }

    /**
     * Returns an InterfaceType for a WSDL portType
     *
     * @param fullyQualifiedClassName
     * @return
     */
    public WSDLInterfaceType getWSDLInterfaceType(QName qname) {

        ResourceSet resourceSet = (ResourceSet) modelContext.getAssemblyLoader();
        Resource resource = resourceSet.getResource(URI.createURI(qname.getNamespaceURI()), false);
        if (resource != null) {
            EPackage ePackage = (EPackage) resource.getContents().get(0);

            // Get the InterfaceType corresponding to the portType
            return (WSDLInterfaceType) ePackage.getEClassifier(AssemblyConstants.WSDL_INTERFACE_NAME_PREFIX + qname.getLocalPart());

        } else {
            return null;
        }
    }

    /**
     * Returns the WSDL definition associated with a Package.
     *
     * @param EPackage
     * @return
     */
    public Definition getWSDLDefinition(Object typePackage) {
        WSDLDefinitionAdapter adapter = (WSDLDefinitionAdapter) EcoreUtil.getAdapter(((EPackage) typePackage).eAdapters(), WSDLDefinitionAdapter.class);
        if (adapter != null) {
            return adapter.getWSDLDefinition();
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLTypeHelper#getWSDLDefinition(java.lang.String)
     */
    public Definition getWSDLDefinition(String ns) {

        ResourceSet resourceSet = (ResourceSet) modelContext.getAssemblyLoader();
        Resource resource = resourceSet.getResource(URI.createURI(ns), false);
        if (resource != null) {
            EPackage ePackage = (EPackage) resource.getContents().get(0);
            if (ePackage != null)
                return getWSDLDefinition(ePackage);
        }
        return null;
	}

}