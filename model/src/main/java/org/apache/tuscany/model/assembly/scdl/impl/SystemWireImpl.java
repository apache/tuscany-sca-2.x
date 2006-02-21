/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl.impl;

import commonj.sdo.Sequence;

import org.apache.tuscany.model.assembly.scdl.SystemWire;

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
 * An implementation of the model object '<em><b>System Wire</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl#getSourceGroup <em>Source Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl#getTargetGroup <em>Target Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.impl.SystemWireImpl#getAny <em>Any</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SystemWireImpl extends DataObjectImpl implements SystemWire
{
  /**
   * The cached value of the '{@link #getSourceGroup() <em>Source Group</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceGroup()
   * @generated
   * @ordered
   */
  protected BasicSequence sourceGroup = null;

  /**
   * The cached value of the '{@link #getTargetGroup() <em>Target Group</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetGroup()
   * @generated
   * @ordered
   */
  protected BasicSequence targetGroup = null;

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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SystemWireImpl()
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
    return SCDLPackageImpl.Literals.SYSTEM_WIRE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getSourceGroup()
  {
    if (sourceGroup == null)
    {
      sourceGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.SYSTEM_WIRE__SOURCE_GROUP));
    }
    return sourceGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getSource()
  {
    return (Object)((FeatureMap.Internal.Wrapper)getSourceGroup()).featureMap().get(SCDLPackageImpl.Literals.SYSTEM_WIRE__SOURCE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSource(Object newSource)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getSourceGroup()).featureMap()).set(SCDLPackageImpl.Literals.SYSTEM_WIRE__SOURCE, newSource);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence getTargetGroup()
  {
    if (targetGroup == null)
    {
      targetGroup = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.SYSTEM_WIRE__TARGET_GROUP));
    }
    return targetGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getTarget()
  {
    return (Object)((FeatureMap.Internal.Wrapper)getTargetGroup()).featureMap().get(SCDLPackageImpl.Literals.SYSTEM_WIRE__TARGET, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTarget(Object newTarget)
  {
    ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getTargetGroup()).featureMap()).set(SCDLPackageImpl.Literals.SYSTEM_WIRE__TARGET, newTarget);
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
      any = new BasicSequence(new BasicFeatureMap(this, SCDLPackageImpl.SYSTEM_WIRE__ANY));
    }
    return any;
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
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getSourceGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET_GROUP:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getTargetGroup()).featureMap()).basicRemove(otherEnd, msgs);
      case SCDLPackageImpl.SYSTEM_WIRE__ANY:
        return ((InternalEList)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).basicRemove(otherEnd, msgs);
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
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getSourceGroup()).featureMap();
        return getSourceGroup();
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE:
        return getSource();
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET_GROUP:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getTargetGroup()).featureMap();
        return getTargetGroup();
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET:
        return getTarget();
      case SCDLPackageImpl.SYSTEM_WIRE__ANY:
        if (coreType) return ((FeatureMap.Internal.Wrapper)getAny()).featureMap();
        return getAny();
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
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getSourceGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE:
        setSource((Object)newValue);
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET_GROUP:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getTargetGroup()).featureMap()).set(newValue);
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET:
        setTarget((Object)newValue);
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__ANY:
        ((FeatureMap.Internal)((FeatureMap.Internal.Wrapper)getAny()).featureMap()).set(newValue);
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
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE_GROUP:
        ((FeatureMap.Internal.Wrapper)getSourceGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE:
        setSource((Object)null);
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET_GROUP:
        ((FeatureMap.Internal.Wrapper)getTargetGroup()).featureMap().clear();
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET:
        setTarget((Object)null);
        return;
      case SCDLPackageImpl.SYSTEM_WIRE__ANY:
        ((FeatureMap.Internal.Wrapper)getAny()).featureMap().clear();
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
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE_GROUP:
        return sourceGroup != null && !sourceGroup.featureMap().isEmpty();
      case SCDLPackageImpl.SYSTEM_WIRE__SOURCE:
        return getSource() != null;
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET_GROUP:
        return targetGroup != null && !targetGroup.featureMap().isEmpty();
      case SCDLPackageImpl.SYSTEM_WIRE__TARGET:
        return getTarget() != null;
      case SCDLPackageImpl.SYSTEM_WIRE__ANY:
        return any != null && !any.featureMap().isEmpty();
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
    result.append(" (sourceGroup: ");
    result.append(sourceGroup);
    result.append(", targetGroup: ");
    result.append(targetGroup);
    result.append(", any: ");
    result.append(any);
    result.append(')');
    return result.toString();
  }

} //SystemWireImpl
