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
import org.apache.tuscany.model.assembly.scdl.Interface;
import org.apache.tuscany.model.assembly.scdl.Multiplicity;

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
 * An implementation of the model object '<em><b>Entry Point</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getInterfaceGroup <em>Interface Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getBindingGroup <em>Binding Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.EntryPointImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EntryPointImpl extends DataObjectImpl implements EntryPoint
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
   * The cached value of the '{@link #getReference() <em>Reference</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReference()
   * @generated
   * @ordered
   */
  protected EList reference = null;

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
   * The default value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultiplicity()
   * @generated
   * @ordered
   */
  protected static final Multiplicity MULTIPLICITY_EDEFAULT = Multiplicity._01_LITERAL;

  /**
   * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultiplicity()
   * @generated
   * @ordered
   */
  protected Multiplicity multiplicity = MULTIPLICITY_EDEFAULT;

  /**
   * This is true if the Multiplicity attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean multiplicityESet = false;

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
  protected EntryPointImpl()
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
    return SCDLPackageImpl.Literals.ENTRY_POINT;
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
      interfaceGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.ENTRY_POINT__INTERFACE_GROUP));
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
    return (Interface)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap().get(SCDLPackageImpl.Literals.ENTRY_POINT__INTERFACE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterface(Interface newInterface)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).set(SCDLPackageImpl.Literals.ENTRY_POINT__INTERFACE, newInterface);
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
      bindingGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.ENTRY_POINT__BINDING_GROUP));
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
    return ((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap().list(SCDLPackageImpl.Literals.ENTRY_POINT__BINDING);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getReference()
  {
    if (reference == null)
    {
      reference = new BasicInternalEList(String.class);
    }
    return reference;
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
      any = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.ENTRY_POINT__ANY));
    }
    return any;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multiplicity getMultiplicity()
  {
    return multiplicity;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMultiplicity(Multiplicity newMultiplicity)
  {
    multiplicity = newMultiplicity == null ? MULTIPLICITY_EDEFAULT : newMultiplicity;
    multiplicityESet = true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetMultiplicity()
  {
    multiplicity = MULTIPLICITY_EDEFAULT;
    multiplicityESet = false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetMultiplicity()
  {
    return multiplicityESet;
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
  public Sequence getAnyAttribute()
  {
    if (anyAttribute == null)
    {
      anyAttribute = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.ENTRY_POINT__ANY_ATTRIBUTE));
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
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.ENTRY_POINT__BINDING_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.ENTRY_POINT__ANY:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.ENTRY_POINT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap();
        return getInterfaceGroup();
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE:
        return getInterface();
      case SCDLPackageImpl.ENTRY_POINT__BINDING_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap();
        return getBindingGroup();
      case SCDLPackageImpl.ENTRY_POINT__BINDING:
        return getBinding();
      case SCDLPackageImpl.ENTRY_POINT__REFERENCE:
        return getReference();
      case SCDLPackageImpl.ENTRY_POINT__ANY:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAny()).featureMap();
        return getAny();
      case SCDLPackageImpl.ENTRY_POINT__MULTIPLICITY:
        return getMultiplicity();
      case SCDLPackageImpl.ENTRY_POINT__NAME:
        return getName();
      case SCDLPackageImpl.ENTRY_POINT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE:
        setInterface((Interface)newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__BINDING_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__BINDING:
        getBinding().clear();
        getBinding().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__REFERENCE:
        getReference().clear();
        getReference().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__ANY:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__MULTIPLICITY:
        setMultiplicity((Multiplicity)newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__NAME:
        setName((String)newValue);
        return;
      case SCDLPackageImpl.ENTRY_POINT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE_GROUP:
        ((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE:
        setInterface((Interface)null);
        return;
      case SCDLPackageImpl.ENTRY_POINT__BINDING_GROUP:
        ((FeatureMap.Internal.Wrapper)getBindingGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.ENTRY_POINT__BINDING:
        getBinding().clear();
        return;
      case SCDLPackageImpl.ENTRY_POINT__REFERENCE:
        getReference().clear();
        return;
      case SCDLPackageImpl.ENTRY_POINT__ANY:
        ((FeatureMap.Internal.Wrapper)getAny()).featureMap().clear();
        return;
      case SCDLPackageImpl.ENTRY_POINT__MULTIPLICITY:
        unsetMultiplicity();
        return;
      case SCDLPackageImpl.ENTRY_POINT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case SCDLPackageImpl.ENTRY_POINT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE_GROUP:
        return interfaceGroup != null && !interfaceGroup.featureMap().isEmpty();
      case SCDLPackageImpl.ENTRY_POINT__INTERFACE:
        return getInterface() != null;
      case SCDLPackageImpl.ENTRY_POINT__BINDING_GROUP:
        return bindingGroup != null && !bindingGroup.featureMap().isEmpty();
      case SCDLPackageImpl.ENTRY_POINT__BINDING:
        return !getBinding().isEmpty();
      case SCDLPackageImpl.ENTRY_POINT__REFERENCE:
        return reference != null && !reference.isEmpty();
      case SCDLPackageImpl.ENTRY_POINT__ANY:
        return any != null && !any.featureMap().isEmpty();
      case SCDLPackageImpl.ENTRY_POINT__MULTIPLICITY:
        return isSetMultiplicity();
      case SCDLPackageImpl.ENTRY_POINT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case SCDLPackageImpl.ENTRY_POINT__ANY_ATTRIBUTE:
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
    result.append(", reference: ");
    result.append(reference);
    result.append(", any: ");
    result.append(any);
    result.append(", multiplicity: ");
    if (multiplicityESet) result.append(multiplicity); else result.append("<unset>");
    result.append(", name: ");
    result.append(name);
    result.append(", anyAttribute: ");
    result.append(anyAttribute);
    result.append(')');
    return result.toString();
  }

} //EntryPointImpl
