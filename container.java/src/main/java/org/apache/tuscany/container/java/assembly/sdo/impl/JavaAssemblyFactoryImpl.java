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
package org.apache.tuscany.container.java.assembly.sdo.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.osoa.sca.model.JavaImplementation;

import org.apache.tuscany.container.java.assembly.sdo.DocumentRoot;
import org.apache.tuscany.container.java.assembly.sdo.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.sdo.JavaAssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class JavaAssemblyFactoryImpl extends EFactoryImpl implements JavaAssemblyFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public JavaAssemblyFactoryImpl() {
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
        case JavaAssemblyPackage.DOCUMENT_ROOT:
            return (EObject) createDocumentRoot();
        case JavaAssemblyPackage.JAVA_IMPLEMENTATION:
            return (EObject) createJavaImplementation();
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
    public JavaImplementation createJavaImplementationGen() {
        JavaImplementationImpl javaImplementation = new JavaImplementationImpl();
        return javaImplementation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @deprecated
     */
    public static JavaAssemblyPackage getPackage() {
        return JavaAssemblyPackage.eINSTANCE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public JavaAssemblyPackage getJavaAssemblyPackage() {
        return (JavaAssemblyPackage) getEPackage();
    }

    /**
     * Custom code
     */

    private final org.apache.tuscany.container.java.assembly.JavaAssemblyFactory logicalModelFactory = new org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl();

    /**
     * @see org.apache.tuscany.container.java.assembly.sdo.JavaAssemblyFactory#createJavaImplementation()
     */
    public JavaImplementation createJavaImplementation() {
        return (JavaImplementation) logicalModelFactory.createJavaImplementation();
    }

} //AssemblyFactoryImpl
