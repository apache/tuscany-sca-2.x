/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import commonj.sdo.Sequence;

import org.apache.tuscany.model.assembly.scdl.Component;
import org.apache.tuscany.model.assembly.scdl.Implementation;
import org.apache.tuscany.model.assembly.scdl.PropertyValues;
import org.apache.tuscany.model.assembly.scdl.ReferenceValues;

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
 * An implementation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getImplementationGroup <em>Implementation Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getReferences <em>References</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentImpl extends DataObjectImpl implements Component
{
  /**
   * The cached value of the '{@link #getImplementationGroup() <em>Implementation Group</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImplementationGroup()
   * @generated
   * @ordered
   */
  protected BasicSequence implementationGroup = null;

  /**
   * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProperties()
   * @generated
   * @ordered
   */
  protected PropertyValues properties = null;

  /**
   * The cached value of the '{@link #getReferences() <em>References</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReferences()
   * @generated
   * @ordered
   */
  protected ReferenceValues references = null;

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
  protected ComponentImpl()
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
    return SCDLPackageImpl.Literals.COMPONENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getImplementationGroup()
  {
    if (implementationGroup == null)
    {
      implementationGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.COMPONENT__IMPLEMENTATION_GROUP));
    }
    return implementationGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Implementation getImplementation()
  {
    return (Implementation)((FeatureMap.Internal.Wrapper)getImplementationGroup()).featureMap().get(SCDLPackageImpl.Literals.COMPONENT__IMPLEMENTATION, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setImplementation(Implementation newImplementation)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getImplementationGroup()).featureMap()).set(SCDLPackageImpl.Literals.COMPONENT__IMPLEMENTATION, newImplementation);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertyValues getProperties()
  {
    return properties;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setProperties(PropertyValues newProperties)
  {
    properties = newProperties;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReferenceValues getReferences()
  {
    return references;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReferences(ReferenceValues newReferences)
  {
    references = newReferences;
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
      any = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.COMPONENT__ANY));
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
  public Sequence getAnyAttribute()
  {
    if (anyAttribute == null)
    {
      anyAttribute = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.COMPONENT__ANY_ATTRIBUTE));
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
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getImplementationGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.COMPONENT__ANY:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.COMPONENT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getImplementationGroup()).featureMap();
        return getImplementationGroup();
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION:
        return getImplementation();
      case SCDLPackageImpl.COMPONENT__PROPERTIES:
        return getProperties();
      case SCDLPackageImpl.COMPONENT__REFERENCES:
        return getReferences();
      case SCDLPackageImpl.COMPONENT__ANY:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAny()).featureMap();
        return getAny();
      case SCDLPackageImpl.COMPONENT__NAME:
        return getName();
      case SCDLPackageImpl.COMPONENT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getImplementationGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION:
        setImplementation((Implementation)newValue);
        return;
      case SCDLPackageImpl.COMPONENT__PROPERTIES:
        setProperties((PropertyValues)newValue);
        return;
      case SCDLPackageImpl.COMPONENT__REFERENCES:
        setReferences((ReferenceValues)newValue);
        return;
      case SCDLPackageImpl.COMPONENT__ANY:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.COMPONENT__NAME:
        setName((String)newValue);
        return;
      case SCDLPackageImpl.COMPONENT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION_GROUP:
        ((FeatureMap.Internal.Wrapper)getImplementationGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION:
        setImplementation((Implementation)null);
        return;
      case SCDLPackageImpl.COMPONENT__PROPERTIES:
        setProperties((PropertyValues)null);
        return;
      case SCDLPackageImpl.COMPONENT__REFERENCES:
        setReferences((ReferenceValues)null);
        return;
      case SCDLPackageImpl.COMPONENT__ANY:
        ((FeatureMap.Internal.Wrapper)getAny()).featureMap().clear();
        return;
      case SCDLPackageImpl.COMPONENT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case SCDLPackageImpl.COMPONENT__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION_GROUP:
        return implementationGroup != null && !implementationGroup.featureMap().isEmpty();
      case SCDLPackageImpl.COMPONENT__IMPLEMENTATION:
        return getImplementation() != null;
      case SCDLPackageImpl.COMPONENT__PROPERTIES:
        return properties != null;
      case SCDLPackageImpl.COMPONENT__REFERENCES:
        return references != null;
      case SCDLPackageImpl.COMPONENT__ANY:
        return any != null && !any.featureMap().isEmpty();
      case SCDLPackageImpl.COMPONENT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case SCDLPackageImpl.COMPONENT__ANY_ATTRIBUTE:
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
    result.append(" (implementationGroup: ");
    result.append(implementationGroup);
    result.append(", any: ");
    result.append(any);
    result.append(", name: ");
    result.append(name);
    result.append(", anyAttribute: ");
    result.append(anyAttribute);
    result.append(')');
    return result.toString();
  }

} //ComponentImpl
