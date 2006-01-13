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
package org.apache.tuscany.core.system.assembly.sdo.impl;

import org.apache.tuscany.core.system.assembly.sdo.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.apache.tuscany.core.system.assembly.sdo.DocumentRoot;
import org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage;
import org.apache.tuscany.core.system.assembly.sdo.SystemImplementation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SystemAssemblyFactoryImpl extends EFactoryImpl implements SystemAssemblyFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SystemAssemblyFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case SystemAssemblyPackage.DOCUMENT_ROOT: return (EObject)createDocumentRoot();
            case SystemAssemblyPackage.SYSTEM_BINDING: return (EObject)createSystemBinding();
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION: return (EObject)createSystemImplementation();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DocumentRoot createDocumentRoot() {
        DocumentRootImpl documentRoot = new DocumentRootImpl();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SystemBinding createSystemBinding() {
        SystemBindingImpl systemBinding = new SystemBindingImpl();
        return systemBinding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static SystemAssemblyPackage getPackage() {
        return SystemAssemblyPackage.eINSTANCE;
    }

    /**
     * Custom code
     */

    private org.apache.tuscany.core.system.assembly.SystemAssemblyFactory logicalModelFactory = new org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl();

    /**
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyFactory#createSystemImplementation()
     */
    public SystemImplementation createSystemImplementation() {
        return (SystemImplementation) logicalModelFactory.createSystemImplementation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SystemAssemblyPackage getSystemAssemblyPackage() {
        return (SystemAssemblyPackage)getEPackage();
    }

} //ExtensionsAssemblyFactoryImpl
