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
package org.apache.tuscany.model.types.xsd.impl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

import org.apache.tuscany.model.util.ExtendedResourceSet;

/**
 * Extends XSDEcoreBuilder to use the given resource and package registry
 *
 */
public class XSD2SDOBuilder extends XSDEcoreBuilder {

    private ExtendedResourceSet resourceSet;
    private String baseNsURI;

    /**
     * Constructor
     *
     * @param resourceSet
     */
    public XSD2SDOBuilder(ExtendedResourceSet resourceSet, String baseNsURI) {
        super(resourceSet.getExtendedMetaData());
        this.resourceSet = resourceSet;
        this.baseNsURI = baseNsURI;
    }

    /**
     * @see org.eclipse.xsd.ecore.XSDEcoreBuilder#createResourceSet()
     */
    protected org.eclipse.emf.ecore.resource.ResourceSet createResourceSet() {
        return resourceSet;
    }

    /**
     * Returns the package with the given namespace uri.
     *
     * @param nsURI
     * @return
     */
    private EPackage getEPackage(String nsURI) {
        EPackage ePackage = (EPackage) targetNamespaceToEPackageMap.get(nsURI);
        if (ePackage != null)
            return ePackage;

        if (baseNsURI.equals(nsURI)) {
            return null;
        } else {
            EPackage.Registry registry = resourceSet.getPackageRegistry();
            ePackage = registry.getEPackage(nsURI);
            return ePackage;
        }
    }

    /**
     * @see org.eclipse.xsd.ecore.XSDEcoreBuilder#getEPackage(org.eclipse.xsd.XSDNamedComponent)
     */
    public EPackage getEPackage(XSDNamedComponent xsdNamedComponent) {
        EPackage ePackage = getEPackage(xsdNamedComponent.getTargetNamespace());
        if (ePackage != null)
            return ePackage;
        else
            return super.getEPackage(xsdNamedComponent);
    }

    /**
     * @see org.eclipse.xsd.ecore.XSDEcoreBuilder#getEDataType(org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public EDataType getEDataType(XSDSimpleTypeDefinition xsdSimpleTypeDefinition) {
        EDataType eDataType = (EDataType) xsdComponentToEModelElementMap.get(xsdSimpleTypeDefinition);
        if (eDataType != null)
            return eDataType;

        EPackage ePackage = getEPackage(xsdSimpleTypeDefinition.getTargetNamespace());
        if (ePackage != null) {
            String aliasName = getEcoreAttribute(xsdSimpleTypeDefinition, "name");
            if (aliasName == null) {
                aliasName = validName(xsdSimpleTypeDefinition.getAliasName(), true);
            }
            eDataType = (EDataType) ePackage.getEClassifier(aliasName);
            if (eDataType != null) {
                xsdComponentToEModelElementMap.put(xsdSimpleTypeDefinition, eDataType);
                return eDataType;
            }
        }

        return super.getEDataType(xsdSimpleTypeDefinition);
    }

    /**
     * @see org.eclipse.xsd.ecore.XSDEcoreBuilder#computeEClassifier(org.eclipse.xsd.XSDTypeDefinition)
     */
    protected EClassifier computeEClassifier(XSDTypeDefinition xsdTypeDefinition) {
        EClassifier eClassifier = (EClassifier) xsdComponentToEModelElementMap.get(xsdTypeDefinition);
        if (eClassifier != null)
            return eClassifier;

        EPackage ePackage = getEPackage(xsdTypeDefinition.getTargetNamespace());
        if (ePackage != null) {
            String aliasName = getEcoreAttribute(xsdTypeDefinition, "name");
            if (aliasName == null) {
                aliasName = validName(xsdTypeDefinition.getAliasName(), true);
            }
            eClassifier = ePackage.getEClassifier(aliasName);
            if (eClassifier != null) {
                xsdComponentToEModelElementMap.put(xsdTypeDefinition, eClassifier);
                return eClassifier;
            }
        }
        return super.computeEClassifier(xsdTypeDefinition);
    }

    /**
     * @see org.eclipse.xsd.ecore.XSDEcoreBuilder#setAnnotations(org.eclipse.emf.ecore.EModelElement, org.eclipse.xsd.XSDConcreteComponent)
     */
    protected void setAnnotations(EModelElement eModelElement, XSDConcreteComponent xsdComponent) {
        super.setAnnotations(eModelElement, xsdComponent);
	}
}