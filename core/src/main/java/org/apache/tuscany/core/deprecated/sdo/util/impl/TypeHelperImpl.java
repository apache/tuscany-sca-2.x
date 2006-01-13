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
package org.apache.tuscany.core.deprecated.sdo.util.impl;

import commonj.sdo.Type;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.sdo.util.SDOUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import org.apache.tuscany.model.config.ModelConfiguration;
import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.core.deprecated.sdo.util.TypeHelper;

public class TypeHelperImpl implements TypeHelper {
    private ConfiguredResourceSet configuredResourceSet;

    /**
     * Constructor
     */
    protected TypeHelperImpl(ConfiguredResourceSet resourceSet) {
        configuredResourceSet = resourceSet;
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.TypeHelper#getType(java.lang.String, java.lang.String)
     */
    public Type getType(String uri, String typeName) {
        // Lookup the EPackage
        EPackage.Registry packageRegistry = configuredResourceSet.getPackageRegistry();
        EPackage ePackage = packageRegistry.getEPackage(uri);
        if (ePackage == null) {
            throw new IllegalArgumentException("Cannot find package for " + uri);
        }

        // First try to find EClassifier
        EClassifier eClassifier = ePackage.getEClassifier(typeName);
        ExtendedMetaData extendedMetaData = configuredResourceSet.getExtendedMetaData();
        if (eClassifier == null) {
            // Try to match by XML name
            eClassifier = extendedMetaData.getType(ePackage, typeName);
        }

        if (eClassifier == null) {
            // Try to match by element name
            EStructuralFeature element = extendedMetaData.getElement(uri, typeName);
            if (element != null) {
                eClassifier = element.getEType();
            }
        }

        if (eClassifier == null) {
            throw new IllegalArgumentException("Cannot find Type for " + typeName + " in uri " + uri);
        }
        if (!(eClassifier instanceof EClass)) {
            throw new IllegalArgumentException(typeName + " is not the name of a valid DataObject Type");
        }

        return SDOUtil.adaptType(eClassifier);
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.TypeHelper#getType(java.lang.Class)
     */
    public Type getType(Class interfaceClass) {
        ModelConfiguration modelConfiguration = configuredResourceSet.getModelConfiguration();
        EClass eClass = modelConfiguration.getEClass(interfaceClass);
        if (eClass != null) {
            return (Type) SDOUtil.getType(eClass);
        } else {
            throw new IllegalArgumentException(interfaceClass.getName() + " does not have a corresponding SDO Type");
        }
    }

}