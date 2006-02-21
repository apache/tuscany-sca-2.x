/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import commonj.sdo.Sequence;

import java.util.Map;

import org.apache.tuscany.model.assembly.scdl.Binding;
import org.apache.tuscany.model.assembly.scdl.ComponentType;
import org.apache.tuscany.model.assembly.scdl.DocumentRoot;
import org.apache.tuscany.model.assembly.scdl.Implementation;
import org.apache.tuscany.model.assembly.scdl.Interface;
import org.apache.tuscany.model.assembly.scdl.JavaImplementation;
import org.apache.tuscany.model.assembly.scdl.JavaInterface;
import org.apache.tuscany.model.assembly.scdl.Module;
import org.apache.tuscany.model.assembly.scdl.ModuleFragment;
import org.apache.tuscany.model.assembly.scdl.SCABinding;
import org.apache.tuscany.model.assembly.scdl.Subsystem;
import org.apache.tuscany.model.assembly.scdl.WSDLPortType;
import org.apache.tuscany.model.assembly.scdl.WebServiceBinding;

import org.apache.tuscany.sdo.impl.DataObjectImpl;

import org.apache.tuscany.sdo.util.BasicSequence;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getBindingSca <em>Binding Sca</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getBindingWs <em>Binding Ws</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getImplementationJava <em>Implementation Java</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getInterfaceJava <em>Interface Java</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getInterfaceWsdl <em>Interface Wsdl</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getModule <em>Module</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getModuleFragment <em>Module Fragment</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getSourceEpr <em>Source Epr</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getSourceUri <em>Source Uri</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getSubsystem <em>Subsystem</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getTargetEpr <em>Target Epr</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.DocumentRootImpl#getTargetUri <em>Target Uri</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DocumentRootImpl extends DataObjectImpl implements DocumentRoot
{
  /**
   * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMixed()
   * @generated
   * @ordered
   */
  protected BasicSequence mixed = null;

  /**
   * The cached value of the '{@link #getXMLNSPrefixMap() <em>XMLNS Prefix Map</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getXMLNSPrefixMap()
   * @generated
   * @ordered
   */
  protected EMap xMLNSPrefixMap = null;

  /**
   * The cached value of the '{@link #getXSISchemaLocation() <em>XSI Schema Location</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getXSISchemaLocation()
   * @generated
   * @ordered
   */
  protected EMap xSISchemaLocation = null;

  /**
   * The default value of the '{@link #getSourceUri() <em>Source Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceUri()
   * @generated
   * @ordered
   */
  protected static final String SOURCE_URI_EDEFAULT = null;

  /**
   * The default value of the '{@link #getTargetUri() <em>Target Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetUri()
   * @generated
   * @ordered
   */
  protected static final String TARGET_URI_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DocumentRootImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return SCDLPackageImpl.Literals.DOCUMENT_ROOT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getMixed()
  {
    if (mixed == null)
    {
      mixed = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.DOCUMENT_ROOT__MIXED));
    }
    return mixed;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map getXMLNSPrefixMap()
  {
    if (xMLNSPrefixMap == null)
    {
      xMLNSPrefixMap = new EcoreEMap(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, SCDLPackageImpl.DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
    }
    return xMLNSPrefixMap.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map getXSISchemaLocation()
  {
    if (xSISchemaLocation == null)
    {
      xSISchemaLocation = new EcoreEMap(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, SCDLPackageImpl.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
    }
    return xSISchemaLocation.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Binding getBinding()
  {
    return (Binding)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__BINDING, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBinding(Binding newBinding)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__BINDING, newBinding);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SCABinding getBindingSca()
  {
    return (SCABinding)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__BINDING_SCA, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBindingSca(SCABinding newBindingSca)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__BINDING_SCA, newBindingSca);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WebServiceBinding getBindingWs()
  {
    return (WebServiceBinding)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__BINDING_WS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBindingWs(WebServiceBinding newBindingWs)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__BINDING_WS, newBindingWs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComponentType getComponentType()
  {
    return (ComponentType)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__COMPONENT_TYPE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComponentType(ComponentType newComponentType)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__COMPONENT_TYPE, newComponentType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Implementation getImplementation()
  {
    return (Implementation)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__IMPLEMENTATION, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setImplementation(Implementation newImplementation)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__IMPLEMENTATION, newImplementation);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JavaImplementation getImplementationJava()
  {
    return (JavaImplementation)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__IMPLEMENTATION_JAVA, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setImplementationJava(JavaImplementation newImplementationJava)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__IMPLEMENTATION_JAVA, newImplementationJava);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Interface getInterface()
  {
    return (Interface)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__INTERFACE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterface(Interface newInterface)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__INTERFACE, newInterface);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JavaInterface getInterfaceJava()
  {
    return (JavaInterface)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__INTERFACE_JAVA, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterfaceJava(JavaInterface newInterfaceJava)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__INTERFACE_JAVA, newInterfaceJava);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WSDLPortType getInterfaceWsdl()
  {
    return (WSDLPortType)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__INTERFACE_WSDL, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterfaceWsdl(WSDLPortType newInterfaceWsdl)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__INTERFACE_WSDL, newInterfaceWsdl);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Module getModule()
  {
    return (Module)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__MODULE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setModule(Module newModule)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__MODULE, newModule);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModuleFragment getModuleFragment()
  {
    return (ModuleFragment)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__MODULE_FRAGMENT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setModuleFragment(ModuleFragment newModuleFragment)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__MODULE_FRAGMENT, newModuleFragment);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getSource()
  {
    return (Object)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SOURCE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSource(Object newSource)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SOURCE, newSource);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getSourceEpr()
  {
    return (Object)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SOURCE_EPR, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSourceEpr(Object newSourceEpr)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SOURCE_EPR, newSourceEpr);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getSourceUri()
  {
    return (String)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SOURCE_URI, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSourceUri(String newSourceUri)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SOURCE_URI, newSourceUri);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Subsystem getSubsystem()
  {
    return (Subsystem)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SUBSYSTEM, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSubsystem(Subsystem newSubsystem)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__SUBSYSTEM, newSubsystem);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getTarget()
  {
    return (Object)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__TARGET, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTarget(Object newTarget)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__TARGET, newTarget);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getTargetEpr()
  {
    return (Object)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__TARGET_EPR, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTargetEpr(Object newTargetEpr)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__TARGET_EPR, newTargetEpr);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTargetUri()
  {
    return (String)((FeatureMap.Internal.Wrapper)getMixed()).featureMap().get(SCDLPackageImpl.Literals.DOCUMENT_ROOT__TARGET_URI, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTargetUri(String newTargetUri)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(SCDLPackageImpl.Literals.DOCUMENT_ROOT__TARGET_URI, newTargetUri);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case SCDLPackageImpl.DOCUMENT_ROOT__MIXED:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case SCDLPackageImpl.DOCUMENT_ROOT__MIXED:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getMixed()).featureMap();
        return getMixed();
      case SCDLPackageImpl.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
        if (coreType) return ((EMap.InternalMapView)getXMLNSPrefixMap()).eMap();
        else return getXMLNSPrefixMap();
      case SCDLPackageImpl.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
        if (coreType) return ((EMap.InternalMapView)getXSISchemaLocation()).eMap();
        else return getXSISchemaLocation();
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING:
        return getBinding();
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_SCA:
        return getBindingSca();
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_WS:
        return getBindingWs();
      case SCDLPackageImpl.DOCUMENT_ROOT__COMPONENT_TYPE:
        return getComponentType();
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION:
        return getImplementation();
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION_JAVA:
        return getImplementationJava();
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE:
        return getInterface();
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_JAVA:
        return getInterfaceJava();
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_WSDL:
        return getInterfaceWsdl();
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE:
        return getModule();
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE_FRAGMENT:
        return getModuleFragment();
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE:
        return getSource();
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_EPR:
        return getSourceEpr();
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_URI:
        return getSourceUri();
      case SCDLPackageImpl.DOCUMENT_ROOT__SUBSYSTEM:
        return getSubsystem();
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET:
        return getTarget();
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_EPR:
        return getTargetEpr();
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_URI:
        return getTargetUri();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case SCDLPackageImpl.DOCUMENT_ROOT__MIXED:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getMixed()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
        ((EStructuralFeature.Setting)((EMap.InternalMapView)getXMLNSPrefixMap()).eMap()).set(newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
        ((EStructuralFeature.Setting)((EMap.InternalMapView)getXSISchemaLocation()).eMap()).set(newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING:
        setBinding((Binding)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_SCA:
        setBindingSca((SCABinding)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_WS:
        setBindingWs((WebServiceBinding)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__COMPONENT_TYPE:
        setComponentType((ComponentType)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION:
        setImplementation((Implementation)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION_JAVA:
        setImplementationJava((JavaImplementation)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE:
        setInterface((Interface)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_JAVA:
        setInterfaceJava((JavaInterface)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_WSDL:
        setInterfaceWsdl((WSDLPortType)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE:
        setModule((Module)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE_FRAGMENT:
        setModuleFragment((ModuleFragment)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE:
        setSource((Object)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_EPR:
        setSourceEpr((Object)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_URI:
        setSourceUri((String)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SUBSYSTEM:
        setSubsystem((Subsystem)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET:
        setTarget((Object)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_EPR:
        setTargetEpr((Object)newValue);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_URI:
        setTargetUri((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case SCDLPackageImpl.DOCUMENT_ROOT__MIXED:
        ((FeatureMap.Internal.Wrapper)getMixed()).featureMap().clear();
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
        getXMLNSPrefixMap().clear();
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
        getXSISchemaLocation().clear();
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING:
        setBinding((Binding)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_SCA:
        setBindingSca((SCABinding)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_WS:
        setBindingWs((WebServiceBinding)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__COMPONENT_TYPE:
        setComponentType((ComponentType)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION:
        setImplementation((Implementation)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION_JAVA:
        setImplementationJava((JavaImplementation)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE:
        setInterface((Interface)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_JAVA:
        setInterfaceJava((JavaInterface)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_WSDL:
        setInterfaceWsdl((WSDLPortType)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE:
        setModule((Module)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE_FRAGMENT:
        setModuleFragment((ModuleFragment)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE:
        setSource((Object)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_EPR:
        setSourceEpr((Object)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_URI:
        setSourceUri(SOURCE_URI_EDEFAULT);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__SUBSYSTEM:
        setSubsystem((Subsystem)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET:
        setTarget((Object)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_EPR:
        setTargetEpr((Object)null);
        return;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_URI:
        setTargetUri(TARGET_URI_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case SCDLPackageImpl.DOCUMENT_ROOT__MIXED:
        return mixed != null && !mixed.featureMap().isEmpty();
      case SCDLPackageImpl.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
        return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
      case SCDLPackageImpl.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
        return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING:
        return getBinding() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_SCA:
        return getBindingSca() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__BINDING_WS:
        return getBindingWs() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__COMPONENT_TYPE:
        return getComponentType() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION:
        return getImplementation() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__IMPLEMENTATION_JAVA:
        return getImplementationJava() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE:
        return getInterface() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_JAVA:
        return getInterfaceJava() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__INTERFACE_WSDL:
        return getInterfaceWsdl() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE:
        return getModule() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__MODULE_FRAGMENT:
        return getModuleFragment() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE:
        return getSource() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_EPR:
        return getSourceEpr() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__SOURCE_URI:
        return SOURCE_URI_EDEFAULT == null ? getSourceUri() != null : !SOURCE_URI_EDEFAULT.equals(getSourceUri());
      case SCDLPackageImpl.DOCUMENT_ROOT__SUBSYSTEM:
        return getSubsystem() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET:
        return getTarget() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_EPR:
        return getTargetEpr() != null;
      case SCDLPackageImpl.DOCUMENT_ROOT__TARGET_URI:
        return TARGET_URI_EDEFAULT == null ? getTargetUri() != null : !TARGET_URI_EDEFAULT.equals(getTargetUri());
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (mixed: ");
    result.append(mixed);
    result.append(')');
    return result.toString();
  }

} //DocumentRootImpl
