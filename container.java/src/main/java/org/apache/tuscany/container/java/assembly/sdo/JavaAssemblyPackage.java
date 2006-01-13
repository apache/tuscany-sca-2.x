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
package org.apache.tuscany.container.java.assembly.sdo;

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
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @model kind="package"
 * @generated
 * @see JavaAssemblyFactory
 */
public interface JavaAssemblyPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "assembly";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http://org.apache.tuscany/xmlns/java/0.9";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "java";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    JavaAssemblyPackage eINSTANCE = org.apache.tuscany.container.java.assembly.sdo.impl.JavaAssemblyPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.container.java.assembly.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.container.java.assembly.sdo.impl.DocumentRootImpl
     * @see org.apache.tuscany.container.java.assembly.sdo.impl.JavaAssemblyPackageImpl#getDocumentRoot()
     */
    int DOCUMENT_ROOT = 0;

    /**
     * The feature id for the '<em><b>Mixed</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MIXED = 0;

    /**
     * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

    /**
     * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Implementation Java</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__IMPLEMENTATION_JAVA = 3;

    /**
     * The number of structural features of the the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.apache.tuscany.container.java.assembly.sdo.impl.JavaImplementationImpl <em>Java Implementation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.container.java.assembly.sdo.impl.JavaImplementationImpl
     * @see org.apache.tuscany.container.java.assembly.sdo.impl.JavaAssemblyPackageImpl#getJavaImplementation()
     */
    int JAVA_IMPLEMENTATION = 1;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_IMPLEMENTATION__ANY = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_IMPLEMENTATION__CLASS = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_IMPLEMENTATION__ANY_ATTRIBUTE = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Java Implementation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_IMPLEMENTATION_FEATURE_COUNT = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 3;


    /**
     * Returns the meta object for class '{@link DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Document Root</em>'.
     * @generated
     * @see DocumentRoot
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @generated
     * @see DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @generated
     * @see DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @generated
     * @see DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getImplementationJava <em>Implementation Java</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Implementation Java</em>'.
     * @generated
     * @see DocumentRoot#getImplementationJava()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_ImplementationJava();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.JavaImplementation <em>Java Implementation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Java Implementation</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaImplementation
     */
    EClass getJavaImplementation();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.JavaImplementation#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaImplementation#getAny()
     * @see #getJavaImplementation()
     */
    EAttribute getJavaImplementation_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.JavaImplementation#getClass_ <em>Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Class</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaImplementation#getClass_()
     * @see #getJavaImplementation()
     */
    EAttribute getJavaImplementation_Class();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.JavaImplementation#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaImplementation#getAnyAttribute()
     * @see #getJavaImplementation()
     */
    EAttribute getJavaImplementation_AnyAttribute();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	JavaAssemblyFactory getJavaAssemblyFactory();

} //AssemblyPackage
