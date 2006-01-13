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
package org.apache.tuscany.binding.axis.assembly.sdo.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.osoa.sca.model.WebServiceBinding;

import org.apache.tuscany.binding.axis.assembly.sdo.DocumentRoot;
import org.apache.tuscany.binding.axis.assembly.sdo.WebServiceAssemblyFactory;
import org.apache.tuscany.binding.axis.assembly.sdo.WebServiceAssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class WebServiceAssemblyFactoryImpl extends EFactoryImpl implements WebServiceAssemblyFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public WebServiceAssemblyFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case WebServiceAssemblyPackage.DOCUMENT_ROOT:
            return (EObject) createDocumentRoot();
        case WebServiceAssemblyPackage.WEB_SERVICE_BINDING:
            return (EObject) createWebServiceBinding();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public DocumentRoot createDocumentRoot() {
        DocumentRootImpl documentRoot = new DocumentRootImpl();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public WebServiceBinding createWebServiceBindingGen() {
        WebServiceBindingImpl webServiceBinding = new WebServiceBindingImpl();
        return webServiceBinding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public WebServiceAssemblyPackage getWebServiceAssemblyPackage() {
        return (WebServiceAssemblyPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @deprecated
     */
    public static WebServiceAssemblyPackage getPackage() {
        return WebServiceAssemblyPackage.eINSTANCE;
    }

    /**
     * Custom code
     */

    private final org.apache.tuscany.binding.axis.assembly.WebServiceAssemblyFactory logicalModelFactory = new org.apache.tuscany.binding.axis.assembly.impl.WebServiceAssemblyFactoryImpl();

    /**
     * @see org.apache.tuscany.binding.axis.assembly.sdo.WebServiceAssemblyFactory#createWebServiceBinding()
     */
    public WebServiceBinding createWebServiceBinding() {
        return (WebServiceBinding) logicalModelFactory.createWebServiceBinding();
    }

} //WebServiceAssemblyFactoryImpl
