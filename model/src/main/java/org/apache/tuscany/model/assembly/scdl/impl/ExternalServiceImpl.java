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

import org.apache.tuscany.model.assembly.scdl.ExternalService;
import org.apache.tuscany.model.assembly.scdl.Interface;
import org.apache.tuscany.model.assembly.scdl.OverrideOptions;

import org.apache.tuscany.sdo.impl.DataObjectImpl;

import org.apache.tuscany.sdo.util.BasicSequence;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>External Service</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getInterfaceGroup <em>Interface Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getBindingGroup <em>Binding Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getOverridable <em>Overridable</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ExternalServiceImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExternalServiceImpl extends DataObjectImpl implements ExternalService
{
  /**
   * The cached value of the '{@link #getInterfaceGroup() <em>Interface Group</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInterfaceGroup()
   * @generated
   * @ordered
   */
  protected BasicSequence interfaceGroup = null;

  /**
   * The cached value of the '{@link #getBindingGroup() <em>Binding Group</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBindingGroup()
   * @generated
   * @ordered
   */
  protected BasicSequence bindingGroup = null;

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
   * The default value of the '{@link #getOverridable() <em>Overridable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOverridable()
   * @generated
   * @ordered
   */
  protected static final OverrideOptions OVERRIDABLE_EDEFAULT = OverrideOptions.MAY_LITERAL;

  /**
   * The cached value of the '{@link #getOverridable() <em>Overridable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOverridable()
   * @generated
   * @ordered
   */
  protected OverrideOptions overridable = OVERRIDABLE_EDEFAULT;

  /**
   * This is true if the Overridable attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean overridableESet = false;

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
  protected ExternalServiceImpl()
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
    return SCDLPackageImpl.Literals.EXTERNAL_SERVICE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getInterfaceGroup()
  {
    if (interfaceGroup == null)
    {
      interfaceGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE_GROUP));
    }
    return interfaceGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Interface getInterface()
  {
    return (Interface)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap().get(SCDLPackageImpl.Literals.EXTERNAL_SERVICE__INTERFACE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterface(Interface newInterface)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).set(SCDLPackageImpl.Literals.EXTERNAL_SERVICE__INTERFACE, newInterface);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getBindingGroup()
  {
    if (bindingGroup == null)
    {
      bindingGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.EXTERNAL_SERVICE__BINDING_GROUP));
    }
    return bindingGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getBinding()
  {
    return ((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap().list(SCDLPackageImpl.Literals.EXTERNAL_SERVICE__BINDING);
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
  public OverrideOptions getOverridable()
  {
    return overridable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOverridable(OverrideOptions newOverridable)
  {
    overridable = newOverridable == null ? OVERRIDABLE_EDEFAULT : newOverridable;
    overridableESet = true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetOverridable()
  {
    overridable = OVERRIDABLE_EDEFAULT;
    overridableESet = false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetOverridable()
  {
    return overridableESet;
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
      anyAttribute = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.EXTERNAL_SERVICE__ANY_ATTRIBUTE));
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
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap();
        return getInterfaceGroup();
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE:
        return getInterface();
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap();
        return getBindingGroup();
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING:
        return getBinding();
      case SCDLPackageImpl.EXTERNAL_SERVICE__NAME:
        return getName();
      case SCDLPackageImpl.EXTERNAL_SERVICE__OVERRIDABLE:
        return getOverridable();
      case SCDLPackageImpl.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE:
        setInterface((Interface)newValue);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING:
        getBinding().clear();
        getBinding().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__NAME:
        setName((String)newValue);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__OVERRIDABLE:
        setOverridable((OverrideOptions)newValue);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE_GROUP:
        ((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE:
        setInterface((Interface)null);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING_GROUP:
        ((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING:
        getBinding().clear();
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__OVERRIDABLE:
        unsetOverridable();
        return;
      case SCDLPackageImpl.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE_GROUP:
        return interfaceGroup != null && !interfaceGroup.featureMap().isEmpty();
      case SCDLPackageImpl.EXTERNAL_SERVICE__INTERFACE:
        return getInterface() != null;
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING_GROUP:
        return bindingGroup != null && !bindingGroup.featureMap().isEmpty();
      case SCDLPackageImpl.EXTERNAL_SERVICE__BINDING:
        return !getBinding().isEmpty();
      case SCDLPackageImpl.EXTERNAL_SERVICE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case SCDLPackageImpl.EXTERNAL_SERVICE__OVERRIDABLE:
        return isSetOverridable();
      case SCDLPackageImpl.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
    result.append(" (interfaceGroup: ");
    result.append(interfaceGroup);
    result.append(", bindingGroup: ");
    result.append(bindingGroup);
    result.append(", name: ");
    result.append(name);
    result.append(", overridable: ");
    if (overridableESet) result.append(overridable); else result.append("<unset>");
    result.append(", anyAttribute: ");
    result.append(anyAttribute);
    result.append(')');
    return result.toString();
  }

} //ExternalServiceImpl
