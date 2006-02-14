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
package org.apache.tuscany.container.js.assembly.sdo;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptAssemblyFactory
 * @model kind="package"
 * @generated
 */
public interface JavaScriptAssemblyPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "assembly";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.apache.tuscany/xmlns/js/0.9";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "js";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	JavaScriptAssemblyPackage eINSTANCE = org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptAssemblyPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.apache.tuscany.container.js.assembly.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.apache.tuscany.container.js.assembly.sdo.impl.DocumentRootImpl
	 * @see org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptAssemblyPackageImpl#getDocumentRoot()
	 * @generated
	 */
	int DOCUMENT_ROOT = 0;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>Implementation Js</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__IMPLEMENTATION_JS = 3;

	/**
	 * The number of structural features of the the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptImplementationImpl <em>Java Script Implementation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptImplementationImpl
	 * @see org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptAssemblyPackageImpl#getJavaScriptImplementation()
	 * @generated
	 */
	int JAVA_SCRIPT_IMPLEMENTATION = 1;

	/**
	 * The feature id for the '<em><b>Any</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_SCRIPT_IMPLEMENTATION__ANY = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Script File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_SCRIPT_IMPLEMENTATION__SCRIPT_FILE = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the the '<em>Java Script Implementation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_SCRIPT_IMPLEMENTATION_FEATURE_COUNT = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 3;


	/**
	 * Returns the meta object for class '{@link org.apache.tuscany.container.js.assembly.sdo.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document Root</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.DocumentRoot
	 * @generated
	 */
	EClass getDocumentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getMixed()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getXMLNSPrefixMap()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getXSISchemaLocation()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getImplementationJs <em>Implementation Js</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Implementation Js</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.DocumentRoot#getImplementationJs()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ImplementationJs();

	/**
	 * Returns the meta object for class '{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation <em>Java Script Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Java Script Implementation</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation
	 * @generated
	 */
	EClass getJavaScriptImplementation();

	/**
	 * Returns the meta object for the attribute list '{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getAny <em>Any</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getAny()
	 * @see #getJavaScriptImplementation()
	 * @generated
	 */
	EAttribute getJavaScriptImplementation_Any();

	/**
	 * Returns the meta object for the attribute '{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getScriptFile <em>Script File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Script File</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getScriptFile()
	 * @see #getJavaScriptImplementation()
	 * @generated
	 */
	EAttribute getJavaScriptImplementation_ScriptFile();

	/**
	 * Returns the meta object for the attribute list '{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getAnyAttribute <em>Any Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any Attribute</em>'.
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getAnyAttribute()
	 * @see #getJavaScriptImplementation()
	 * @generated
	 */
	EAttribute getJavaScriptImplementation_AnyAttribute();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	JavaScriptAssemblyFactory getJavaScriptAssemblyFactory();

} //JavaScriptAssemblyPackage
