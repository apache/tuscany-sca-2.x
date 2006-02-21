/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import commonj.sdo.Sequence;

import org.apache.tuscany.model.assembly.scdl.Interface;
import org.apache.tuscany.model.assembly.scdl.Multiplicity;
import org.apache.tuscany.model.assembly.scdl.Reference;

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
 * An implementation of the model object '<em><b>Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl#getInterfaceGroup <em>Interface Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ReferenceImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReferenceImpl extends DataObjectImpl implements Reference
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
  protected ReferenceImpl()
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
    return SCDLPackageImpl.Literals.REFERENCE;
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
      interfaceGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.REFERENCE__INTERFACE_GROUP));
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
    return (Interface)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap().get(SCDLPackageImpl.Literals.REFERENCE__INTERFACE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterface(Interface newInterface)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).set(SCDLPackageImpl.Literals.REFERENCE__INTERFACE, newInterface);
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
      any = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.REFERENCE__ANY));
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
      anyAttribute = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.REFERENCE__ANY_ATTRIBUTE));
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
      case SCDLPackageImpl.REFERENCE__INTERFACE_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.REFERENCE__ANY:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.REFERENCE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.REFERENCE__INTERFACE_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap();
        return getInterfaceGroup();
      case SCDLPackageImpl.REFERENCE__INTERFACE:
        return getInterface();
      case SCDLPackageImpl.REFERENCE__ANY:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAny()).featureMap();
        return getAny();
      case SCDLPackageImpl.REFERENCE__MULTIPLICITY:
        return getMultiplicity();
      case SCDLPackageImpl.REFERENCE__NAME:
        return getName();
      case SCDLPackageImpl.REFERENCE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.REFERENCE__INTERFACE_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.REFERENCE__INTERFACE:
        setInterface((Interface)newValue);
        return;
      case SCDLPackageImpl.REFERENCE__ANY:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.REFERENCE__MULTIPLICITY:
        setMultiplicity((Multiplicity)newValue);
        return;
      case SCDLPackageImpl.REFERENCE__NAME:
        setName((String)newValue);
        return;
      case SCDLPackageImpl.REFERENCE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.REFERENCE__INTERFACE_GROUP:
        ((FeatureMap.Internal.Wrapper)getInterfaceGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.REFERENCE__INTERFACE:
        setInterface((Interface)null);
        return;
      case SCDLPackageImpl.REFERENCE__ANY:
        ((FeatureMap.Internal.Wrapper)getAny()).featureMap().clear();
        return;
      case SCDLPackageImpl.REFERENCE__MULTIPLICITY:
        unsetMultiplicity();
        return;
      case SCDLPackageImpl.REFERENCE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case SCDLPackageImpl.REFERENCE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.REFERENCE__INTERFACE_GROUP:
        return interfaceGroup != null && !interfaceGroup.featureMap().isEmpty();
      case SCDLPackageImpl.REFERENCE__INTERFACE:
        return getInterface() != null;
      case SCDLPackageImpl.REFERENCE__ANY:
        return any != null && !any.featureMap().isEmpty();
      case SCDLPackageImpl.REFERENCE__MULTIPLICITY:
        return isSetMultiplicity();
      case SCDLPackageImpl.REFERENCE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case SCDLPackageImpl.REFERENCE__ANY_ATTRIBUTE:
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

} //ReferenceImpl
