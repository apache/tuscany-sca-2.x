/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import commonj.sdo.Sequence;

import java.util.Collection;
import java.util.List;

import org.apache.tuscany.model.assembly.scdl.EntryPoint;
import org.apache.tuscany.model.assembly.scdl.ExternalService;
import org.apache.tuscany.model.assembly.scdl.ModuleComponent;
import org.apache.tuscany.model.assembly.scdl.Subsystem;
import org.apache.tuscany.model.assembly.scdl.SystemWire;

import org.apache.tuscany.sdo.impl.DataObjectImpl;

import org.apache.tuscany.sdo.util.BasicSequence;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Subsystem</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getEntryPoint <em>Entry Point</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getModuleComponent <em>Module Component</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getExternalService <em>External Service</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getWire <em>Wire</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SubsystemImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SubsystemImpl extends DataObjectImpl implements Subsystem
{
  /**
   * The cached value of the '{@link #getEntryPoint() <em>Entry Point</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEntryPoint()
   * @generated
   * @ordered
   */
  protected EList entryPoint = null;

  /**
   * The cached value of the '{@link #getModuleComponent() <em>Module Component</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModuleComponent()
   * @generated
   * @ordered
   */
  protected EList moduleComponent = null;

  /**
   * The cached value of the '{@link #getExternalService() <em>External Service</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExternalService()
   * @generated
   * @ordered
   */
  protected EList externalService = null;

  /**
   * The cached value of the '{@link #getWire() <em>Wire</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWire()
   * @generated
   * @ordered
   */
  protected EList wire = null;

  /**
   * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAny()
   * @generated
   * @ordered
   */
  protected BasicSequence any = null;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUri()
   * @generated
   * @ordered
   */
  protected static final String URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUri()
   * @generated
   * @ordered
   */
  protected String uri = URI_EDEFAULT;

  /**
   * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAnyAttribute()
   * @generated
   * @ordered
   */
  protected BasicSequence anyAttribute = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SubsystemImpl()
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
    return SCDLPackageImpl.Literals.SUBSYSTEM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getEntryPoint()
  {
    if (entryPoint == null)
    {
      entryPoint = new BasicInternalEList(EntryPoint.class);
    }
    return entryPoint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getModuleComponent()
  {
    if (moduleComponent == null)
    {
      moduleComponent = new BasicInternalEList(ModuleComponent.class);
    }
    return moduleComponent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getExternalService()
  {
    if (externalService == null)
    {
      externalService = new BasicInternalEList(ExternalService.class);
    }
    return externalService;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getWire()
  {
    if (wire == null)
    {
      wire = new BasicInternalEList(SystemWire.class);
    }
    return wire;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getAny()
  {
    if (any == null)
    {
      any = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.SUBSYSTEM__ANY));
    }
    return any;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    name = newName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getUri()
  {
    return uri;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUri(String newUri)
  {
    uri = newUri;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getAnyAttribute()
  {
    if (anyAttribute == null)
    {
      anyAttribute = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.SUBSYSTEM__ANY_ATTRIBUTE));
    }
    return anyAttribute;
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
      case SCDLPackageImpl.SUBSYSTEM__ANY:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.SUBSYSTEM__ANY_ATTRIBUTE:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAnyAttribute()).featureMap()).basicRemove(otherEnd, msgs);
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
      case SCDLPackageImpl.SUBSYSTEM__ENTRY_POINT:
        return getEntryPoint();
      case SCDLPackageImpl.SUBSYSTEM__MODULE_COMPONENT:
        return getModuleComponent();
      case SCDLPackageImpl.SUBSYSTEM__EXTERNAL_SERVICE:
        return getExternalService();
      case SCDLPackageImpl.SUBSYSTEM__WIRE:
        return getWire();
      case SCDLPackageImpl.SUBSYSTEM__ANY:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAny()).featureMap();
        return getAny();
      case SCDLPackageImpl.SUBSYSTEM__NAME:
        return getName();
      case SCDLPackageImpl.SUBSYSTEM__URI:
        return getUri();
      case SCDLPackageImpl.SUBSYSTEM__ANY_ATTRIBUTE:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAnyAttribute()).featureMap();
        return getAnyAttribute();
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
      case SCDLPackageImpl.SUBSYSTEM__ENTRY_POINT:
        getEntryPoint().clear();
        getEntryPoint().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__MODULE_COMPONENT:
        getModuleComponent().clear();
        getModuleComponent().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__EXTERNAL_SERVICE:
        getExternalService().clear();
        getExternalService().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__WIRE:
        getWire().clear();
        getWire().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__ANY:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__NAME:
        setName((String)newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__URI:
        setUri((String)newValue);
        return;
      case SCDLPackageImpl.SUBSYSTEM__ANY_ATTRIBUTE:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAnyAttribute()).featureMap()).set(newValue);
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
      case SCDLPackageImpl.SUBSYSTEM__ENTRY_POINT:
        getEntryPoint().clear();
        return;
      case SCDLPackageImpl.SUBSYSTEM__MODULE_COMPONENT:
        getModuleComponent().clear();
        return;
      case SCDLPackageImpl.SUBSYSTEM__EXTERNAL_SERVICE:
        getExternalService().clear();
        return;
      case SCDLPackageImpl.SUBSYSTEM__WIRE:
        getWire().clear();
        return;
      case SCDLPackageImpl.SUBSYSTEM__ANY:
        ((FeatureMap.Internal.Wrapper)getAny()).featureMap().clear();
        return;
      case SCDLPackageImpl.SUBSYSTEM__NAME:
        setName(NAME_EDEFAULT);
        return;
      case SCDLPackageImpl.SUBSYSTEM__URI:
        setUri(URI_EDEFAULT);
        return;
      case SCDLPackageImpl.SUBSYSTEM__ANY_ATTRIBUTE:
        ((FeatureMap.Internal.Wrapper)getAnyAttribute()).featureMap().clear();
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
      case SCDLPackageImpl.SUBSYSTEM__ENTRY_POINT:
        return entryPoint != null && !entryPoint.isEmpty();
      case SCDLPackageImpl.SUBSYSTEM__MODULE_COMPONENT:
        return moduleComponent != null && !moduleComponent.isEmpty();
      case SCDLPackageImpl.SUBSYSTEM__EXTERNAL_SERVICE:
        return externalService != null && !externalService.isEmpty();
      case SCDLPackageImpl.SUBSYSTEM__WIRE:
        return wire != null && !wire.isEmpty();
      case SCDLPackageImpl.SUBSYSTEM__ANY:
        return any != null && !any.featureMap().isEmpty();
      case SCDLPackageImpl.SUBSYSTEM__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case SCDLPackageImpl.SUBSYSTEM__URI:
        return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
      case SCDLPackageImpl.SUBSYSTEM__ANY_ATTRIBUTE:
        return anyAttribute != null && !anyAttribute.featureMap().isEmpty();
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
    result.append(" (any: ");
    result.append(any);
    result.append(", name: ");
    result.append(name);
    result.append(", uri: ");
    result.append(uri);
    result.append(", anyAttribute: ");
    result.append(anyAttribute);
    result.append(')');
    return result.toString();
  }

} //SubsystemImpl
