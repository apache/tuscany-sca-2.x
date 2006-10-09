/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.example.creditscore.doclit;

import org.apache.tuscany.sdo.impl.SDOPackageImpl;

import org.apache.tuscany.sdo.model.impl.ModelPackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

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
 * @see org.example.creditscore.doclit.DoclitFactory
 * @generated
 */
public class DoclitPackage extends EPackageImpl
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String eNAME = "doclit";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String eNS_URI = "http://www.example.org/creditscore/doclit/";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String eNS_PREFIX = "doclit";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final DoclitPackage eINSTANCE = org.example.creditscore.doclit.DoclitPackage.init();

  /**
   * The meta object id for the '{@link org.example.creditscore.doclit.CreditReport <em>Credit Report</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.example.creditscore.doclit.CreditReport
   * @see org.example.creditscore.doclit.DoclitPackage#getCreditReport()
   * @generated
   */
  public static final int CREDIT_REPORT = 0;

  /**
   * The feature id for the '<em><b>Score</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int CREDIT_REPORT__SCORE = 0;

  /**
   * The number of structural features of the '<em>Credit Report</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int CREDIT_REPORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.example.creditscore.doclit.Customer <em>Customer</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.example.creditscore.doclit.Customer
   * @see org.example.creditscore.doclit.DoclitPackage#getCustomer()
   * @generated
   */
  public static final int CUSTOMER = 1;

  /**
   * The feature id for the '<em><b>Ssn</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int CUSTOMER__SSN = 0;

  /**
   * The feature id for the '<em><b>First Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int CUSTOMER__FIRST_NAME = 1;

  /**
   * The feature id for the '<em><b>Last Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int CUSTOMER__LAST_NAME = 2;

  /**
   * The number of structural features of the '<em>Customer</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int CUSTOMER_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.example.creditscore.doclit.DocumentRoot <em>Document Root</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.example.creditscore.doclit.DocumentRoot
   * @see org.example.creditscore.doclit.DoclitPackage#getDocumentRoot()
   * @generated
   */
  public static final int DOCUMENT_ROOT = 2;

  /**
   * The feature id for the '<em><b>Mixed</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__MIXED = 0;

  /**
   * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

  /**
   * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

  /**
   * The feature id for the '<em><b>Get Credit Score Request</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__GET_CREDIT_SCORE_REQUEST = 3;

  /**
   * The feature id for the '<em><b>Get Credit Score Response</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__GET_CREDIT_SCORE_RESPONSE = 4;

  /**
   * The number of structural features of the '<em>Document Root</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT_FEATURE_COUNT = 5;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass creditReportEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass customerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass documentRootEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.example.creditscore.doclit.DoclitPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private DoclitPackage()
  {
    super(eNS_URI, ((EFactory)DoclitFactory.INSTANCE));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this
   * model, and for any others upon which it depends.  Simple
   * dependencies are satisfied by calling this method on all
   * dependent packages before doing anything else.  This method drives
   * initialization for interdependent packages directly, in parallel
   * with this package, itself.
   * <p>Of this package and its interdependencies, all packages which
   * have not yet been registered by their URI values are first created
   * and registered.  The packages are then initialized in two steps:
   * meta-model objects for all of the packages are created before any
   * are initialized, since one package's meta-model objects may refer to
   * those of another.
   * <p>Invocation of this method will not affect any packages that have
   * already been initialized.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static DoclitPackage init()
  {
    if (isInited) return (DoclitPackage)EPackage.Registry.INSTANCE.getEPackage(DoclitPackage.eNS_URI);

    // Obtain or create and register package
    DoclitPackage theDoclitPackage = (DoclitPackage)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof DoclitPackage ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new DoclitPackage());

    isInited = true;

    // Initialize simple dependencies
    SDOPackageImpl.eINSTANCE.eClass();
    ModelPackageImpl.eINSTANCE.eClass();

    // Create package meta-data objects
    theDoclitPackage.createPackageContents();

    // Initialize created meta-data
    theDoclitPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theDoclitPackage.freeze();

    return theDoclitPackage;
  }


  /**
   * Returns the meta object for class '{@link org.example.creditscore.doclit.CreditReport <em>Credit Report</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Credit Report</em>'.
   * @see org.example.creditscore.doclit.CreditReport
   * @generated
   */
  public EClass getCreditReport()
  {
    return creditReportEClass;
  }

  /**
   * Returns the meta object for the attribute '{@link org.example.creditscore.doclit.CreditReport#getScore <em>Score</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Score</em>'.
   * @see org.example.creditscore.doclit.CreditReport#getScore()
   * @see #getCreditReport()
   * @generated
   */
  public EAttribute getCreditReport_Score()
  {
    return (EAttribute)creditReportEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for class '{@link org.example.creditscore.doclit.Customer <em>Customer</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Customer</em>'.
   * @see org.example.creditscore.doclit.Customer
   * @generated
   */
  public EClass getCustomer()
  {
    return customerEClass;
  }

  /**
   * Returns the meta object for the attribute '{@link org.example.creditscore.doclit.Customer#getSsn <em>Ssn</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ssn</em>'.
   * @see org.example.creditscore.doclit.Customer#getSsn()
   * @see #getCustomer()
   * @generated
   */
  public EAttribute getCustomer_Ssn()
  {
    return (EAttribute)customerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.example.creditscore.doclit.Customer#getFirstName <em>First Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>First Name</em>'.
   * @see org.example.creditscore.doclit.Customer#getFirstName()
   * @see #getCustomer()
   * @generated
   */
  public EAttribute getCustomer_FirstName()
  {
    return (EAttribute)customerEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute '{@link org.example.creditscore.doclit.Customer#getLastName <em>Last Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Last Name</em>'.
   * @see org.example.creditscore.doclit.Customer#getLastName()
   * @see #getCustomer()
   * @generated
   */
  public EAttribute getCustomer_LastName()
  {
    return (EAttribute)customerEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.ecore.EObject <em>Document Root</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Document Root</em>'.
   * @see org.eclipse.emf.ecore.EObject
   * @generated
   */
  public EClass getDocumentRoot()
  {
    return documentRootEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.ecore.EObject#getMixed <em>Mixed</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Mixed</em>'.
   * @see org.eclipse.emf.ecore.EObject#getMixed()
   * @see #getDocumentRoot()
   * @generated
   */
  public EAttribute getDocumentRoot_Mixed()
  {
    return (EAttribute)documentRootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.ecore.EObject#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
   * @see org.eclipse.emf.ecore.EObject#getXMLNSPrefixMap()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_XMLNSPrefixMap()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.ecore.EObject#getXSISchemaLocation <em>XSI Schema Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>XSI Schema Location</em>'.
   * @see org.eclipse.emf.ecore.EObject#getXSISchemaLocation()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_XSISchemaLocation()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.ecore.EObject#getGetCreditScoreRequest <em>Get Credit Score Request</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Get Credit Score Request</em>'.
   * @see org.eclipse.emf.ecore.EObject#getGetCreditScoreRequest()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_GetCreditScoreRequest()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.ecore.EObject#getGetCreditScoreResponse <em>Get Credit Score Response</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Get Credit Score Response</em>'.
   * @see org.eclipse.emf.ecore.EObject#getGetCreditScoreResponse()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_GetCreditScoreResponse()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  public DoclitFactory getDoclitFactory()
  {
    return (DoclitFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    creditReportEClass = createEClass(CREDIT_REPORT);
    createEAttribute(creditReportEClass, CREDIT_REPORT__SCORE);

    customerEClass = createEClass(CUSTOMER);
    createEAttribute(customerEClass, CUSTOMER__SSN);
    createEAttribute(customerEClass, CUSTOMER__FIRST_NAME);
    createEAttribute(customerEClass, CUSTOMER__LAST_NAME);

    documentRootEClass = createEClass(DOCUMENT_ROOT);
    createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
    createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
    createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
    createEReference(documentRootEClass, DOCUMENT_ROOT__GET_CREDIT_SCORE_REQUEST);
    createEReference(documentRootEClass, DOCUMENT_ROOT__GET_CREDIT_SCORE_RESPONSE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    ModelPackageImpl theModelPackageImpl = (ModelPackageImpl)EPackage.Registry.INSTANCE.getEPackage(ModelPackageImpl.eNS_URI);

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(creditReportEClass, CreditReport.class, "CreditReport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCreditReport_Score(), theModelPackageImpl.getInt(), "score", null, 1, 1, CreditReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(customerEClass, Customer.class, "Customer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCustomer_Ssn(), theModelPackageImpl.getString(), "ssn", null, 1, 1, Customer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCustomer_FirstName(), theModelPackageImpl.getString(), "firstName", null, 1, 1, Customer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCustomer_LastName(), theModelPackageImpl.getString(), "lastName", null, 1, 1, Customer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(documentRootEClass, null, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_GetCreditScoreRequest(), this.getCustomer(), null, "getCreditScoreRequest", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_GetCreditScoreResponse(), this.getCreditReport(), null, "getCreditScoreResponse", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
    addAnnotation
      (creditReportEClass, 
       source, 
       new String[] 
       {
       "name", "CreditReport",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getCreditReport_Score(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "score"
       });		
    addAnnotation
      (customerEClass, 
       source, 
       new String[] 
       {
       "name", "Customer",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getCustomer_Ssn(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "ssn"
       });		
    addAnnotation
      (getCustomer_FirstName(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "firstName"
       });		
    addAnnotation
      (getCustomer_LastName(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "lastName"
       });		
    addAnnotation
      (documentRootEClass, 
       source, 
       new String[] 
       {
       "name", "",
       "kind", "mixed"
       });		
    addAnnotation
      (getDocumentRoot_Mixed(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "name", ":mixed"
       });		
    addAnnotation
      (getDocumentRoot_XMLNSPrefixMap(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "xmlns:prefix"
       });		
    addAnnotation
      (getDocumentRoot_XSISchemaLocation(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "xsi:schemaLocation"
       });		
    addAnnotation
      (getDocumentRoot_GetCreditScoreRequest(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "getCreditScoreRequest",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_GetCreditScoreResponse(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "getCreditScoreResponse",
       "namespace", "##targetNamespace"
       });
  }

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  public interface Literals
  {
    /**
     * The meta object literal for the '{@link org.example.creditscore.doclit.CreditReport <em>Credit Report</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.example.creditscore.doclit.CreditReport
     * @see org.example.creditscore.doclit.DoclitPackage#getCreditReport()
     * @generated
     */
    public static final EClass CREDIT_REPORT = eINSTANCE.getCreditReport();

    /**
     * The meta object literal for the '<em><b>Score</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute CREDIT_REPORT__SCORE = eINSTANCE.getCreditReport_Score();

    /**
     * The meta object literal for the '{@link org.example.creditscore.doclit.Customer <em>Customer</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.example.creditscore.doclit.Customer
     * @see org.example.creditscore.doclit.DoclitPackage#getCustomer()
     * @generated
     */
    public static final EClass CUSTOMER = eINSTANCE.getCustomer();

    /**
     * The meta object literal for the '<em><b>Ssn</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute CUSTOMER__SSN = eINSTANCE.getCustomer_Ssn();

    /**
     * The meta object literal for the '<em><b>First Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute CUSTOMER__FIRST_NAME = eINSTANCE.getCustomer_FirstName();

    /**
     * The meta object literal for the '<em><b>Last Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute CUSTOMER__LAST_NAME = eINSTANCE.getCustomer_LastName();

    /**
     * The meta object literal for the '{@link org.example.creditscore.doclit.DocumentRoot <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.example.creditscore.doclit.DocumentRoot
     * @see org.example.creditscore.doclit.DoclitPackage#getDocumentRoot()
     * @generated
     */
    public static final EClass DOCUMENT_ROOT = eINSTANCE.getDocumentRoot();

    /**
     * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute DOCUMENT_ROOT__MIXED = eINSTANCE.getDocumentRoot_Mixed();

    /**
     * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getDocumentRoot_XMLNSPrefixMap();

    /**
     * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getDocumentRoot_XSISchemaLocation();

    /**
     * The meta object literal for the '<em><b>Get Credit Score Request</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__GET_CREDIT_SCORE_REQUEST = eINSTANCE.getDocumentRoot_GetCreditScoreRequest();

    /**
     * The meta object literal for the '<em><b>Get Credit Score Response</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__GET_CREDIT_SCORE_RESPONSE = eINSTANCE.getDocumentRoot_GetCreditScoreResponse();

  }

} //DoclitPackage
