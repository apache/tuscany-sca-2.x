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
package org.apache.tuscany.container.js.assembly.sdo.impl;

import org.apache.tuscany.container.js.assembly.sdo.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JavaScriptAssemblyFactoryImpl extends EFactoryImpl implements JavaScriptAssemblyFactory {
	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JavaScriptAssemblyFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case JavaScriptAssemblyPackage.DOCUMENT_ROOT: return (EObject)createDocumentRoot();
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION: return (EObject)createJavaScriptImplementation();
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
	public JavaScriptImplementation createJavaScriptImplementationGen() {
		JavaScriptImplementationImpl javaScriptImplementation = new JavaScriptImplementationImpl();
		return javaScriptImplementation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JavaScriptAssemblyPackage getJavaScriptAssemblyPackage() {
		return (JavaScriptAssemblyPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static JavaScriptAssemblyPackage getPackage() {
		return JavaScriptAssemblyPackage.eINSTANCE;
	}

    /**
     * Custom code
     */

    private final org.apache.tuscany.container.js.assembly.JavaScriptAssemblyFactory logicalModelFactory = new org.apache.tuscany.container.js.assembly.impl.JavaScriptAssemblyFactoryImpl();

    /**
     * @see org.apache.tuscany.container.java.assembly.sdo.sdo.JavaAssemblyFactory#createJavaImplementation()
     */
    public JavaScriptImplementation createJavaScriptImplementation() {
        return (JavaScriptImplementation) logicalModelFactory.createJavaScriptImplementation();
    }

} //JavaScriptAssemblyFactoryImpl
