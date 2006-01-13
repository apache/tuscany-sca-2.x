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
package org.apache.tuscany.core.message.sdo;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see AnyObjectFactory
 */
public interface AnyObjectPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "object";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http://org.apache.tuscany/xmlns/anyobject/v0.0.1/";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "obj";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    AnyObjectPackage eINSTANCE = org.apache.tuscany.core.message.sdo.impl.AnyObjectPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.AnyObjectImpl <em>Any Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.AnyObjectImpl
     * @see org.apache.tuscany.core.message.sdo.impl.AnyObjectPackageImpl#getAnyObject()
     */
    int ANY_OBJECT = 0;

    /**
     * The feature id for the '<em><b>Object</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ANY_OBJECT__OBJECT = 0;

    /**
     * The number of structural features of the the '<em>Any Object</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ANY_OBJECT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.AnyObjectDocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.AnyObjectDocumentRootImpl
     * @see org.apache.tuscany.core.message.sdo.impl.AnyObjectPackageImpl#getDocumentRoot()
     */
    int DOCUMENT_ROOT = 1;

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
     * The feature id for the '<em><b>Any Object</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ANY_OBJECT = 3;

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
     * The meta object id for the '<em>Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see Object
     * @see org.apache.tuscany.core.message.sdo.impl.AnyObjectPackageImpl#getObject()
     */
    int OBJECT = 2;


    /**
     * Returns the meta object for class '{@link AnyObject <em>Any Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Any Object</em>'.
     * @generated
     * @see AnyObject
     */
    EClass getAnyObject();

    /**
     * Returns the meta object for the attribute '{@link AnyObject#getObject <em>Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Object</em>'.
     * @generated
     * @see AnyObject#getObject()
     * @see #getAnyObject()
     */
    EAttribute getAnyObject_Object();

    /**
     * Returns the meta object for class '{@link AnyObjectDocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Document Root</em>'.
     * @generated
     * @see AnyObjectDocumentRoot
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link AnyObjectDocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @generated
     * @see AnyObjectDocumentRoot#getMixed()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link AnyObjectDocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @generated
     * @see AnyObjectDocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link AnyObjectDocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @generated
     * @see AnyObjectDocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link AnyObjectDocumentRoot#getAnyObject <em>Any Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Any Object</em>'.
     * @generated
     * @see AnyObjectDocumentRoot#getAnyObject()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_AnyObject();

    /**
     * Returns the meta object for data type '{@link Object <em>Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>Object</em>'.
     * @model instanceClass="java.lang.Object"
     * extendedMetaData="name='Object'"
     * @generated
     * @see Object
     */
    EDataType getObject();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	AnyObjectFactory getAnyObjectFactory();

} //AnyObjectPackage
