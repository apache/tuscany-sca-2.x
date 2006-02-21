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

import org.apache.tuscany.model.assembly.scdl.ComponentType;
import org.apache.tuscany.model.assembly.scdl.Property;
import org.apache.tuscany.model.assembly.scdl.Reference;
import org.apache.tuscany.model.assembly.scdl.Service;

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
 * An implementation of the model object '<em><b>Component Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl#getService <em>Service</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl#getProperty <em>Property</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.ComponentTypeImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentTypeImpl extends DataObjectImpl implements ComponentType
{
  /**
   * The cached value of the '{@link #getService() <em>Service</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getService()
   * @generated
   * @ordered
   */
  protected EList service = null;

  /**
   * The cached value of the '{@link #getReference() <em>Reference</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReference()
   * @generated
   * @ordered
   */
  protected EList reference = null;

  /**
   * The cached value of the '{@link #getProperty() <em>Property</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProperty()
   * @generated
   * @ordered
   */
  protected EList property = null;

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
  protected ComponentTypeImpl()
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
    return SCDLPackageImpl.Literals.COMPONENT_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getService()
  {
    if (service == null)
    {
      service = new BasicInternalEList(Service.class);
    }
    return service;
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
      reference = new BasicInternalEList(Reference.class);
    }
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getProperty()
  {
    if (property == null)
    {
      property = new BasicInternalEList(Property.class);
    }
    return property;
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
      any = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.COMPONENT_TYPE__ANY));
    }
    return any;
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
      anyAttribute = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.COMPONENT_TYPE__ANY_ATTRIBUTE));
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
      case SCDLPackageImpl.COMPONENT_TYPE__ANY:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.COMPONENT_TYPE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT_TYPE__SERVICE:
        return getService();
      case SCDLPackageImpl.COMPONENT_TYPE__REFERENCE:
        return getReference();
      case SCDLPackageImpl.COMPONENT_TYPE__PROPERTY:
        return getProperty();
      case SCDLPackageImpl.COMPONENT_TYPE__ANY:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAny()).featureMap();
        return getAny();
      case SCDLPackageImpl.COMPONENT_TYPE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT_TYPE__SERVICE:
        getService().clear();
        getService().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__REFERENCE:
        getReference().clear();
        getReference().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__PROPERTY:
        getProperty().clear();
        getProperty().addAll((Collection)newValue);
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__ANY:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT_TYPE__SERVICE:
        getService().clear();
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__REFERENCE:
        getReference().clear();
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__PROPERTY:
        getProperty().clear();
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__ANY:
        ((FeatureMap.Internal.Wrapper)getAny()).featureMap().clear();
        return;
      case SCDLPackageImpl.COMPONENT_TYPE__ANY_ATTRIBUTE:
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
      case SCDLPackageImpl.COMPONENT_TYPE__SERVICE:
        return service != null && !service.isEmpty();
      case SCDLPackageImpl.COMPONENT_TYPE__REFERENCE:
        return reference != null && !reference.isEmpty();
      case SCDLPackageImpl.COMPONENT_TYPE__PROPERTY:
        return property != null && !property.isEmpty();
      case SCDLPackageImpl.COMPONENT_TYPE__ANY:
        return any != null && !any.featureMap().isEmpty();
      case SCDLPackageImpl.COMPONENT_TYPE__ANY_ATTRIBUTE:
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
    result.append(", anyAttribute: ");
    result.append(anyAttribute);
    result.append(')');
    return result.toString();
  }

} //ComponentTypeImpl
