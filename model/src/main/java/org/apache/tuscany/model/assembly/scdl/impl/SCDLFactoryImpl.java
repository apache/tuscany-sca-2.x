/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import org.apache.tuscany.model.assembly.scdl.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SCDLFactoryImpl extends EFactoryImpl implements SCDLFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final SCDLFactoryImpl eINSTANCE = init();

  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SCDLFactoryImpl init()
  {
    try
    {
      SCDLFactoryImpl theSCDLFactory = (SCDLFactoryImpl)EPackage.Registry.INSTANCE.getEFactory("http://www.osoa.org/xmlns/sca/0.9"); 
      if (theSCDLFactory != null)
      {
        return theSCDLFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SCDLFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SCDLFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case SCDLPackageImpl.BINDING: return (EObject)createBinding();
      case SCDLPackageImpl.COMPONENT: return (EObject)createComponent();
      case SCDLPackageImpl.COMPONENT_TYPE: return (EObject)createComponentType();
      case SCDLPackageImpl.DOCUMENT_ROOT: return (EObject)createDocumentRoot();
      case SCDLPackageImpl.ENTRY_POINT: return (EObject)createEntryPoint();
      case SCDLPackageImpl.EXTERNAL_SERVICE: return (EObject)createExternalService();
      case SCDLPackageImpl.IMPLEMENTATION: return (EObject)createImplementation();
      case SCDLPackageImpl.INTERFACE: return (EObject)createInterface();
      case SCDLPackageImpl.JAVA_IMPLEMENTATION: return (EObject)createJavaImplementation();
      case SCDLPackageImpl.JAVA_INTERFACE: return (EObject)createJavaInterface();
      case SCDLPackageImpl.MODULE: return (EObject)createModule();
      case SCDLPackageImpl.MODULE_COMPONENT: return (EObject)createModuleComponent();
      case SCDLPackageImpl.MODULE_FRAGMENT: return (EObject)createModuleFragment();
      case SCDLPackageImpl.MODULE_WIRE: return (EObject)createModuleWire();
      case SCDLPackageImpl.PROPERTY_VALUES: return (EObject)createPropertyValues();
      case SCDLPackageImpl.REFERENCE: return (EObject)createReference();
      case SCDLPackageImpl.REFERENCE_VALUES: return (EObject)createReferenceValues();
      case SCDLPackageImpl.SCA_BINDING: return (EObject)createSCABinding();
      case SCDLPackageImpl.SERVICE: return (EObject)createService();
      case SCDLPackageImpl.SUBSYSTEM: return (EObject)createSubsystem();
      case SCDLPackageImpl.SYSTEM_WIRE: return (EObject)createSystemWire();
      case SCDLPackageImpl.WEB_SERVICE_BINDING: return (EObject)createWebServiceBinding();
      case SCDLPackageImpl.WSDL_PORT_TYPE: return (EObject)createWSDLPortType();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case SCDLPackageImpl.MULTIPLICITY:
        return createMultiplicityFromString(eDataType, initialValue);
      case SCDLPackageImpl.OVERRIDE_OPTIONS:
        return createOverrideOptionsFromString(eDataType, initialValue);
      case SCDLPackageImpl.MULTIPLICITY_OBJECT:
        return createMultiplicityObjectFromString(eDataType, initialValue);
      case SCDLPackageImpl.OVERRIDE_OPTIONS_OBJECT:
        return createOverrideOptionsObjectFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case SCDLPackageImpl.MULTIPLICITY:
        return convertMultiplicityToString(eDataType, instanceValue);
      case SCDLPackageImpl.OVERRIDE_OPTIONS:
        return convertOverrideOptionsToString(eDataType, instanceValue);
      case SCDLPackageImpl.MULTIPLICITY_OBJECT:
        return convertMultiplicityObjectToString(eDataType, instanceValue);
      case SCDLPackageImpl.OVERRIDE_OPTIONS_OBJECT:
        return convertOverrideOptionsObjectToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Binding createBinding()
  {
    BindingImpl binding = new BindingImpl();
    return binding;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component createComponent()
  {
    ComponentImpl component = new ComponentImpl();
    return component;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComponentType createComponentType()
  {
    ComponentTypeImpl componentType = new ComponentTypeImpl();
    return componentType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DocumentRoot createDocumentRoot()
  {
    DocumentRootImpl documentRoot = new DocumentRootImpl();
    return documentRoot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EntryPoint createEntryPoint()
  {
    EntryPointImpl entryPoint = new EntryPointImpl();
    return entryPoint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExternalService createExternalService()
  {
    ExternalServiceImpl externalService = new ExternalServiceImpl();
    return externalService;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Implementation createImplementation()
  {
    ImplementationImpl implementation = new ImplementationImpl();
    return implementation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Interface createInterface()
  {
    InterfaceImpl interface_ = new InterfaceImpl();
    return interface_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JavaImplementation createJavaImplementation()
  {
    JavaImplementationImpl javaImplementation = new JavaImplementationImpl();
    return javaImplementation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JavaInterface createJavaInterface()
  {
    JavaInterfaceImpl javaInterface = new JavaInterfaceImpl();
    return javaInterface;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Module createModule()
  {
    ModuleImpl module = new ModuleImpl();
    return module;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModuleComponent createModuleComponent()
  {
    ModuleComponentImpl moduleComponent = new ModuleComponentImpl();
    return moduleComponent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModuleFragment createModuleFragment()
  {
    ModuleFragmentImpl moduleFragment = new ModuleFragmentImpl();
    return moduleFragment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModuleWire createModuleWire()
  {
    ModuleWireImpl moduleWire = new ModuleWireImpl();
    return moduleWire;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertyValues createPropertyValues()
  {
    PropertyValuesImpl propertyValues = new PropertyValuesImpl();
    return propertyValues;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Reference createReference()
  {
    ReferenceImpl reference = new ReferenceImpl();
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReferenceValues createReferenceValues()
  {
    ReferenceValuesImpl referenceValues = new ReferenceValuesImpl();
    return referenceValues;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SCABinding createSCABinding()
  {
    SCABindingImpl scaBinding = new SCABindingImpl();
    return scaBinding;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Service createService()
  {
    ServiceImpl service = new ServiceImpl();
    return service;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Subsystem createSubsystem()
  {
    SubsystemImpl subsystem = new SubsystemImpl();
    return subsystem;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SystemWire createSystemWire()
  {
    SystemWireImpl systemWire = new SystemWireImpl();
    return systemWire;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WebServiceBinding createWebServiceBinding()
  {
    WebServiceBindingImpl webServiceBinding = new WebServiceBindingImpl();
    return webServiceBinding;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WSDLPortType createWSDLPortType()
  {
    WSDLPortTypeImpl wsdlPortType = new WSDLPortTypeImpl();
    return wsdlPortType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multiplicity createMultiplicityFromString(EDataType eDataType, String initialValue)
  {
    Multiplicity result = Multiplicity.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMultiplicityToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OverrideOptions createOverrideOptionsFromString(EDataType eDataType, String initialValue)
  {
    OverrideOptions result = OverrideOptions.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOverrideOptionsToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multiplicity createMultiplicityObjectFromString(EDataType eDataType, String initialValue)
  {
    return (Multiplicity)createMultiplicityFromString(SCDLPackageImpl.Literals.MULTIPLICITY, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMultiplicityObjectToString(EDataType eDataType, Object instanceValue)
  {
    return convertMultiplicityToString(SCDLPackageImpl.Literals.MULTIPLICITY, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OverrideOptions createOverrideOptionsObjectFromString(EDataType eDataType, String initialValue)
  {
    return (OverrideOptions)createOverrideOptionsFromString(SCDLPackageImpl.Literals.OVERRIDE_OPTIONS, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOverrideOptionsObjectToString(EDataType eDataType, Object instanceValue)
  {
    return convertOverrideOptionsToString(SCDLPackageImpl.Literals.OVERRIDE_OPTIONS, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SCDLPackageImpl getSCDLPackageImpl()
  {
    return (SCDLPackageImpl)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  public static SCDLPackageImpl getPackage()
  {
    return SCDLPackageImpl.eINSTANCE;
  }

} //SCDLFactoryImpl
