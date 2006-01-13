/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.sdo.impl;

import java.util.Collection;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.SystemWire;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>System Wire</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link SystemWireImpl#getSourceGroup <em>Source Group</em>}</li>
 * <li>{@link SystemWireImpl#getSource <em>Source</em>}</li>
 * <li>{@link SystemWireImpl#getTargetGroup <em>Target Group</em>}</li>
 * <li>{@link SystemWireImpl#getTarget <em>Target</em>}</li>
 * <li>{@link SystemWireImpl#getAny <em>Any</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SystemWireImpl extends EDataObjectImpl implements SystemWire {
    /**
     * The cached value of the '{@link #getSourceGroup() <em>Source Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getSourceGroup()
     */
    protected ESequence sourceGroup = null;

    /**
     * The cached value of the '{@link #getTargetGroup() <em>Target Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getTargetGroup()
     */
    protected ESequence targetGroup = null;

    /**
     * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAny()
     */
    protected ESequence any = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected SystemWireImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getSystemWire();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getSourceGroup() {
        if (sourceGroup == null) {
            sourceGroup = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.SYSTEM_WIRE__SOURCE_GROUP));
        }
        return sourceGroup;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getSource() {
        return (Object) ((ESequence) getSourceGroup()).featureMap().get(AssemblyPackage.eINSTANCE.getSystemWire_Source(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetSource(EObject newSource, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getSourceGroup()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getSystemWire_Source(), newSource, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setSource(Object newSource) {
        ((FeatureMap.Internal) ((ESequence) getSourceGroup()).featureMap()).set(AssemblyPackage.eINSTANCE.getSystemWire_Source(), newSource);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getTargetGroup() {
        if (targetGroup == null) {
            targetGroup = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.SYSTEM_WIRE__TARGET_GROUP));
        }
        return targetGroup;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getTarget() {
        return (Object) ((ESequence) getTargetGroup()).featureMap().get(AssemblyPackage.eINSTANCE.getSystemWire_Target(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetTarget(EObject newTarget, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getTargetGroup()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getSystemWire_Target(), newTarget, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setTarget(Object newTarget) {
        ((FeatureMap.Internal) ((ESequence) getTargetGroup()).featureMap()).set(AssemblyPackage.eINSTANCE.getSystemWire_Target(), newTarget);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.SYSTEM_WIRE__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
            case AssemblyPackage.SYSTEM_WIRE__SOURCE_GROUP:
                return ((InternalEList) ((ESequence) getSourceGroup()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.SYSTEM_WIRE__SOURCE:
                return basicSetSource(null, msgs);
            case AssemblyPackage.SYSTEM_WIRE__TARGET_GROUP:
                return ((InternalEList) ((ESequence) getTargetGroup()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.SYSTEM_WIRE__TARGET:
                return basicSetTarget(null, msgs);
            case AssemblyPackage.SYSTEM_WIRE__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case AssemblyPackage.SYSTEM_WIRE__SOURCE_GROUP:
            return ((ESequence) getSourceGroup()).featureMap();
        case AssemblyPackage.SYSTEM_WIRE__SOURCE:
            return getSource();
        case AssemblyPackage.SYSTEM_WIRE__TARGET_GROUP:
            return ((ESequence) getTargetGroup()).featureMap();
        case AssemblyPackage.SYSTEM_WIRE__TARGET:
            return getTarget();
        case AssemblyPackage.SYSTEM_WIRE__ANY:
            return ((ESequence) getAny()).featureMap();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case AssemblyPackage.SYSTEM_WIRE__SOURCE_GROUP:
            ((ESequence) getSourceGroup()).featureMap().clear();
            ((ESequence) getSourceGroup()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.SYSTEM_WIRE__SOURCE:
            setSource((Object) newValue);
            return;
        case AssemblyPackage.SYSTEM_WIRE__TARGET_GROUP:
            ((ESequence) getTargetGroup()).featureMap().clear();
            ((ESequence) getTargetGroup()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.SYSTEM_WIRE__TARGET:
            setTarget((Object) newValue);
            return;
        case AssemblyPackage.SYSTEM_WIRE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case AssemblyPackage.SYSTEM_WIRE__SOURCE_GROUP:
            ((ESequence) getSourceGroup()).featureMap().clear();
            return;
        case AssemblyPackage.SYSTEM_WIRE__SOURCE:
            setSource((Object) null);
            return;
        case AssemblyPackage.SYSTEM_WIRE__TARGET_GROUP:
            ((ESequence) getTargetGroup()).featureMap().clear();
            return;
        case AssemblyPackage.SYSTEM_WIRE__TARGET:
            setTarget((Object) null);
            return;
        case AssemblyPackage.SYSTEM_WIRE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case AssemblyPackage.SYSTEM_WIRE__SOURCE_GROUP:
            return sourceGroup != null && !sourceGroup.featureMap().isEmpty();
        case AssemblyPackage.SYSTEM_WIRE__SOURCE:
            return getSource() != null;
        case AssemblyPackage.SYSTEM_WIRE__TARGET_GROUP:
            return targetGroup != null && !targetGroup.featureMap().isEmpty();
        case AssemblyPackage.SYSTEM_WIRE__TARGET:
            return getTarget() != null;
        case AssemblyPackage.SYSTEM_WIRE__ANY:
            return any != null && !any.featureMap().isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
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
