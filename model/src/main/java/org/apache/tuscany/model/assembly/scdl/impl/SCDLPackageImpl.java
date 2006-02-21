/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import org.apache.tuscany.model.assembly.scdl.Binding;
import org.apache.tuscany.model.assembly.scdl.Component;
import org.apache.tuscany.model.assembly.scdl.ComponentType;
import org.apache.tuscany.model.assembly.scdl.DocumentRoot;
import org.apache.tuscany.model.assembly.scdl.EntryPoint;
import org.apache.tuscany.model.assembly.scdl.ExternalService;
import org.apache.tuscany.model.assembly.scdl.Implementation;
import org.apache.tuscany.model.assembly.scdl.Interface;
import org.apache.tuscany.model.assembly.scdl.JavaImplementation;
import org.apache.tuscany.model.assembly.scdl.JavaInterface;
import org.apache.tuscany.model.assembly.scdl.Module;
import org.apache.tuscany.model.assembly.scdl.ModuleComponent;
import org.apache.tuscany.model.assembly.scdl.ModuleFragment;
import org.apache.tuscany.model.assembly.scdl.ModuleWire;
import org.apache.tuscany.model.assembly.scdl.Multiplicity;
import org.apache.tuscany.model.assembly.scdl.OverrideOptions;
import org.apache.tuscany.model.assembly.scdl.Property;
import org.apache.tuscany.model.assembly.scdl.PropertyValues;
import org.apache.tuscany.model.assembly.scdl.Reference;
import org.apache.tuscany.model.assembly.scdl.ReferenceValues;
import org.apache.tuscany.model.assembly.scdl.SCABinding;
import org.apache.tuscany.model.assembly.scdl.SCDLFactory;
import org.apache.tuscany.model.assembly.scdl.Service;
import org.apache.tuscany.model.assembly.scdl.Subsystem;
import org.apache.tuscany.model.assembly.scdl.SystemWire;
import org.apache.tuscany.model.assembly.scdl.WSDLPortType;
import org.apache.tuscany.model.assembly.scdl.WebServiceBinding;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

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
 * @see org.apache.tuscany.model.assembly.scdl.SCDLFactory
 * @generated
 */
