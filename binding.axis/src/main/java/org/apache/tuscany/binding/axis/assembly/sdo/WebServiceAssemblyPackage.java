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
package org.apache.tuscany.binding.axis.assembly.sdo;

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
 * @see WebServiceAssemblyFactory
 */
public interface WebServiceAssemblyPackage extends EPackage {
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
    String eNS_URI = "http://org.apache.tuscany/xmlns/webservice/0.9";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "ws";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    WebServiceAssemblyPackage eINSTANCE = org.apache.tuscany.binding.axis.assembly.sdo.impl.WebServiceAssemblyPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.binding.axis.assembly.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.binding.axis.assembly.sdo.impl.DocumentRootImpl
     * @see org.apache.tuscany.binding.axis.assembly.sdo.impl.WebServiceAssemblyPackageImpl#getDocumentRoot()
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
     * The feature id for the '<em><b>Binding Ws</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__BINDING_WS = 3;

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
     * The meta object id for the '{@link org.apache.tuscany.binding.axis.assembly.sdo.impl.WebServiceBindingImpl <em>Web Service Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.binding.axis.assembly.sdo.impl.WebServiceBindingImpl
     * @see org.apache.tuscany.binding.axis.assembly.sdo.impl.WebServiceAssemblyPackageImpl#getWebServiceBinding()
     */
    int WEB_SERVICE_BINDING = 1;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WEB_SERVICE_BINDING__URI = AssemblyPackage.BINDING__URI;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WEB_SERVICE_BINDING__ANY = AssemblyPackage.BINDING_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Port</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WEB_SERVICE_BINDING__PORT = AssemblyPackage.BINDING_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WEB_SERVICE_BINDING__ANY_ATTRIBUTE = AssemblyPackage.BINDING_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Web Service Binding</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WEB_SERVICE_BINDING_FEATURE_COUNT = AssemblyPackage.BINDING_FEATURE_COUNT + 3;


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
     * Returns the meta object for the containment reference '{@link DocumentRoot#getBindingWs <em>Binding Ws</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Binding Ws</em>'.
     * @generated
     * @see DocumentRoot#getBindingWs()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_BindingWs();

    /**
     * Returns the meta object for class '{@link org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding <em>Web Service Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Web Service Binding</em>'.
     * @generated
     * @see org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding
     */
    EClass getWebServiceBinding();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding#getAny()
     * @see #getWebServiceBinding()
     */
    EAttribute getWebServiceBinding_Any();

    /**
     * Returns the meta object for the attribute '{@link org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding#getPort <em>Port</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Port</em>'.
     * @generated
     * @see org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding#getPort()
     * @see #getWebServiceBinding()
     */
    EAttribute getWebServiceBinding_Port();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.apache.tuscany.binding.axis.assembly.sdo.WebServiceBinding#getAnyAttribute()
     * @see #getWebServiceBinding()
     */
    EAttribute getWebServiceBinding_AnyAttribute();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    WebServiceAssemblyFactory getWebServiceAssemblyFactory();

} //WebServiceAssemblyPackage