public class SCDLPackageImpl extends EPackageImpl
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String eNAME = "scdl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String eNS_URI = "http://www.osoa.org/xmlns/sca/0.9";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String eNS_PREFIX = "_0";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final SCDLPackageImpl eINSTANCE = org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl.init();

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.BindingImpl <em>Binding</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.BindingImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getBinding()
   * @generated
   */
  public static final int BINDING = 0;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int BINDING__URI = 0;

  /**
   * The number of structural features of the '<em>Binding</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int BINDING_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl <em>Component</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getComponent()
   * @generated
   */
  public static final int COMPONENT = 1;

  /**
   * The feature id for the '<em><b>Implementation Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__IMPLEMENTATION_GROUP = 0;

  /**
   * The feature id for the '<em><b>Implementation</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__IMPLEMENTATION = 1;

  /**
   * The feature id for the '<em><b>Properties</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__PROPERTIES = 2;

  /**
   * The feature id for the '<em><b>References</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__REFERENCES = 3;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__ANY = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__NAME = 5;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT__ANY_ATTRIBUTE = 6;

  /**
   * The number of structural features of the '<em>Component</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl <em>Component Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getComponentType()
   * @generated
   */
  public static final int COMPONENT_TYPE = 2;

  /**
   * The feature id for the '<em><b>Service</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_TYPE__SERVICE = 0;

  /**
   * The feature id for the '<em><b>Reference</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_TYPE__REFERENCE = 1;

  /**
   * The feature id for the '<em><b>Property</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_TYPE__PROPERTY = 2;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_TYPE__ANY = 3;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_TYPE__ANY_ATTRIBUTE = 4;

  /**
   * The number of structural features of the '<em>Component Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int COMPONENT_TYPE_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl <em>Document Root</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getDocumentRoot()
   * @generated
   */
  public static final int DOCUMENT_ROOT = 3;

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
   * The feature id for the '<em><b>Binding</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__BINDING = 3;

  /**
   * The feature id for the '<em><b>Binding Sca</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__BINDING_SCA = 4;

  /**
   * The feature id for the '<em><b>Binding Ws</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__BINDING_WS = 5;

  /**
   * The feature id for the '<em><b>Component Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__COMPONENT_TYPE = 6;

  /**
   * The feature id for the '<em><b>Implementation</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__IMPLEMENTATION = 7;

  /**
   * The feature id for the '<em><b>Implementation Java</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__IMPLEMENTATION_JAVA = 8;

  /**
   * The feature id for the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__INTERFACE = 9;

  /**
   * The feature id for the '<em><b>Interface Java</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__INTERFACE_JAVA = 10;

  /**
   * The feature id for the '<em><b>Interface Wsdl</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__INTERFACE_WSDL = 11;

  /**
   * The feature id for the '<em><b>Module</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__MODULE = 12;

  /**
   * The feature id for the '<em><b>Module Fragment</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__MODULE_FRAGMENT = 13;

  /**
   * The feature id for the '<em><b>Source</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__SOURCE = 14;

  /**
   * The feature id for the '<em><b>Source Epr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__SOURCE_EPR = 15;

  /**
   * The feature id for the '<em><b>Source Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__SOURCE_URI = 16;

  /**
   * The feature id for the '<em><b>Subsystem</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__SUBSYSTEM = 17;

  /**
   * The feature id for the '<em><b>Target</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__TARGET = 18;

  /**
   * The feature id for the '<em><b>Target Epr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__TARGET_EPR = 19;

  /**
   * The feature id for the '<em><b>Target Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT__TARGET_URI = 20;

  /**
   * The number of structural features of the '<em>Document Root</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int DOCUMENT_ROOT_FEATURE_COUNT = 21;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl <em>Entry Point</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getEntryPoint()
   * @generated
   */
  public static final int ENTRY_POINT = 4;

  /**
   * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__INTERFACE_GROUP = 0;

  /**
   * The feature id for the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__INTERFACE = 1;

  /**
   * The feature id for the '<em><b>Binding Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__BINDING_GROUP = 2;

  /**
   * The feature id for the '<em><b>Binding</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__BINDING = 3;

  /**
   * The feature id for the '<em><b>Reference</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__REFERENCE = 4;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__ANY = 5;

  /**
   * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__MULTIPLICITY = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__NAME = 7;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT__ANY_ATTRIBUTE = 8;

  /**
   * The number of structural features of the '<em>Entry Point</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int ENTRY_POINT_FEATURE_COUNT = 9;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl <em>External Service</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getExternalService()
   * @generated
   */
  public static final int EXTERNAL_SERVICE = 5;

  /**
   * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__INTERFACE_GROUP = 0;

  /**
   * The feature id for the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__INTERFACE = 1;

  /**
   * The feature id for the '<em><b>Binding Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__BINDING_GROUP = 2;

  /**
   * The feature id for the '<em><b>Binding</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__BINDING = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__NAME = 4;

  /**
   * The feature id for the '<em><b>Overridable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__OVERRIDABLE = 5;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE__ANY_ATTRIBUTE = 6;

  /**
   * The number of structural features of the '<em>External Service</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int EXTERNAL_SERVICE_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ImplementationImpl <em>Implementation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ImplementationImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getImplementation()
   * @generated
   */
  public static final int IMPLEMENTATION = 6;

  /**
   * The number of structural features of the '<em>Implementation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int IMPLEMENTATION_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.InterfaceImpl <em>Interface</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.InterfaceImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getInterface()
   * @generated
   */
  public static final int INTERFACE = 7;

  /**
   * The number of structural features of the '<em>Interface</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int INTERFACE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.JavaImplementationImpl <em>Java Implementation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.JavaImplementationImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getJavaImplementation()
   * @generated
   */
  public static final int JAVA_IMPLEMENTATION = 8;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_IMPLEMENTATION__ANY = IMPLEMENTATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Class</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_IMPLEMENTATION__CLASS = IMPLEMENTATION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_IMPLEMENTATION__ANY_ATTRIBUTE = IMPLEMENTATION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Java Implementation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_IMPLEMENTATION_FEATURE_COUNT = IMPLEMENTATION_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.JavaInterfaceImpl <em>Java Interface</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.JavaInterfaceImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getJavaInterface()
   * @generated
   */
  public static final int JAVA_INTERFACE = 9;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_INTERFACE__ANY = INTERFACE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Callback Interface</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_INTERFACE__CALLBACK_INTERFACE = INTERFACE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Interface</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_INTERFACE__INTERFACE = INTERFACE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_INTERFACE__ANY_ATTRIBUTE = INTERFACE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Java Interface</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int JAVA_INTERFACE_FEATURE_COUNT = INTERFACE_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleFragmentImpl <em>Module Fragment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleFragmentImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModuleFragment()
   * @generated
   */
  public static final int MODULE_FRAGMENT = 12;

  /**
   * The feature id for the '<em><b>Entry Point</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__ENTRY_POINT = 0;

  /**
   * The feature id for the '<em><b>Component</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__COMPONENT = 1;

  /**
   * The feature id for the '<em><b>External Service</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__EXTERNAL_SERVICE = 2;

  /**
   * The feature id for the '<em><b>Wire</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__WIRE = 3;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__ANY = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__NAME = 5;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT__ANY_ATTRIBUTE = 6;

  /**
   * The number of structural features of the '<em>Module Fragment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FRAGMENT_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleImpl <em>Module</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModule()
   * @generated
   */
  public static final int MODULE = 10;

  /**
   * The feature id for the '<em><b>Entry Point</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__ENTRY_POINT = MODULE_FRAGMENT__ENTRY_POINT;

  /**
   * The feature id for the '<em><b>Component</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__COMPONENT = MODULE_FRAGMENT__COMPONENT;

  /**
   * The feature id for the '<em><b>External Service</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__EXTERNAL_SERVICE = MODULE_FRAGMENT__EXTERNAL_SERVICE;

  /**
   * The feature id for the '<em><b>Wire</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__WIRE = MODULE_FRAGMENT__WIRE;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__ANY = MODULE_FRAGMENT__ANY;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__NAME = MODULE_FRAGMENT__NAME;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE__ANY_ATTRIBUTE = MODULE_FRAGMENT__ANY_ATTRIBUTE;

  /**
   * The number of structural features of the '<em>Module</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_FEATURE_COUNT = MODULE_FRAGMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleComponentImpl <em>Module Component</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleComponentImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModuleComponent()
   * @generated
   */
  public static final int MODULE_COMPONENT = 11;

  /**
   * The feature id for the '<em><b>Properties</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__PROPERTIES = 0;

  /**
   * The feature id for the '<em><b>References</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__REFERENCES = 1;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__ANY = 2;

  /**
   * The feature id for the '<em><b>Module</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__MODULE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__NAME = 4;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__URI = 5;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT__ANY_ATTRIBUTE = 6;

  /**
   * The number of structural features of the '<em>Module Component</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_COMPONENT_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleWireImpl <em>Module Wire</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleWireImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModuleWire()
   * @generated
   */
  public static final int MODULE_WIRE = 13;

  /**
   * The feature id for the '<em><b>Source Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_WIRE__SOURCE_URI = 0;

  /**
   * The feature id for the '<em><b>Target Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_WIRE__TARGET_URI = 1;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_WIRE__ANY = 2;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_WIRE__ANY_ATTRIBUTE = 3;

  /**
   * The number of structural features of the '<em>Module Wire</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int MODULE_WIRE_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.PropertyImpl <em>Property</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.PropertyImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getProperty()
   * @generated
   */
  public static final int PROPERTY = 14;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__ANY = 0;

  /**
   * The feature id for the '<em><b>Default</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__DEFAULT = 1;

  /**
   * The feature id for the '<em><b>Many</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__MANY = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__NAME = 3;

  /**
   * The feature id for the '<em><b>Required</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__REQUIRED = 4;

  /**
   * The feature id for the '<em><b>Data Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__DATA_TYPE = 5;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY__ANY_ATTRIBUTE = 6;

  /**
   * The number of structural features of the '<em>Property</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.PropertyValuesImpl <em>Property Values</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.PropertyValuesImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getPropertyValues()
   * @generated
   */
  public static final int PROPERTY_VALUES = 15;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY_VALUES__ANY = 0;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY_VALUES__ANY_ATTRIBUTE = 1;

  /**
   * The number of structural features of the '<em>Property Values</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int PROPERTY_VALUES_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl <em>Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getReference()
   * @generated
   */
  public static final int REFERENCE = 16;

  /**
   * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE__INTERFACE_GROUP = 0;

  /**
   * The feature id for the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE__INTERFACE = 1;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE__ANY = 2;

  /**
   * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE__MULTIPLICITY = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE__NAME = 4;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE__ANY_ATTRIBUTE = 5;

  /**
   * The number of structural features of the '<em>Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE_FEATURE_COUNT = 6;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceValuesImpl <em>Reference Values</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ReferenceValuesImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getReferenceValues()
   * @generated
   */
  public static final int REFERENCE_VALUES = 17;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE_VALUES__ANY = 0;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE_VALUES__ANY_ATTRIBUTE = 1;

  /**
   * The number of structural features of the '<em>Reference Values</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int REFERENCE_VALUES_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.SCABindingImpl <em>SCA Binding</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCABindingImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getSCABinding()
   * @generated
   */
  public static final int SCA_BINDING = 18;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SCA_BINDING__URI = BINDING__URI;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SCA_BINDING__ANY = BINDING_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SCA_BINDING__ANY_ATTRIBUTE = BINDING_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>SCA Binding</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SCA_BINDING_FEATURE_COUNT = BINDING_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ServiceImpl <em>Service</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.ServiceImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getService()
   * @generated
   */
  public static final int SERVICE = 19;

  /**
   * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SERVICE__INTERFACE_GROUP = 0;

  /**
   * The feature id for the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SERVICE__INTERFACE = 1;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SERVICE__ANY = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SERVICE__NAME = 3;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SERVICE__ANY_ATTRIBUTE = 4;

  /**
   * The number of structural features of the '<em>Service</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SERVICE_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl <em>Subsystem</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getSubsystem()
   * @generated
   */
  public static final int SUBSYSTEM = 20;

  /**
   * The feature id for the '<em><b>Entry Point</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__ENTRY_POINT = 0;

  /**
   * The feature id for the '<em><b>Module Component</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__MODULE_COMPONENT = 1;

  /**
   * The feature id for the '<em><b>External Service</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__EXTERNAL_SERVICE = 2;

  /**
   * The feature id for the '<em><b>Wire</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__WIRE = 3;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__ANY = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__NAME = 5;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__URI = 6;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM__ANY_ATTRIBUTE = 7;

  /**
   * The number of structural features of the '<em>Subsystem</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SUBSYSTEM_FEATURE_COUNT = 8;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl <em>System Wire</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getSystemWire()
   * @generated
   */
  public static final int SYSTEM_WIRE = 21;

  /**
   * The feature id for the '<em><b>Source Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SYSTEM_WIRE__SOURCE_GROUP = 0;

  /**
   * The feature id for the '<em><b>Source</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SYSTEM_WIRE__SOURCE = 1;

  /**
   * The feature id for the '<em><b>Target Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SYSTEM_WIRE__TARGET_GROUP = 2;

  /**
   * The feature id for the '<em><b>Target</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SYSTEM_WIRE__TARGET = 3;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SYSTEM_WIRE__ANY = 4;

  /**
   * The number of structural features of the '<em>System Wire</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int SYSTEM_WIRE_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.WebServiceBindingImpl <em>Web Service Binding</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.WebServiceBindingImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getWebServiceBinding()
   * @generated
   */
  public static final int WEB_SERVICE_BINDING = 22;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WEB_SERVICE_BINDING__URI = BINDING__URI;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WEB_SERVICE_BINDING__ANY = BINDING_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Port</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WEB_SERVICE_BINDING__PORT = BINDING_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WEB_SERVICE_BINDING__ANY_ATTRIBUTE = BINDING_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Web Service Binding</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WEB_SERVICE_BINDING_FEATURE_COUNT = BINDING_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.impl.WSDLPortTypeImpl <em>WSDL Port Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.impl.WSDLPortTypeImpl
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getWSDLPortType()
   * @generated
   */
  public static final int WSDL_PORT_TYPE = 23;

  /**
   * The feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WSDL_PORT_TYPE__ANY = INTERFACE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Callback Interface</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WSDL_PORT_TYPE__CALLBACK_INTERFACE = INTERFACE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Interface</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WSDL_PORT_TYPE__INTERFACE = INTERFACE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WSDL_PORT_TYPE__ANY_ATTRIBUTE = INTERFACE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>WSDL Port Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public static final int WSDL_PORT_TYPE_FEATURE_COUNT = INTERFACE_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.Multiplicity <em>Multiplicity</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getMultiplicity()
   * @generated
   */
  public static final int MULTIPLICITY = 24;

  /**
   * The meta object id for the '{@link org.apache.tuscany.model.assembly.scdl.OverrideOptions <em>Override Options</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.OverrideOptions
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getOverrideOptions()
   * @generated
   */
  public static final int OVERRIDE_OPTIONS = 25;

  /**
   * The meta object id for the '<em>Multiplicity Object</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getMultiplicityObject()
   * @generated
   */
  public static final int MULTIPLICITY_OBJECT = 26;

  /**
   * The meta object id for the '<em>Override Options Object</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.apache.tuscany.model.assembly.scdl.OverrideOptions
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getOverrideOptionsObject()
   * @generated
   */
  public static final int OVERRIDE_OPTIONS_OBJECT = 27;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bindingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass componentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass componentTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass documentRootEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass entryPointEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass externalServiceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass implementationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass interfaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass javaImplementationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass javaInterfaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleComponentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleFragmentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleWireEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertyValuesEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass referenceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass referenceValuesEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass scaBindingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass serviceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subsystemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass systemWireEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass webServiceBindingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass wsdlPortTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum multiplicityEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum overrideOptionsEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType multiplicityObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType overrideOptionsObjectEDataType = null;

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
   * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#eNS_URI
   * @see #init()
   * @generated
   */
  private SCDLPackageImpl()
  {
    super(eNS_URI, ((EFactory)SCDLFactory.INSTANCE));
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
  public static SCDLPackageImpl init()
  {
    if (isInited) return (SCDLPackageImpl)EPackage.Registry.INSTANCE.getEPackage(SCDLPackageImpl.eNS_URI);

    // Obtain or create and register package
    SCDLPackageImpl theSCDLPackageImpl = (SCDLPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof SCDLPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new SCDLPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    XMLTypePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theSCDLPackageImpl.createPackageContents();

    // Initialize created meta-data
    theSCDLPackageImpl.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theSCDLPackageImpl.freeze();

    return theSCDLPackageImpl;
  }


  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Binding <em>Binding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Binding</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Binding
   * @generated
   */
  public EClass getBinding()
  {
    return bindingEClass;
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Binding#getUri <em>Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Binding#getUri()
   * @see #getBinding()
   * @generated
   */
  public EAttribute getBinding_Uri()
  {
    return (EAttribute)bindingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Component <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component
   * @generated
   */
  public EClass getComponent()
  {
    return componentEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Component#getImplementationGroup <em>Implementation Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Implementation Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getImplementationGroup()
   * @see #getComponent()
   * @generated
   */
  public EAttribute getComponent_ImplementationGroup()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.Component#getImplementation <em>Implementation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Implementation</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getImplementation()
   * @see #getComponent()
   * @generated
   */
  public EReference getComponent_Implementation()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.Component#getProperties <em>Properties</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Properties</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getProperties()
   * @see #getComponent()
   * @generated
   */
  public EReference getComponent_Properties()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.Component#getReferences <em>References</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>References</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getReferences()
   * @see #getComponent()
   * @generated
   */
  public EReference getComponent_References()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Component#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getAny()
   * @see #getComponent()
   * @generated
   */
  public EAttribute getComponent_Any()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Component#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getName()
   * @see #getComponent()
   * @generated
   */
  public EAttribute getComponent_Name()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Component#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Component#getAnyAttribute()
   * @see #getComponent()
   * @generated
   */
  public EAttribute getComponent_AnyAttribute()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.ComponentType <em>Component Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component Type</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ComponentType
   * @generated
   */
  public EClass getComponentType()
  {
    return componentTypeEClass;
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ComponentType#getService <em>Service</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Service</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ComponentType#getService()
   * @see #getComponentType()
   * @generated
   */
  public EReference getComponentType_Service()
  {
    return (EReference)componentTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ComponentType#getReference <em>Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Reference</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ComponentType#getReference()
   * @see #getComponentType()
   * @generated
   */
  public EReference getComponentType_Reference()
  {
    return (EReference)componentTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ComponentType#getProperty <em>Property</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Property</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ComponentType#getProperty()
   * @see #getComponentType()
   * @generated
   */
  public EReference getComponentType_Property()
  {
    return (EReference)componentTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ComponentType#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ComponentType#getAny()
   * @see #getComponentType()
   * @generated
   */
  public EAttribute getComponentType_Any()
  {
    return (EAttribute)componentTypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ComponentType#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ComponentType#getAnyAttribute()
   * @see #getComponentType()
   * @generated
   */
  public EAttribute getComponentType_AnyAttribute()
  {
    return (EAttribute)componentTypeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot <em>Document Root</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Document Root</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot
   * @generated
   */
  public EClass getDocumentRoot()
  {
    return documentRootEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getMixed <em>Mixed</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Mixed</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getMixed()
   * @see #getDocumentRoot()
   * @generated
   */
  public EAttribute getDocumentRoot_Mixed()
  {
    return (EAttribute)documentRootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the map '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getXMLNSPrefixMap()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_XMLNSPrefixMap()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the map '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>XSI Schema Location</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getXSISchemaLocation()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_XSISchemaLocation()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBinding <em>Binding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Binding</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBinding()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Binding()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingSca <em>Binding Sca</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Binding Sca</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingSca()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_BindingSca()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingWs <em>Binding Ws</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Binding Ws</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingWs()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_BindingWs()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getComponentType <em>Component Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Component Type</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getComponentType()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_ComponentType()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementation <em>Implementation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Implementation</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementation()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Implementation()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(7);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementationJava <em>Implementation Java</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Implementation Java</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementationJava()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_ImplementationJava()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(8);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterface()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Interface()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(9);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceJava <em>Interface Java</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface Java</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceJava()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_InterfaceJava()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(10);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceWsdl <em>Interface Wsdl</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface Wsdl</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceWsdl()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_InterfaceWsdl()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(11);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModule <em>Module</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Module</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModule()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Module()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(12);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModuleFragment <em>Module Fragment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Module Fragment</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModuleFragment()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_ModuleFragment()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(13);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Source</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSource()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Source()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(14);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceEpr <em>Source Epr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Source Epr</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceEpr()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_SourceEpr()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(15);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceUri <em>Source Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Source Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceUri()
   * @see #getDocumentRoot()
   * @generated
   */
  public EAttribute getDocumentRoot_SourceUri()
  {
    return (EAttribute)documentRootEClass.getEStructuralFeatures().get(16);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSubsystem <em>Subsystem</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Subsystem</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSubsystem()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Subsystem()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(17);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Target</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTarget()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_Target()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(18);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetEpr <em>Target Epr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Target Epr</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetEpr()
   * @see #getDocumentRoot()
   * @generated
   */
  public EReference getDocumentRoot_TargetEpr()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(19);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetUri <em>Target Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetUri()
   * @see #getDocumentRoot()
   * @generated
   */
  public EAttribute getDocumentRoot_TargetUri()
  {
    return (EAttribute)documentRootEClass.getEStructuralFeatures().get(20);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint <em>Entry Point</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Entry Point</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint
   * @generated
   */
  public EClass getEntryPoint()
  {
    return entryPointEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterfaceGroup <em>Interface Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Interface Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterfaceGroup()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_InterfaceGroup()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterface()
   * @see #getEntryPoint()
   * @generated
   */
  public EReference getEntryPoint_Interface()
  {
    return (EReference)entryPointEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getBindingGroup <em>Binding Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Binding Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getBindingGroup()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_BindingGroup()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getBinding <em>Binding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Binding</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getBinding()
   * @see #getEntryPoint()
   * @generated
   */
  public EReference getEntryPoint_Binding()
  {
    return (EReference)entryPointEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getReference <em>Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Reference</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getReference()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_Reference()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getAny()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_Any()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getMultiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Multiplicity</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getMultiplicity()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_Multiplicity()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getName()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_Name()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(7);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.EntryPoint#getAnyAttribute()
   * @see #getEntryPoint()
   * @generated
   */
  public EAttribute getEntryPoint_AnyAttribute()
  {
    return (EAttribute)entryPointEClass.getEStructuralFeatures().get(8);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.ExternalService <em>External Service</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>External Service</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService
   * @generated
   */
  public EClass getExternalService()
  {
    return externalServiceEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getInterfaceGroup <em>Interface Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Interface Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getInterfaceGroup()
   * @see #getExternalService()
   * @generated
   */
  public EAttribute getExternalService_InterfaceGroup()
  {
    return (EAttribute)externalServiceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getInterface()
   * @see #getExternalService()
   * @generated
   */
  public EReference getExternalService_Interface()
  {
    return (EReference)externalServiceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getBindingGroup <em>Binding Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Binding Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getBindingGroup()
   * @see #getExternalService()
   * @generated
   */
  public EAttribute getExternalService_BindingGroup()
  {
    return (EAttribute)externalServiceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getBinding <em>Binding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Binding</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getBinding()
   * @see #getExternalService()
   * @generated
   */
  public EReference getExternalService_Binding()
  {
    return (EReference)externalServiceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getName()
   * @see #getExternalService()
   * @generated
   */
  public EAttribute getExternalService_Name()
  {
    return (EAttribute)externalServiceEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getOverridable <em>Overridable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Overridable</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getOverridable()
   * @see #getExternalService()
   * @generated
   */
  public EAttribute getExternalService_Overridable()
  {
    return (EAttribute)externalServiceEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ExternalService#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ExternalService#getAnyAttribute()
   * @see #getExternalService()
   * @generated
   */
  public EAttribute getExternalService_AnyAttribute()
  {
    return (EAttribute)externalServiceEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Implementation <em>Implementation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Implementation</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Implementation
   * @generated
   */
  public EClass getImplementation()
  {
    return implementationEClass;
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Interface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Interface
   * @generated
   */
  public EClass getInterface()
  {
    return interfaceEClass;
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.JavaImplementation <em>Java Implementation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Java Implementation</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaImplementation
   * @generated
   */
  public EClass getJavaImplementation()
  {
    return javaImplementationEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.JavaImplementation#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaImplementation#getAny()
   * @see #getJavaImplementation()
   * @generated
   */
  public EAttribute getJavaImplementation_Any()
  {
    return (EAttribute)javaImplementationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.JavaImplementation#getClass_ <em>Class</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Class</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaImplementation#getClass_()
   * @see #getJavaImplementation()
   * @generated
   */
  public EAttribute getJavaImplementation_Class()
  {
    return (EAttribute)javaImplementationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.JavaImplementation#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaImplementation#getAnyAttribute()
   * @see #getJavaImplementation()
   * @generated
   */
  public EAttribute getJavaImplementation_AnyAttribute()
  {
    return (EAttribute)javaImplementationEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.JavaInterface <em>Java Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Java Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaInterface
   * @generated
   */
  public EClass getJavaInterface()
  {
    return javaInterfaceEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.JavaInterface#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaInterface#getAny()
   * @see #getJavaInterface()
   * @generated
   */
  public EAttribute getJavaInterface_Any()
  {
    return (EAttribute)javaInterfaceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.JavaInterface#getCallbackInterface <em>Callback Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Callback Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaInterface#getCallbackInterface()
   * @see #getJavaInterface()
   * @generated
   */
  public EAttribute getJavaInterface_CallbackInterface()
  {
    return (EAttribute)javaInterfaceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.JavaInterface#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaInterface#getInterface()
   * @see #getJavaInterface()
   * @generated
   */
  public EAttribute getJavaInterface_Interface()
  {
    return (EAttribute)javaInterfaceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.JavaInterface#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.JavaInterface#getAnyAttribute()
   * @see #getJavaInterface()
   * @generated
   */
  public EAttribute getJavaInterface_AnyAttribute()
  {
    return (EAttribute)javaInterfaceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Module <em>Module</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Module
   * @generated
   */
  public EClass getModule()
  {
    return moduleEClass;
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent <em>Module Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Component</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent
   * @generated
   */
  public EClass getModuleComponent()
  {
    return moduleComponentEClass;
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getProperties <em>Properties</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Properties</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getProperties()
   * @see #getModuleComponent()
   * @generated
   */
  public EReference getModuleComponent_Properties()
  {
    return (EReference)moduleComponentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getReferences <em>References</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>References</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getReferences()
   * @see #getModuleComponent()
   * @generated
   */
  public EReference getModuleComponent_References()
  {
    return (EReference)moduleComponentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getAny()
   * @see #getModuleComponent()
   * @generated
   */
  public EAttribute getModuleComponent_Any()
  {
    return (EAttribute)moduleComponentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getModule <em>Module</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Module</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getModule()
   * @see #getModuleComponent()
   * @generated
   */
  public EAttribute getModuleComponent_Module()
  {
    return (EAttribute)moduleComponentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getName()
   * @see #getModuleComponent()
   * @generated
   */
  public EAttribute getModuleComponent_Name()
  {
    return (EAttribute)moduleComponentEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getUri <em>Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getUri()
   * @see #getModuleComponent()
   * @generated
   */
  public EAttribute getModuleComponent_Uri()
  {
    return (EAttribute)moduleComponentEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ModuleComponent#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleComponent#getAnyAttribute()
   * @see #getModuleComponent()
   * @generated
   */
  public EAttribute getModuleComponent_AnyAttribute()
  {
    return (EAttribute)moduleComponentEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment <em>Module Fragment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Fragment</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment
   * @generated
   */
  public EClass getModuleFragment()
  {
    return moduleFragmentEClass;
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getEntryPoint <em>Entry Point</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Entry Point</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getEntryPoint()
   * @see #getModuleFragment()
   * @generated
   */
  public EReference getModuleFragment_EntryPoint()
  {
    return (EReference)moduleFragmentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getComponent <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Component</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getComponent()
   * @see #getModuleFragment()
   * @generated
   */
  public EReference getModuleFragment_Component()
  {
    return (EReference)moduleFragmentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getExternalService <em>External Service</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>External Service</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getExternalService()
   * @see #getModuleFragment()
   * @generated
   */
  public EReference getModuleFragment_ExternalService()
  {
    return (EReference)moduleFragmentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getWire <em>Wire</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Wire</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getWire()
   * @see #getModuleFragment()
   * @generated
   */
  public EReference getModuleFragment_Wire()
  {
    return (EReference)moduleFragmentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getAny()
   * @see #getModuleFragment()
   * @generated
   */
  public EAttribute getModuleFragment_Any()
  {
    return (EAttribute)moduleFragmentEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getName()
   * @see #getModuleFragment()
   * @generated
   */
  public EAttribute getModuleFragment_Name()
  {
    return (EAttribute)moduleFragmentEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleFragment#getAnyAttribute()
   * @see #getModuleFragment()
   * @generated
   */
  public EAttribute getModuleFragment_AnyAttribute()
  {
    return (EAttribute)moduleFragmentEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire <em>Module Wire</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Wire</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleWire
   * @generated
   */
  public EClass getModuleWire()
  {
    return moduleWireEClass;
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getSourceUri <em>Source Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Source Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleWire#getSourceUri()
   * @see #getModuleWire()
   * @generated
   */
  public EAttribute getModuleWire_SourceUri()
  {
    return (EAttribute)moduleWireEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getTargetUri <em>Target Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleWire#getTargetUri()
   * @see #getModuleWire()
   * @generated
   */
  public EAttribute getModuleWire_TargetUri()
  {
    return (EAttribute)moduleWireEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleWire#getAny()
   * @see #getModuleWire()
   * @generated
   */
  public EAttribute getModuleWire_Any()
  {
    return (EAttribute)moduleWireEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ModuleWire#getAnyAttribute()
   * @see #getModuleWire()
   * @generated
   */
  public EAttribute getModuleWire_AnyAttribute()
  {
    return (EAttribute)moduleWireEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Property <em>Property</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Property</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property
   * @generated
   */
  public EClass getProperty()
  {
    return propertyEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Property#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#getAny()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_Any()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Property#getDefault <em>Default</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#getDefault()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_Default()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Property#isMany <em>Many</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Many</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#isMany()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_Many()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Property#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#getName()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_Name()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Property#isRequired <em>Required</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Required</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#isRequired()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_Required()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Property#getDataType <em>Data Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Data Type</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#getDataType()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_DataType()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Property#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Property#getAnyAttribute()
   * @see #getProperty()
   * @generated
   */
  public EAttribute getProperty_AnyAttribute()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.PropertyValues <em>Property Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Property Values</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.PropertyValues
   * @generated
   */
  public EClass getPropertyValues()
  {
    return propertyValuesEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.PropertyValues#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.PropertyValues#getAny()
   * @see #getPropertyValues()
   * @generated
   */
  public EAttribute getPropertyValues_Any()
  {
    return (EAttribute)propertyValuesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.PropertyValues#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.PropertyValues#getAnyAttribute()
   * @see #getPropertyValues()
   * @generated
   */
  public EAttribute getPropertyValues_AnyAttribute()
  {
    return (EAttribute)propertyValuesEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Reference <em>Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Reference</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference
   * @generated
   */
  public EClass getReference()
  {
    return referenceEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Reference#getInterfaceGroup <em>Interface Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Interface Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference#getInterfaceGroup()
   * @see #getReference()
   * @generated
   */
  public EAttribute getReference_InterfaceGroup()
  {
    return (EAttribute)referenceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.Reference#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference#getInterface()
   * @see #getReference()
   * @generated
   */
  public EReference getReference_Interface()
  {
    return (EReference)referenceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Reference#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference#getAny()
   * @see #getReference()
   * @generated
   */
  public EAttribute getReference_Any()
  {
    return (EAttribute)referenceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Reference#getMultiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Multiplicity</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference#getMultiplicity()
   * @see #getReference()
   * @generated
   */
  public EAttribute getReference_Multiplicity()
  {
    return (EAttribute)referenceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Reference#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference#getName()
   * @see #getReference()
   * @generated
   */
  public EAttribute getReference_Name()
  {
    return (EAttribute)referenceEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Reference#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Reference#getAnyAttribute()
   * @see #getReference()
   * @generated
   */
  public EAttribute getReference_AnyAttribute()
  {
    return (EAttribute)referenceEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.ReferenceValues <em>Reference Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Reference Values</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ReferenceValues
   * @generated
   */
  public EClass getReferenceValues()
  {
    return referenceValuesEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ReferenceValues#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ReferenceValues#getAny()
   * @see #getReferenceValues()
   * @generated
   */
  public EAttribute getReferenceValues_Any()
  {
    return (EAttribute)referenceValuesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.ReferenceValues#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.ReferenceValues#getAnyAttribute()
   * @see #getReferenceValues()
   * @generated
   */
  public EAttribute getReferenceValues_AnyAttribute()
  {
    return (EAttribute)referenceValuesEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.SCABinding <em>SCA Binding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>SCA Binding</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SCABinding
   * @generated
   */
  public EClass getSCABinding()
  {
    return scaBindingEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.SCABinding#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SCABinding#getAny()
   * @see #getSCABinding()
   * @generated
   */
  public EAttribute getSCABinding_Any()
  {
    return (EAttribute)scaBindingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.SCABinding#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SCABinding#getAnyAttribute()
   * @see #getSCABinding()
   * @generated
   */
  public EAttribute getSCABinding_AnyAttribute()
  {
    return (EAttribute)scaBindingEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Service <em>Service</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Service</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Service
   * @generated
   */
  public EClass getService()
  {
    return serviceEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Service#getInterfaceGroup <em>Interface Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Interface Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Service#getInterfaceGroup()
   * @see #getService()
   * @generated
   */
  public EAttribute getService_InterfaceGroup()
  {
    return (EAttribute)serviceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.Service#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Service#getInterface()
   * @see #getService()
   * @generated
   */
  public EReference getService_Interface()
  {
    return (EReference)serviceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Service#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Service#getAny()
   * @see #getService()
   * @generated
   */
  public EAttribute getService_Any()
  {
    return (EAttribute)serviceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Service#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Service#getName()
   * @see #getService()
   * @generated
   */
  public EAttribute getService_Name()
  {
    return (EAttribute)serviceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Service#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Service#getAnyAttribute()
   * @see #getService()
   * @generated
   */
  public EAttribute getService_AnyAttribute()
  {
    return (EAttribute)serviceEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.Subsystem <em>Subsystem</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Subsystem</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem
   * @generated
   */
  public EClass getSubsystem()
  {
    return subsystemEClass;
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getEntryPoint <em>Entry Point</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Entry Point</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getEntryPoint()
   * @see #getSubsystem()
   * @generated
   */
  public EReference getSubsystem_EntryPoint()
  {
    return (EReference)subsystemEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getModuleComponent <em>Module Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Module Component</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getModuleComponent()
   * @see #getSubsystem()
   * @generated
   */
  public EReference getSubsystem_ModuleComponent()
  {
    return (EReference)subsystemEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getExternalService <em>External Service</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>External Service</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getExternalService()
   * @see #getSubsystem()
   * @generated
   */
  public EReference getSubsystem_ExternalService()
  {
    return (EReference)subsystemEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference list '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getWire <em>Wire</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Wire</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getWire()
   * @see #getSubsystem()
   * @generated
   */
  public EReference getSubsystem_Wire()
  {
    return (EReference)subsystemEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getAny()
   * @see #getSubsystem()
   * @generated
   */
  public EAttribute getSubsystem_Any()
  {
    return (EAttribute)subsystemEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getName()
   * @see #getSubsystem()
   * @generated
   */
  public EAttribute getSubsystem_Name()
  {
    return (EAttribute)subsystemEClass.getEStructuralFeatures().get(5);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getUri <em>Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uri</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getUri()
   * @see #getSubsystem()
   * @generated
   */
  public EAttribute getSubsystem_Uri()
  {
    return (EAttribute)subsystemEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.Subsystem#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Subsystem#getAnyAttribute()
   * @see #getSubsystem()
   * @generated
   */
  public EAttribute getSubsystem_AnyAttribute()
  {
    return (EAttribute)subsystemEClass.getEStructuralFeatures().get(7);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.SystemWire <em>System Wire</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>System Wire</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SystemWire
   * @generated
   */
  public EClass getSystemWire()
  {
    return systemWireEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getSourceGroup <em>Source Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Source Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SystemWire#getSourceGroup()
   * @see #getSystemWire()
   * @generated
   */
  public EAttribute getSystemWire_SourceGroup()
  {
    return (EAttribute)systemWireEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Source</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SystemWire#getSource()
   * @see #getSystemWire()
   * @generated
   */
  public EReference getSystemWire_Source()
  {
    return (EReference)systemWireEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getTargetGroup <em>Target Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Target Group</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SystemWire#getTargetGroup()
   * @see #getSystemWire()
   * @generated
   */
  public EAttribute getSystemWire_TargetGroup()
  {
    return (EAttribute)systemWireEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the containment reference '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Target</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SystemWire#getTarget()
   * @see #getSystemWire()
   * @generated
   */
  public EReference getSystemWire_Target()
  {
    return (EReference)systemWireEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.SystemWire#getAny()
   * @see #getSystemWire()
   * @generated
   */
  public EAttribute getSystemWire_Any()
  {
    return (EAttribute)systemWireEClass.getEStructuralFeatures().get(4);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.WebServiceBinding <em>Web Service Binding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Web Service Binding</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WebServiceBinding
   * @generated
   */
  public EClass getWebServiceBinding()
  {
    return webServiceBindingEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.WebServiceBinding#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WebServiceBinding#getAny()
   * @see #getWebServiceBinding()
   * @generated
   */
  public EAttribute getWebServiceBinding_Any()
  {
    return (EAttribute)webServiceBindingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.WebServiceBinding#getPort <em>Port</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Port</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WebServiceBinding#getPort()
   * @see #getWebServiceBinding()
   * @generated
   */
  public EAttribute getWebServiceBinding_Port()
  {
    return (EAttribute)webServiceBindingEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.WebServiceBinding#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WebServiceBinding#getAnyAttribute()
   * @see #getWebServiceBinding()
   * @generated
   */
  public EAttribute getWebServiceBinding_AnyAttribute()
  {
    return (EAttribute)webServiceBindingEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for class '{@link org.apache.tuscany.model.assembly.scdl.WSDLPortType <em>WSDL Port Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>WSDL Port Type</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WSDLPortType
   * @generated
   */
  public EClass getWSDLPortType()
  {
    return wsdlPortTypeEClass;
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.WSDLPortType#getAny <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WSDLPortType#getAny()
   * @see #getWSDLPortType()
   * @generated
   */
  public EAttribute getWSDLPortType_Any()
  {
    return (EAttribute)wsdlPortTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.WSDLPortType#getCallbackInterface <em>Callback Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Callback Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WSDLPortType#getCallbackInterface()
   * @see #getWSDLPortType()
   * @generated
   */
  public EAttribute getWSDLPortType_CallbackInterface()
  {
    return (EAttribute)wsdlPortTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * Returns the meta object for the attribute '{@link org.apache.tuscany.model.assembly.scdl.WSDLPortType#getInterface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Interface</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WSDLPortType#getInterface()
   * @see #getWSDLPortType()
   * @generated
   */
  public EAttribute getWSDLPortType_Interface()
  {
    return (EAttribute)wsdlPortTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * Returns the meta object for the attribute list '{@link org.apache.tuscany.model.assembly.scdl.WSDLPortType#getAnyAttribute <em>Any Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Any Attribute</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.WSDLPortType#getAnyAttribute()
   * @see #getWSDLPortType()
   * @generated
   */
  public EAttribute getWSDLPortType_AnyAttribute()
  {
    return (EAttribute)wsdlPortTypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * Returns the meta object for enum '{@link org.apache.tuscany.model.assembly.scdl.Multiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Multiplicity</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
   * @generated
   */
  public EEnum getMultiplicity()
  {
    return multiplicityEEnum;
  }

  /**
   * Returns the meta object for enum '{@link org.apache.tuscany.model.assembly.scdl.OverrideOptions <em>Override Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Override Options</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.OverrideOptions
   * @generated
   */
  public EEnum getOverrideOptions()
  {
    return overrideOptionsEEnum;
  }

  /**
   * Returns the meta object for data type '{@link org.apache.tuscany.model.assembly.scdl.Multiplicity <em>Multiplicity Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Multiplicity Object</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
   * @generated
   */
  public EDataType getMultiplicityObject()
  {
    return multiplicityObjectEDataType;
  }

  /**
   * Returns the meta object for data type '{@link org.apache.tuscany.model.assembly.scdl.OverrideOptions <em>Override Options Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Override Options Object</em>'.
   * @see org.apache.tuscany.model.assembly.scdl.OverrideOptions
   * @generated
   */
  public EDataType getOverrideOptionsObject()
  {
    return overrideOptionsObjectEDataType;
  }

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  public SCDLFactory getSCDLFactory()
  {
    return (SCDLFactory)getEFactoryInstance();
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
    bindingEClass = createEClass(BINDING);
    createEAttribute(bindingEClass, BINDING__URI);

    componentEClass = createEClass(COMPONENT);
    createEAttribute(componentEClass, COMPONENT__IMPLEMENTATION_GROUP);
    createEReference(componentEClass, COMPONENT__IMPLEMENTATION);
    createEReference(componentEClass, COMPONENT__PROPERTIES);
    createEReference(componentEClass, COMPONENT__REFERENCES);
    createEAttribute(componentEClass, COMPONENT__ANY);
    createEAttribute(componentEClass, COMPONENT__NAME);
    createEAttribute(componentEClass, COMPONENT__ANY_ATTRIBUTE);

    componentTypeEClass = createEClass(COMPONENT_TYPE);
    createEReference(componentTypeEClass, COMPONENT_TYPE__SERVICE);
    createEReference(componentTypeEClass, COMPONENT_TYPE__REFERENCE);
    createEReference(componentTypeEClass, COMPONENT_TYPE__PROPERTY);
    createEAttribute(componentTypeEClass, COMPONENT_TYPE__ANY);
    createEAttribute(componentTypeEClass, COMPONENT_TYPE__ANY_ATTRIBUTE);

    documentRootEClass = createEClass(DOCUMENT_ROOT);
    createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
    createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
    createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
    createEReference(documentRootEClass, DOCUMENT_ROOT__BINDING);
    createEReference(documentRootEClass, DOCUMENT_ROOT__BINDING_SCA);
    createEReference(documentRootEClass, DOCUMENT_ROOT__BINDING_WS);
    createEReference(documentRootEClass, DOCUMENT_ROOT__COMPONENT_TYPE);
    createEReference(documentRootEClass, DOCUMENT_ROOT__IMPLEMENTATION);
    createEReference(documentRootEClass, DOCUMENT_ROOT__IMPLEMENTATION_JAVA);
    createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE);
    createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE_JAVA);
    createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE_WSDL);
    createEReference(documentRootEClass, DOCUMENT_ROOT__MODULE);
    createEReference(documentRootEClass, DOCUMENT_ROOT__MODULE_FRAGMENT);
    createEReference(documentRootEClass, DOCUMENT_ROOT__SOURCE);
    createEReference(documentRootEClass, DOCUMENT_ROOT__SOURCE_EPR);
    createEAttribute(documentRootEClass, DOCUMENT_ROOT__SOURCE_URI);
    createEReference(documentRootEClass, DOCUMENT_ROOT__SUBSYSTEM);
    createEReference(documentRootEClass, DOCUMENT_ROOT__TARGET);
    createEReference(documentRootEClass, DOCUMENT_ROOT__TARGET_EPR);
    createEAttribute(documentRootEClass, DOCUMENT_ROOT__TARGET_URI);

    entryPointEClass = createEClass(ENTRY_POINT);
    createEAttribute(entryPointEClass, ENTRY_POINT__INTERFACE_GROUP);
    createEReference(entryPointEClass, ENTRY_POINT__INTERFACE);
    createEAttribute(entryPointEClass, ENTRY_POINT__BINDING_GROUP);
    createEReference(entryPointEClass, ENTRY_POINT__BINDING);
    createEAttribute(entryPointEClass, ENTRY_POINT__REFERENCE);
    createEAttribute(entryPointEClass, ENTRY_POINT__ANY);
    createEAttribute(entryPointEClass, ENTRY_POINT__MULTIPLICITY);
    createEAttribute(entryPointEClass, ENTRY_POINT__NAME);
    createEAttribute(entryPointEClass, ENTRY_POINT__ANY_ATTRIBUTE);

    externalServiceEClass = createEClass(EXTERNAL_SERVICE);
    createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__INTERFACE_GROUP);
    createEReference(externalServiceEClass, EXTERNAL_SERVICE__INTERFACE);
    createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__BINDING_GROUP);
    createEReference(externalServiceEClass, EXTERNAL_SERVICE__BINDING);
    createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__NAME);
    createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__OVERRIDABLE);
    createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__ANY_ATTRIBUTE);

    implementationEClass = createEClass(IMPLEMENTATION);

    interfaceEClass = createEClass(INTERFACE);

    javaImplementationEClass = createEClass(JAVA_IMPLEMENTATION);
    createEAttribute(javaImplementationEClass, JAVA_IMPLEMENTATION__ANY);
    createEAttribute(javaImplementationEClass, JAVA_IMPLEMENTATION__CLASS);
    createEAttribute(javaImplementationEClass, JAVA_IMPLEMENTATION__ANY_ATTRIBUTE);

    javaInterfaceEClass = createEClass(JAVA_INTERFACE);
    createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__ANY);
    createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__CALLBACK_INTERFACE);
    createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__INTERFACE);
    createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__ANY_ATTRIBUTE);

    moduleEClass = createEClass(MODULE);

    moduleComponentEClass = createEClass(MODULE_COMPONENT);
    createEReference(moduleComponentEClass, MODULE_COMPONENT__PROPERTIES);
    createEReference(moduleComponentEClass, MODULE_COMPONENT__REFERENCES);
    createEAttribute(moduleComponentEClass, MODULE_COMPONENT__ANY);
    createEAttribute(moduleComponentEClass, MODULE_COMPONENT__MODULE);
    createEAttribute(moduleComponentEClass, MODULE_COMPONENT__NAME);
    createEAttribute(moduleComponentEClass, MODULE_COMPONENT__URI);
    createEAttribute(moduleComponentEClass, MODULE_COMPONENT__ANY_ATTRIBUTE);

    moduleFragmentEClass = createEClass(MODULE_FRAGMENT);
    createEReference(moduleFragmentEClass, MODULE_FRAGMENT__ENTRY_POINT);
    createEReference(moduleFragmentEClass, MODULE_FRAGMENT__COMPONENT);
    createEReference(moduleFragmentEClass, MODULE_FRAGMENT__EXTERNAL_SERVICE);
    createEReference(moduleFragmentEClass, MODULE_FRAGMENT__WIRE);
    createEAttribute(moduleFragmentEClass, MODULE_FRAGMENT__ANY);
    createEAttribute(moduleFragmentEClass, MODULE_FRAGMENT__NAME);
    createEAttribute(moduleFragmentEClass, MODULE_FRAGMENT__ANY_ATTRIBUTE);

    moduleWireEClass = createEClass(MODULE_WIRE);
    createEAttribute(moduleWireEClass, MODULE_WIRE__SOURCE_URI);
    createEAttribute(moduleWireEClass, MODULE_WIRE__TARGET_URI);
    createEAttribute(moduleWireEClass, MODULE_WIRE__ANY);
    createEAttribute(moduleWireEClass, MODULE_WIRE__ANY_ATTRIBUTE);

    propertyEClass = createEClass(PROPERTY);
    createEAttribute(propertyEClass, PROPERTY__ANY);
    createEAttribute(propertyEClass, PROPERTY__DEFAULT);
    createEAttribute(propertyEClass, PROPERTY__MANY);
    createEAttribute(propertyEClass, PROPERTY__NAME);
    createEAttribute(propertyEClass, PROPERTY__REQUIRED);
    createEAttribute(propertyEClass, PROPERTY__DATA_TYPE);
    createEAttribute(propertyEClass, PROPERTY__ANY_ATTRIBUTE);

    propertyValuesEClass = createEClass(PROPERTY_VALUES);
    createEAttribute(propertyValuesEClass, PROPERTY_VALUES__ANY);
    createEAttribute(propertyValuesEClass, PROPERTY_VALUES__ANY_ATTRIBUTE);

    referenceEClass = createEClass(REFERENCE);
    createEAttribute(referenceEClass, REFERENCE__INTERFACE_GROUP);
    createEReference(referenceEClass, REFERENCE__INTERFACE);
    createEAttribute(referenceEClass, REFERENCE__ANY);
    createEAttribute(referenceEClass, REFERENCE__MULTIPLICITY);
    createEAttribute(referenceEClass, REFERENCE__NAME);
    createEAttribute(referenceEClass, REFERENCE__ANY_ATTRIBUTE);

    referenceValuesEClass = createEClass(REFERENCE_VALUES);
    createEAttribute(referenceValuesEClass, REFERENCE_VALUES__ANY);
    createEAttribute(referenceValuesEClass, REFERENCE_VALUES__ANY_ATTRIBUTE);

    scaBindingEClass = createEClass(SCA_BINDING);
    createEAttribute(scaBindingEClass, SCA_BINDING__ANY);
    createEAttribute(scaBindingEClass, SCA_BINDING__ANY_ATTRIBUTE);

    serviceEClass = createEClass(SERVICE);
    createEAttribute(serviceEClass, SERVICE__INTERFACE_GROUP);
    createEReference(serviceEClass, SERVICE__INTERFACE);
    createEAttribute(serviceEClass, SERVICE__ANY);
    createEAttribute(serviceEClass, SERVICE__NAME);
    createEAttribute(serviceEClass, SERVICE__ANY_ATTRIBUTE);

    subsystemEClass = createEClass(SUBSYSTEM);
    createEReference(subsystemEClass, SUBSYSTEM__ENTRY_POINT);
    createEReference(subsystemEClass, SUBSYSTEM__MODULE_COMPONENT);
    createEReference(subsystemEClass, SUBSYSTEM__EXTERNAL_SERVICE);
    createEReference(subsystemEClass, SUBSYSTEM__WIRE);
    createEAttribute(subsystemEClass, SUBSYSTEM__ANY);
    createEAttribute(subsystemEClass, SUBSYSTEM__NAME);
    createEAttribute(subsystemEClass, SUBSYSTEM__URI);
    createEAttribute(subsystemEClass, SUBSYSTEM__ANY_ATTRIBUTE);

    systemWireEClass = createEClass(SYSTEM_WIRE);
    createEAttribute(systemWireEClass, SYSTEM_WIRE__SOURCE_GROUP);
    createEReference(systemWireEClass, SYSTEM_WIRE__SOURCE);
    createEAttribute(systemWireEClass, SYSTEM_WIRE__TARGET_GROUP);
    createEReference(systemWireEClass, SYSTEM_WIRE__TARGET);
    createEAttribute(systemWireEClass, SYSTEM_WIRE__ANY);

    webServiceBindingEClass = createEClass(WEB_SERVICE_BINDING);
    createEAttribute(webServiceBindingEClass, WEB_SERVICE_BINDING__ANY);
    createEAttribute(webServiceBindingEClass, WEB_SERVICE_BINDING__PORT);
    createEAttribute(webServiceBindingEClass, WEB_SERVICE_BINDING__ANY_ATTRIBUTE);

    wsdlPortTypeEClass = createEClass(WSDL_PORT_TYPE);
    createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__ANY);
    createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__CALLBACK_INTERFACE);
    createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__INTERFACE);
    createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__ANY_ATTRIBUTE);

    // Create enums
    multiplicityEEnum = createEEnum(MULTIPLICITY);
    overrideOptionsEEnum = createEEnum(OVERRIDE_OPTIONS);

    // Create data types
    multiplicityObjectEDataType = createEDataType(MULTIPLICITY_OBJECT);
    overrideOptionsObjectEDataType = createEDataType(OVERRIDE_OPTIONS_OBJECT);
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
    XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

    // Add supertypes to classes
    javaImplementationEClass.getESuperTypes().add(this.getImplementation());
    javaInterfaceEClass.getESuperTypes().add(this.getInterface());
    moduleEClass.getESuperTypes().add(this.getModuleFragment());
    scaBindingEClass.getESuperTypes().add(this.getBinding());
    webServiceBindingEClass.getESuperTypes().add(this.getBinding());
    wsdlPortTypeEClass.getESuperTypes().add(this.getInterface());

    // Initialize classes and features; add operations and parameters
    initEClass(bindingEClass, Binding.class, "Binding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBinding_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(componentEClass, Component.class, "Component", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getComponent_ImplementationGroup(), ecorePackage.getEFeatureMapEntry(), "implementationGroup", null, 1, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Implementation(), this.getImplementation(), null, "implementation", null, 1, 1, Component.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Properties(), this.getPropertyValues(), null, "properties", null, 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_References(), this.getReferenceValues(), null, "references", null, 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponent_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponent_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponent_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(componentTypeEClass, ComponentType.class, "ComponentType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComponentType_Service(), this.getService(), null, "service", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponentType_Reference(), this.getReference(), null, "reference", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponentType_Property(), this.getProperty(), null, "property", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponentType_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponentType_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Binding(), this.getBinding(), null, "binding", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_BindingSca(), this.getSCABinding(), null, "bindingSca", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_BindingWs(), this.getWebServiceBinding(), null, "bindingWs", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_ComponentType(), this.getComponentType(), null, "componentType", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Implementation(), this.getImplementation(), null, "implementation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_ImplementationJava(), this.getJavaImplementation(), null, "implementationJava", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Interface(), this.getInterface(), null, "interface", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_InterfaceJava(), this.getJavaInterface(), null, "interfaceJava", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_InterfaceWsdl(), this.getWSDLPortType(), null, "interfaceWsdl", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Module(), this.getModule(), null, "module", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_ModuleFragment(), this.getModuleFragment(), null, "moduleFragment", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Source(), ecorePackage.getEObject(), null, "source", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_SourceEpr(), ecorePackage.getEObject(), null, "sourceEpr", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getDocumentRoot_SourceUri(), theXMLTypePackage.getAnyURI(), "sourceUri", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Subsystem(), this.getSubsystem(), null, "subsystem", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_Target(), ecorePackage.getEObject(), null, "target", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_TargetEpr(), ecorePackage.getEObject(), null, "targetEpr", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getDocumentRoot_TargetUri(), theXMLTypePackage.getAnyURI(), "targetUri", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(entryPointEClass, EntryPoint.class, "EntryPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEntryPoint_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 0, 1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEntryPoint_Interface(), this.getInterface(), null, "interface", null, 0, 1, EntryPoint.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getEntryPoint_BindingGroup(), ecorePackage.getEFeatureMapEntry(), "bindingGroup", null, 1, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEntryPoint_Binding(), this.getBinding(), null, "binding", null, 1, -1, EntryPoint.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getEntryPoint_Reference(), theXMLTypePackage.getAnyURI(), "reference", null, 1, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEntryPoint_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEntryPoint_Multiplicity(), this.getMultiplicity(), "multiplicity", "1..1", 0, 1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEntryPoint_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEntryPoint_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(externalServiceEClass, ExternalService.class, "ExternalService", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExternalService_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 1, 1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExternalService_Interface(), this.getInterface(), null, "interface", null, 1, 1, ExternalService.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getExternalService_BindingGroup(), ecorePackage.getEFeatureMapEntry(), "bindingGroup", null, 0, -1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExternalService_Binding(), this.getBinding(), null, "binding", null, 0, -1, ExternalService.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getExternalService_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getExternalService_Overridable(), this.getOverrideOptions(), "overridable", "may", 0, 1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getExternalService_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(implementationEClass, Implementation.class, "Implementation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(interfaceEClass, Interface.class, "Interface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(javaImplementationEClass, JavaImplementation.class, "JavaImplementation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJavaImplementation_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, JavaImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getJavaImplementation_Class(), theXMLTypePackage.getNCName(), "class", null, 1, 1, JavaImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getJavaImplementation_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, JavaImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(javaInterfaceEClass, JavaInterface.class, "JavaInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJavaInterface_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getJavaInterface_CallbackInterface(), theXMLTypePackage.getNCName(), "callbackInterface", null, 0, 1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getJavaInterface_Interface(), theXMLTypePackage.getNCName(), "interface", null, 1, 1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getJavaInterface_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleEClass, Module.class, "Module", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(moduleComponentEClass, ModuleComponent.class, "ModuleComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModuleComponent_Properties(), this.getPropertyValues(), null, "properties", null, 0, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModuleComponent_References(), this.getReferenceValues(), null, "references", null, 0, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleComponent_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleComponent_Module(), theXMLTypePackage.getNCName(), "module", null, 1, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleComponent_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleComponent_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleComponent_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleFragmentEClass, ModuleFragment.class, "ModuleFragment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModuleFragment_EntryPoint(), this.getEntryPoint(), null, "entryPoint", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModuleFragment_Component(), this.getComponent(), null, "component", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModuleFragment_ExternalService(), this.getExternalService(), null, "externalService", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModuleFragment_Wire(), this.getModuleWire(), null, "wire", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleFragment_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleFragment_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleFragment_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleWireEClass, ModuleWire.class, "ModuleWire", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModuleWire_SourceUri(), theXMLTypePackage.getAnyURI(), "sourceUri", null, 1, 1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleWire_TargetUri(), theXMLTypePackage.getAnyURI(), "targetUri", null, 1, 1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleWire_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleWire_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertyEClass, Property.class, "Property", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProperty_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_Default(), theXMLTypePackage.getString(), "default", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_Many(), theXMLTypePackage.getBoolean(), "many", "false", 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_Required(), theXMLTypePackage.getBoolean(), "required", "false", 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_DataType(), theXMLTypePackage.getQName(), "dataType", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertyValuesEClass, PropertyValues.class, "PropertyValues", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertyValues_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, PropertyValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPropertyValues_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, PropertyValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(referenceEClass, Reference.class, "Reference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getReference_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getReference_Interface(), this.getInterface(), null, "interface", null, 1, 1, Reference.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getReference_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReference_Multiplicity(), this.getMultiplicity(), "multiplicity", "1..1", 0, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReference_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReference_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(referenceValuesEClass, ReferenceValues.class, "ReferenceValues", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getReferenceValues_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ReferenceValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReferenceValues_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ReferenceValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(scaBindingEClass, SCABinding.class, "SCABinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSCABinding_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, SCABinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSCABinding_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, SCABinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(serviceEClass, Service.class, "Service", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getService_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 1, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getService_Interface(), this.getInterface(), null, "interface", null, 1, 1, Service.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getService_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getService_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getService_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subsystemEClass, Subsystem.class, "Subsystem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSubsystem_EntryPoint(), this.getEntryPoint(), null, "entryPoint", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubsystem_ModuleComponent(), this.getModuleComponent(), null, "moduleComponent", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubsystem_ExternalService(), this.getExternalService(), null, "externalService", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubsystem_Wire(), this.getSystemWire(), null, "wire", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSubsystem_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSubsystem_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSubsystem_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSubsystem_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(systemWireEClass, SystemWire.class, "SystemWire", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSystemWire_SourceGroup(), ecorePackage.getEFeatureMapEntry(), "sourceGroup", null, 1, 1, SystemWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSystemWire_Source(), ecorePackage.getEObject(), null, "source", null, 1, 1, SystemWire.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getSystemWire_TargetGroup(), ecorePackage.getEFeatureMapEntry(), "targetGroup", null, 1, 1, SystemWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSystemWire_Target(), ecorePackage.getEObject(), null, "target", null, 1, 1, SystemWire.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getSystemWire_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, SystemWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(webServiceBindingEClass, WebServiceBinding.class, "WebServiceBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getWebServiceBinding_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, WebServiceBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWebServiceBinding_Port(), theXMLTypePackage.getAnyURI(), "port", null, 1, 1, WebServiceBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWebServiceBinding_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, WebServiceBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(wsdlPortTypeEClass, WSDLPortType.class, "WSDLPortType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getWSDLPortType_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWSDLPortType_CallbackInterface(), theXMLTypePackage.getAnyURI(), "callbackInterface", null, 0, 1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWSDLPortType_Interface(), theXMLTypePackage.getAnyURI(), "interface", null, 1, 1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWSDLPortType_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(multiplicityEEnum, Multiplicity.class, "Multiplicity");
    addEEnumLiteral(multiplicityEEnum, Multiplicity._01_LITERAL);
    addEEnumLiteral(multiplicityEEnum, Multiplicity._11_LITERAL);
    addEEnumLiteral(multiplicityEEnum, Multiplicity._0N_LITERAL);
    addEEnumLiteral(multiplicityEEnum, Multiplicity._1N_LITERAL);

    initEEnum(overrideOptionsEEnum, OverrideOptions.class, "OverrideOptions");
    addEEnumLiteral(overrideOptionsEEnum, OverrideOptions.NO_LITERAL);
    addEEnumLiteral(overrideOptionsEEnum, OverrideOptions.MAY_LITERAL);
    addEEnumLiteral(overrideOptionsEEnum, OverrideOptions.MUST_LITERAL);

    // Initialize data types
    initEDataType(multiplicityObjectEDataType, Multiplicity.class, "MultiplicityObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
    initEDataType(overrideOptionsObjectEDataType, OverrideOptions.class, "OverrideOptionsObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);

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
      (bindingEClass, 
       source, 
       new String[] 
       {
       "name", "Binding",
       "kind", "empty"
       });		
    addAnnotation
      (getBinding_Uri(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "uri"
       });		
    addAnnotation
      (componentEClass, 
       source, 
       new String[] 
       {
       "name", "Component",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getComponent_ImplementationGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "implementation:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getComponent_Implementation(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "implementation",
       "namespace", "##targetNamespace",
       "group", "implementation:group"
       });		
    addAnnotation
      (getComponent_Properties(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "properties",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getComponent_References(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "references",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getComponent_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":4",
       "processing", "lax"
       });		
    addAnnotation
      (getComponent_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getComponent_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":6",
       "processing", "lax"
       });		
    addAnnotation
      (componentTypeEClass, 
       source, 
       new String[] 
       {
       "name", "ComponentType",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getComponentType_Service(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "service",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getComponentType_Reference(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "reference",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getComponentType_Property(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "property",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getComponentType_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":3",
       "processing", "lax"
       });		
    addAnnotation
      (getComponentType_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":4",
       "processing", "lax"
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
      (getDocumentRoot_Binding(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "binding",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_BindingSca(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "binding.sca",
       "namespace", "##targetNamespace",
       "affiliation", "binding"
       });		
    addAnnotation
      (getDocumentRoot_BindingWs(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "binding.ws",
       "namespace", "##targetNamespace",
       "affiliation", "binding"
       });		
    addAnnotation
      (getDocumentRoot_ComponentType(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "componentType",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_Implementation(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "implementation",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_ImplementationJava(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "implementation.java",
       "namespace", "##targetNamespace",
       "affiliation", "implementation"
       });		
    addAnnotation
      (getDocumentRoot_Interface(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_InterfaceJava(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface.java",
       "namespace", "##targetNamespace",
       "affiliation", "interface"
       });		
    addAnnotation
      (getDocumentRoot_InterfaceWsdl(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface.wsdl",
       "namespace", "##targetNamespace",
       "affiliation", "interface"
       });		
    addAnnotation
      (getDocumentRoot_Module(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "module",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_ModuleFragment(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "moduleFragment",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_Source(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "source",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_SourceEpr(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "source.epr",
       "namespace", "##targetNamespace",
       "affiliation", "source"
       });		
    addAnnotation
      (getDocumentRoot_SourceUri(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "source.uri",
       "namespace", "##targetNamespace",
       "affiliation", "source"
       });		
    addAnnotation
      (getDocumentRoot_Subsystem(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "subsystem",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_Target(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "target",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getDocumentRoot_TargetEpr(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "target.epr",
       "namespace", "##targetNamespace",
       "affiliation", "target"
       });		
    addAnnotation
      (getDocumentRoot_TargetUri(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "target.uri",
       "namespace", "##targetNamespace",
       "affiliation", "target"
       });		
    addAnnotation
      (entryPointEClass, 
       source, 
       new String[] 
       {
       "name", "EntryPoint",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getEntryPoint_InterfaceGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "interface:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getEntryPoint_Interface(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface",
       "namespace", "##targetNamespace",
       "group", "interface:group"
       });		
    addAnnotation
      (getEntryPoint_BindingGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "binding:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getEntryPoint_Binding(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "binding",
       "namespace", "##targetNamespace",
       "group", "binding:group"
       });		
    addAnnotation
      (getEntryPoint_Reference(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "reference",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getEntryPoint_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":5",
       "processing", "lax"
       });		
    addAnnotation
      (getEntryPoint_Multiplicity(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "multiplicity"
       });		
    addAnnotation
      (getEntryPoint_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getEntryPoint_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":8",
       "processing", "lax"
       });		
    addAnnotation
      (externalServiceEClass, 
       source, 
       new String[] 
       {
       "name", "ExternalService",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getExternalService_InterfaceGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "interface:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getExternalService_Interface(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface",
       "namespace", "##targetNamespace",
       "group", "interface:group"
       });		
    addAnnotation
      (getExternalService_BindingGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "binding:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getExternalService_Binding(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "binding",
       "namespace", "##targetNamespace",
       "group", "binding:group"
       });		
    addAnnotation
      (getExternalService_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getExternalService_Overridable(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "overridable"
       });		
    addAnnotation
      (getExternalService_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":6",
       "processing", "lax"
       });		
    addAnnotation
      (implementationEClass, 
       source, 
       new String[] 
       {
       "name", "Implementation",
       "kind", "empty"
       });		
    addAnnotation
      (interfaceEClass, 
       source, 
       new String[] 
       {
       "name", "Interface",
       "kind", "empty"
       });		
    addAnnotation
      (javaImplementationEClass, 
       source, 
       new String[] 
       {
       "name", "JavaImplementation",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getJavaImplementation_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":0",
       "processing", "lax"
       });		
    addAnnotation
      (getJavaImplementation_Class(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "class"
       });		
    addAnnotation
      (getJavaImplementation_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (javaInterfaceEClass, 
       source, 
       new String[] 
       {
       "name", "JavaInterface",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getJavaInterface_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":0",
       "processing", "lax"
       });		
    addAnnotation
      (getJavaInterface_CallbackInterface(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "callbackInterface"
       });		
    addAnnotation
      (getJavaInterface_Interface(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "interface"
       });		
    addAnnotation
      (getJavaInterface_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":3",
       "processing", "lax"
       });		
    addAnnotation
      (moduleEClass, 
       source, 
       new String[] 
       {
       "name", "Module",
       "kind", "elementOnly"
       });		
    addAnnotation
      (moduleComponentEClass, 
       source, 
       new String[] 
       {
       "name", "ModuleComponent",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getModuleComponent_Properties(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "properties",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleComponent_References(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "references",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleComponent_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (getModuleComponent_Module(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "module"
       });		
    addAnnotation
      (getModuleComponent_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getModuleComponent_Uri(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "uri"
       });		
    addAnnotation
      (getModuleComponent_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":6",
       "processing", "lax"
       });		
    addAnnotation
      (moduleFragmentEClass, 
       source, 
       new String[] 
       {
       "name", "ModuleFragment",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getModuleFragment_EntryPoint(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "entryPoint",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleFragment_Component(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "component",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleFragment_ExternalService(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "externalService",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleFragment_Wire(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "wire",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleFragment_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":4",
       "processing", "lax"
       });		
    addAnnotation
      (getModuleFragment_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getModuleFragment_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":6",
       "processing", "lax"
       });		
    addAnnotation
      (moduleWireEClass, 
       source, 
       new String[] 
       {
       "name", "ModuleWire",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getModuleWire_SourceUri(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "source.uri",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleWire_TargetUri(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "target.uri",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getModuleWire_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (getModuleWire_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":3",
       "processing", "lax"
       });		
    addAnnotation
      (multiplicityEEnum, 
       source, 
       new String[] 
       {
       "name", "Multiplicity"
       });		
    addAnnotation
      (multiplicityObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "Multiplicity:Object",
       "baseType", "Multiplicity"
       });		
    addAnnotation
      (overrideOptionsEEnum, 
       source, 
       new String[] 
       {
       "name", "OverrideOptions"
       });		
    addAnnotation
      (overrideOptionsObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "OverrideOptions:Object",
       "baseType", "OverrideOptions"
       });		
    addAnnotation
      (propertyEClass, 
       source, 
       new String[] 
       {
       "name", "Property",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getProperty_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":0",
       "processing", "lax"
       });		
    addAnnotation
      (getProperty_Default(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "default"
       });		
    addAnnotation
      (getProperty_Many(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "many"
       });		
    addAnnotation
      (getProperty_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getProperty_Required(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "required"
       });		
    addAnnotation
      (getProperty_DataType(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "type"
       });		
    addAnnotation
      (getProperty_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":6",
       "processing", "lax"
       });		
    addAnnotation
      (propertyValuesEClass, 
       source, 
       new String[] 
       {
       "name", "PropertyValues",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getPropertyValues_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":0",
       "processing", "lax"
       });		
    addAnnotation
      (getPropertyValues_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":1",
       "processing", "lax"
       });		
    addAnnotation
      (referenceEClass, 
       source, 
       new String[] 
       {
       "name", "Reference",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getReference_InterfaceGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "interface:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getReference_Interface(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface",
       "namespace", "##targetNamespace",
       "group", "interface:group"
       });		
    addAnnotation
      (getReference_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (getReference_Multiplicity(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "multiplicity"
       });		
    addAnnotation
      (getReference_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getReference_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":5",
       "processing", "lax"
       });		
    addAnnotation
      (referenceValuesEClass, 
       source, 
       new String[] 
       {
       "name", "ReferenceValues",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getReferenceValues_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":0",
       "processing", "lax"
       });		
    addAnnotation
      (getReferenceValues_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":1",
       "processing", "lax"
       });		
    addAnnotation
      (scaBindingEClass, 
       source, 
       new String[] 
       {
       "name", "SCABinding",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getSCABinding_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":1",
       "processing", "lax"
       });		
    addAnnotation
      (getSCABinding_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (serviceEClass, 
       source, 
       new String[] 
       {
       "name", "Service",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getService_InterfaceGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "interface:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getService_Interface(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "interface",
       "namespace", "##targetNamespace",
       "group", "interface:group"
       });		
    addAnnotation
      (getService_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (getService_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getService_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":4",
       "processing", "lax"
       });		
    addAnnotation
      (subsystemEClass, 
       source, 
       new String[] 
       {
       "name", "Subsystem",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getSubsystem_EntryPoint(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "entryPoint",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getSubsystem_ModuleComponent(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "moduleComponent",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getSubsystem_ExternalService(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "externalService",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getSubsystem_Wire(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "wire",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getSubsystem_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":4",
       "processing", "lax"
       });		
    addAnnotation
      (getSubsystem_Name(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "name"
       });		
    addAnnotation
      (getSubsystem_Uri(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "uri"
       });		
    addAnnotation
      (getSubsystem_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":7",
       "processing", "lax"
       });		
    addAnnotation
      (systemWireEClass, 
       source, 
       new String[] 
       {
       "name", "SystemWire",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getSystemWire_SourceGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "source:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getSystemWire_Source(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "source",
       "namespace", "##targetNamespace",
       "group", "source:group"
       });		
    addAnnotation
      (getSystemWire_TargetGroup(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "target:group",
       "namespace", "##targetNamespace"
       });		
    addAnnotation
      (getSystemWire_Target(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "target",
       "namespace", "##targetNamespace",
       "group", "target:group"
       });		
    addAnnotation
      (getSystemWire_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":4",
       "processing", "lax"
       });		
    addAnnotation
      (webServiceBindingEClass, 
       source, 
       new String[] 
       {
       "name", "WebServiceBinding",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getWebServiceBinding_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":1",
       "processing", "lax"
       });		
    addAnnotation
      (getWebServiceBinding_Port(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "port"
       });		
    addAnnotation
      (getWebServiceBinding_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":3",
       "processing", "lax"
       });		
    addAnnotation
      (wsdlPortTypeEClass, 
       source, 
       new String[] 
       {
       "name", "WSDLPortType",
       "kind", "elementOnly"
       });		
    addAnnotation
      (getWSDLPortType_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##other",
       "name", ":0",
       "processing", "lax"
       });		
    addAnnotation
      (getWSDLPortType_CallbackInterface(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "callbackInterface"
       });		
    addAnnotation
      (getWSDLPortType_Interface(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "interface"
       });		
    addAnnotation
      (getWSDLPortType_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":3",
       "processing", "lax"
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
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.BindingImpl <em>Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.BindingImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getBinding()
     * @generated
     */
    public static final EClass BINDING = eINSTANCE.getBinding();

    /**
     * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute BINDING__URI = eINSTANCE.getBinding_Uri();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getComponent()
     * @generated
     */
    public static final EClass COMPONENT = eINSTANCE.getComponent();

    /**
     * The meta object literal for the '<em><b>Implementation Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute COMPONENT__IMPLEMENTATION_GROUP = eINSTANCE.getComponent_ImplementationGroup();

    /**
     * The meta object literal for the '<em><b>Implementation</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference COMPONENT__IMPLEMENTATION = eINSTANCE.getComponent_Implementation();

    /**
     * The meta object literal for the '<em><b>Properties</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference COMPONENT__PROPERTIES = eINSTANCE.getComponent_Properties();

    /**
     * The meta object literal for the '<em><b>References</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference COMPONENT__REFERENCES = eINSTANCE.getComponent_References();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute COMPONENT__ANY = eINSTANCE.getComponent_Any();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute COMPONENT__NAME = eINSTANCE.getComponent_Name();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute COMPONENT__ANY_ATTRIBUTE = eINSTANCE.getComponent_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl <em>Component Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getComponentType()
     * @generated
     */
    public static final EClass COMPONENT_TYPE = eINSTANCE.getComponentType();

    /**
     * The meta object literal for the '<em><b>Service</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference COMPONENT_TYPE__SERVICE = eINSTANCE.getComponentType_Service();

    /**
     * The meta object literal for the '<em><b>Reference</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference COMPONENT_TYPE__REFERENCE = eINSTANCE.getComponentType_Reference();

    /**
     * The meta object literal for the '<em><b>Property</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference COMPONENT_TYPE__PROPERTY = eINSTANCE.getComponentType_Property();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute COMPONENT_TYPE__ANY = eINSTANCE.getComponentType_Any();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute COMPONENT_TYPE__ANY_ATTRIBUTE = eINSTANCE.getComponentType_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getDocumentRoot()
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
     * The meta object literal for the '<em><b>Binding</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__BINDING = eINSTANCE.getDocumentRoot_Binding();

    /**
     * The meta object literal for the '<em><b>Binding Sca</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__BINDING_SCA = eINSTANCE.getDocumentRoot_BindingSca();

    /**
     * The meta object literal for the '<em><b>Binding Ws</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__BINDING_WS = eINSTANCE.getDocumentRoot_BindingWs();

    /**
     * The meta object literal for the '<em><b>Component Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__COMPONENT_TYPE = eINSTANCE.getDocumentRoot_ComponentType();

    /**
     * The meta object literal for the '<em><b>Implementation</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__IMPLEMENTATION = eINSTANCE.getDocumentRoot_Implementation();

    /**
     * The meta object literal for the '<em><b>Implementation Java</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__IMPLEMENTATION_JAVA = eINSTANCE.getDocumentRoot_ImplementationJava();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__INTERFACE = eINSTANCE.getDocumentRoot_Interface();

    /**
     * The meta object literal for the '<em><b>Interface Java</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__INTERFACE_JAVA = eINSTANCE.getDocumentRoot_InterfaceJava();

    /**
     * The meta object literal for the '<em><b>Interface Wsdl</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__INTERFACE_WSDL = eINSTANCE.getDocumentRoot_InterfaceWsdl();

    /**
     * The meta object literal for the '<em><b>Module</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__MODULE = eINSTANCE.getDocumentRoot_Module();

    /**
     * The meta object literal for the '<em><b>Module Fragment</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__MODULE_FRAGMENT = eINSTANCE.getDocumentRoot_ModuleFragment();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__SOURCE = eINSTANCE.getDocumentRoot_Source();

    /**
     * The meta object literal for the '<em><b>Source Epr</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__SOURCE_EPR = eINSTANCE.getDocumentRoot_SourceEpr();

    /**
     * The meta object literal for the '<em><b>Source Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute DOCUMENT_ROOT__SOURCE_URI = eINSTANCE.getDocumentRoot_SourceUri();

    /**
     * The meta object literal for the '<em><b>Subsystem</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__SUBSYSTEM = eINSTANCE.getDocumentRoot_Subsystem();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__TARGET = eINSTANCE.getDocumentRoot_Target();

    /**
     * The meta object literal for the '<em><b>Target Epr</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference DOCUMENT_ROOT__TARGET_EPR = eINSTANCE.getDocumentRoot_TargetEpr();

    /**
     * The meta object literal for the '<em><b>Target Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute DOCUMENT_ROOT__TARGET_URI = eINSTANCE.getDocumentRoot_TargetUri();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl <em>Entry Point</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getEntryPoint()
     * @generated
     */
    public static final EClass ENTRY_POINT = eINSTANCE.getEntryPoint();

    /**
     * The meta object literal for the '<em><b>Interface Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__INTERFACE_GROUP = eINSTANCE.getEntryPoint_InterfaceGroup();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference ENTRY_POINT__INTERFACE = eINSTANCE.getEntryPoint_Interface();

    /**
     * The meta object literal for the '<em><b>Binding Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__BINDING_GROUP = eINSTANCE.getEntryPoint_BindingGroup();

    /**
     * The meta object literal for the '<em><b>Binding</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference ENTRY_POINT__BINDING = eINSTANCE.getEntryPoint_Binding();

    /**
     * The meta object literal for the '<em><b>Reference</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__REFERENCE = eINSTANCE.getEntryPoint_Reference();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__ANY = eINSTANCE.getEntryPoint_Any();

    /**
     * The meta object literal for the '<em><b>Multiplicity</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__MULTIPLICITY = eINSTANCE.getEntryPoint_Multiplicity();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__NAME = eINSTANCE.getEntryPoint_Name();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute ENTRY_POINT__ANY_ATTRIBUTE = eINSTANCE.getEntryPoint_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl <em>External Service</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getExternalService()
     * @generated
     */
    public static final EClass EXTERNAL_SERVICE = eINSTANCE.getExternalService();

    /**
     * The meta object literal for the '<em><b>Interface Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute EXTERNAL_SERVICE__INTERFACE_GROUP = eINSTANCE.getExternalService_InterfaceGroup();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference EXTERNAL_SERVICE__INTERFACE = eINSTANCE.getExternalService_Interface();

    /**
     * The meta object literal for the '<em><b>Binding Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute EXTERNAL_SERVICE__BINDING_GROUP = eINSTANCE.getExternalService_BindingGroup();

    /**
     * The meta object literal for the '<em><b>Binding</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference EXTERNAL_SERVICE__BINDING = eINSTANCE.getExternalService_Binding();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute EXTERNAL_SERVICE__NAME = eINSTANCE.getExternalService_Name();

    /**
     * The meta object literal for the '<em><b>Overridable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute EXTERNAL_SERVICE__OVERRIDABLE = eINSTANCE.getExternalService_Overridable();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute EXTERNAL_SERVICE__ANY_ATTRIBUTE = eINSTANCE.getExternalService_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ImplementationImpl <em>Implementation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ImplementationImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getImplementation()
     * @generated
     */
    public static final EClass IMPLEMENTATION = eINSTANCE.getImplementation();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.InterfaceImpl <em>Interface</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.InterfaceImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getInterface()
     * @generated
     */
    public static final EClass INTERFACE = eINSTANCE.getInterface();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.JavaImplementationImpl <em>Java Implementation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.JavaImplementationImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getJavaImplementation()
     * @generated
     */
    public static final EClass JAVA_IMPLEMENTATION = eINSTANCE.getJavaImplementation();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_IMPLEMENTATION__ANY = eINSTANCE.getJavaImplementation_Any();

    /**
     * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_IMPLEMENTATION__CLASS = eINSTANCE.getJavaImplementation_Class();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_IMPLEMENTATION__ANY_ATTRIBUTE = eINSTANCE.getJavaImplementation_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.JavaInterfaceImpl <em>Java Interface</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.JavaInterfaceImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getJavaInterface()
     * @generated
     */
    public static final EClass JAVA_INTERFACE = eINSTANCE.getJavaInterface();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_INTERFACE__ANY = eINSTANCE.getJavaInterface_Any();

    /**
     * The meta object literal for the '<em><b>Callback Interface</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_INTERFACE__CALLBACK_INTERFACE = eINSTANCE.getJavaInterface_CallbackInterface();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_INTERFACE__INTERFACE = eINSTANCE.getJavaInterface_Interface();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute JAVA_INTERFACE__ANY_ATTRIBUTE = eINSTANCE.getJavaInterface_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleImpl <em>Module</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModule()
     * @generated
     */
    public static final EClass MODULE = eINSTANCE.getModule();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleComponentImpl <em>Module Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleComponentImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModuleComponent()
     * @generated
     */
    public static final EClass MODULE_COMPONENT = eINSTANCE.getModuleComponent();

    /**
     * The meta object literal for the '<em><b>Properties</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference MODULE_COMPONENT__PROPERTIES = eINSTANCE.getModuleComponent_Properties();

    /**
     * The meta object literal for the '<em><b>References</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference MODULE_COMPONENT__REFERENCES = eINSTANCE.getModuleComponent_References();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_COMPONENT__ANY = eINSTANCE.getModuleComponent_Any();

    /**
     * The meta object literal for the '<em><b>Module</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_COMPONENT__MODULE = eINSTANCE.getModuleComponent_Module();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_COMPONENT__NAME = eINSTANCE.getModuleComponent_Name();

    /**
     * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_COMPONENT__URI = eINSTANCE.getModuleComponent_Uri();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_COMPONENT__ANY_ATTRIBUTE = eINSTANCE.getModuleComponent_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleFragmentImpl <em>Module Fragment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleFragmentImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModuleFragment()
     * @generated
     */
    public static final EClass MODULE_FRAGMENT = eINSTANCE.getModuleFragment();

    /**
     * The meta object literal for the '<em><b>Entry Point</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference MODULE_FRAGMENT__ENTRY_POINT = eINSTANCE.getModuleFragment_EntryPoint();

    /**
     * The meta object literal for the '<em><b>Component</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference MODULE_FRAGMENT__COMPONENT = eINSTANCE.getModuleFragment_Component();

    /**
     * The meta object literal for the '<em><b>External Service</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference MODULE_FRAGMENT__EXTERNAL_SERVICE = eINSTANCE.getModuleFragment_ExternalService();

    /**
     * The meta object literal for the '<em><b>Wire</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference MODULE_FRAGMENT__WIRE = eINSTANCE.getModuleFragment_Wire();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_FRAGMENT__ANY = eINSTANCE.getModuleFragment_Any();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_FRAGMENT__NAME = eINSTANCE.getModuleFragment_Name();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_FRAGMENT__ANY_ATTRIBUTE = eINSTANCE.getModuleFragment_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ModuleWireImpl <em>Module Wire</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ModuleWireImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getModuleWire()
     * @generated
     */
    public static final EClass MODULE_WIRE = eINSTANCE.getModuleWire();

    /**
     * The meta object literal for the '<em><b>Source Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_WIRE__SOURCE_URI = eINSTANCE.getModuleWire_SourceUri();

    /**
     * The meta object literal for the '<em><b>Target Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_WIRE__TARGET_URI = eINSTANCE.getModuleWire_TargetUri();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_WIRE__ANY = eINSTANCE.getModuleWire_Any();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute MODULE_WIRE__ANY_ATTRIBUTE = eINSTANCE.getModuleWire_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.PropertyImpl <em>Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.PropertyImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getProperty()
     * @generated
     */
    public static final EClass PROPERTY = eINSTANCE.getProperty();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__ANY = eINSTANCE.getProperty_Any();

    /**
     * The meta object literal for the '<em><b>Default</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__DEFAULT = eINSTANCE.getProperty_Default();

    /**
     * The meta object literal for the '<em><b>Many</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__MANY = eINSTANCE.getProperty_Many();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__NAME = eINSTANCE.getProperty_Name();

    /**
     * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__REQUIRED = eINSTANCE.getProperty_Required();

    /**
     * The meta object literal for the '<em><b>Data Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__DATA_TYPE = eINSTANCE.getProperty_DataType();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY__ANY_ATTRIBUTE = eINSTANCE.getProperty_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.PropertyValuesImpl <em>Property Values</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.PropertyValuesImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getPropertyValues()
     * @generated
     */
    public static final EClass PROPERTY_VALUES = eINSTANCE.getPropertyValues();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY_VALUES__ANY = eINSTANCE.getPropertyValues_Any();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute PROPERTY_VALUES__ANY_ATTRIBUTE = eINSTANCE.getPropertyValues_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl <em>Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getReference()
     * @generated
     */
    public static final EClass REFERENCE = eINSTANCE.getReference();

    /**
     * The meta object literal for the '<em><b>Interface Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE__INTERFACE_GROUP = eINSTANCE.getReference_InterfaceGroup();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference REFERENCE__INTERFACE = eINSTANCE.getReference_Interface();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE__ANY = eINSTANCE.getReference_Any();

    /**
     * The meta object literal for the '<em><b>Multiplicity</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE__MULTIPLICITY = eINSTANCE.getReference_Multiplicity();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE__NAME = eINSTANCE.getReference_Name();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE__ANY_ATTRIBUTE = eINSTANCE.getReference_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceValuesImpl <em>Reference Values</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ReferenceValuesImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getReferenceValues()
     * @generated
     */
    public static final EClass REFERENCE_VALUES = eINSTANCE.getReferenceValues();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE_VALUES__ANY = eINSTANCE.getReferenceValues_Any();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute REFERENCE_VALUES__ANY_ATTRIBUTE = eINSTANCE.getReferenceValues_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.SCABindingImpl <em>SCA Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCABindingImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getSCABinding()
     * @generated
     */
    public static final EClass SCA_BINDING = eINSTANCE.getSCABinding();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SCA_BINDING__ANY = eINSTANCE.getSCABinding_Any();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SCA_BINDING__ANY_ATTRIBUTE = eINSTANCE.getSCABinding_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.ServiceImpl <em>Service</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.ServiceImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getService()
     * @generated
     */
    public static final EClass SERVICE = eINSTANCE.getService();

    /**
     * The meta object literal for the '<em><b>Interface Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SERVICE__INTERFACE_GROUP = eINSTANCE.getService_InterfaceGroup();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SERVICE__INTERFACE = eINSTANCE.getService_Interface();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SERVICE__ANY = eINSTANCE.getService_Any();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SERVICE__NAME = eINSTANCE.getService_Name();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SERVICE__ANY_ATTRIBUTE = eINSTANCE.getService_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl <em>Subsystem</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getSubsystem()
     * @generated
     */
    public static final EClass SUBSYSTEM = eINSTANCE.getSubsystem();

    /**
     * The meta object literal for the '<em><b>Entry Point</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SUBSYSTEM__ENTRY_POINT = eINSTANCE.getSubsystem_EntryPoint();

    /**
     * The meta object literal for the '<em><b>Module Component</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SUBSYSTEM__MODULE_COMPONENT = eINSTANCE.getSubsystem_ModuleComponent();

    /**
     * The meta object literal for the '<em><b>External Service</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SUBSYSTEM__EXTERNAL_SERVICE = eINSTANCE.getSubsystem_ExternalService();

    /**
     * The meta object literal for the '<em><b>Wire</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SUBSYSTEM__WIRE = eINSTANCE.getSubsystem_Wire();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SUBSYSTEM__ANY = eINSTANCE.getSubsystem_Any();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SUBSYSTEM__NAME = eINSTANCE.getSubsystem_Name();

    /**
     * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SUBSYSTEM__URI = eINSTANCE.getSubsystem_Uri();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SUBSYSTEM__ANY_ATTRIBUTE = eINSTANCE.getSubsystem_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl <em>System Wire</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getSystemWire()
     * @generated
     */
    public static final EClass SYSTEM_WIRE = eINSTANCE.getSystemWire();

    /**
     * The meta object literal for the '<em><b>Source Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SYSTEM_WIRE__SOURCE_GROUP = eINSTANCE.getSystemWire_SourceGroup();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SYSTEM_WIRE__SOURCE = eINSTANCE.getSystemWire_Source();

    /**
     * The meta object literal for the '<em><b>Target Group</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SYSTEM_WIRE__TARGET_GROUP = eINSTANCE.getSystemWire_TargetGroup();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EReference SYSTEM_WIRE__TARGET = eINSTANCE.getSystemWire_Target();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute SYSTEM_WIRE__ANY = eINSTANCE.getSystemWire_Any();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.WebServiceBindingImpl <em>Web Service Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.WebServiceBindingImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getWebServiceBinding()
     * @generated
     */
    public static final EClass WEB_SERVICE_BINDING = eINSTANCE.getWebServiceBinding();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WEB_SERVICE_BINDING__ANY = eINSTANCE.getWebServiceBinding_Any();

    /**
     * The meta object literal for the '<em><b>Port</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WEB_SERVICE_BINDING__PORT = eINSTANCE.getWebServiceBinding_Port();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WEB_SERVICE_BINDING__ANY_ATTRIBUTE = eINSTANCE.getWebServiceBinding_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.impl.WSDLPortTypeImpl <em>WSDL Port Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.impl.WSDLPortTypeImpl
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getWSDLPortType()
     * @generated
     */
    public static final EClass WSDL_PORT_TYPE = eINSTANCE.getWSDLPortType();

    /**
     * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WSDL_PORT_TYPE__ANY = eINSTANCE.getWSDLPortType_Any();

    /**
     * The meta object literal for the '<em><b>Callback Interface</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WSDL_PORT_TYPE__CALLBACK_INTERFACE = eINSTANCE.getWSDLPortType_CallbackInterface();

    /**
     * The meta object literal for the '<em><b>Interface</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WSDL_PORT_TYPE__INTERFACE = eINSTANCE.getWSDLPortType_Interface();

    /**
     * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EAttribute WSDL_PORT_TYPE__ANY_ATTRIBUTE = eINSTANCE.getWSDLPortType_AnyAttribute();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.Multiplicity <em>Multiplicity</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getMultiplicity()
     * @generated
     */
    public static final EEnum MULTIPLICITY = eINSTANCE.getMultiplicity();

    /**
     * The meta object literal for the '{@link org.apache.tuscany.model.assembly.scdl.OverrideOptions <em>Override Options</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.OverrideOptions
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getOverrideOptions()
     * @generated
     */
    public static final EEnum OVERRIDE_OPTIONS = eINSTANCE.getOverrideOptions();

    /**
     * The meta object literal for the '<em>Multiplicity Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getMultiplicityObject()
     * @generated
     */
    public static final EDataType MULTIPLICITY_OBJECT = eINSTANCE.getMultiplicityObject();

    /**
     * The meta object literal for the '<em>Override Options Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.scdl.OverrideOptions
     * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getOverrideOptionsObject()
     * @generated
     */
    public static final EDataType OVERRIDE_OPTIONS_OBJECT = eINSTANCE.getOverrideOptionsObject();

  }

} //SCDLPackageImpl
