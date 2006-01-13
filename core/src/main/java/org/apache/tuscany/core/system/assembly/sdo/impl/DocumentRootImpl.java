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
package org.apache.tuscany.core.system.assembly.sdo.impl;

import java.util.Collection;
import java.util.Map;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.core.system.assembly.sdo.DocumentRoot;
import org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage;
import org.apache.tuscany.core.system.assembly.sdo.SystemBinding;
import org.apache.tuscany.core.system.assembly.sdo.SystemImplementation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl#getBindingSystem <em>Binding System</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl#getImplementationSystem <em>Implementation System</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DocumentRootImpl extends EDataObjectImpl implements DocumentRoot {
    /**
     * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMixed()
     * @generated
     * @ordered
     */
    protected ESequence mixed = null;

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DocumentRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return SystemAssemblyPackage.eINSTANCE.getDocumentRoot();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Sequence getMixed() {
        if (mixed == null) {
            mixed = new BasicESequence(new BasicFeatureMap(this, SystemAssemblyPackage.DOCUMENT_ROOT__MIXED));
        }
        return mixed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Map getXMLNSPrefixMap() {
        if (xMLNSPrefixMap == null) {
            xMLNSPrefixMap = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, SystemAssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        }
        return xMLNSPrefixMap.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Map getXSISchemaLocation() {
        if (xSISchemaLocation == null) {
            xSISchemaLocation = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, SystemAssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        }
        return xSISchemaLocation.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SystemBinding getBindingSystem() {
        return (SystemBinding)((ESequence)getMixed()).featureMap().get(SystemAssemblyPackage.eINSTANCE.getDocumentRoot_BindingSystem(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetBindingSystem(SystemBinding newBindingSystem, NotificationChain msgs) {
        return ((FeatureMap.Internal)((ESequence)getMixed()).featureMap()).basicAdd(SystemAssemblyPackage.eINSTANCE.getDocumentRoot_BindingSystem(), newBindingSystem, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBindingSystem(SystemBinding newBindingSystem) {
        ((FeatureMap.Internal)((ESequence)getMixed()).featureMap()).set(SystemAssemblyPackage.eINSTANCE.getDocumentRoot_BindingSystem(), newBindingSystem);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SystemImplementation getImplementationSystem() {
        return (SystemImplementation)((ESequence)getMixed()).featureMap().get(SystemAssemblyPackage.eINSTANCE.getDocumentRoot_ImplementationSystem(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetImplementationSystem(SystemImplementation newImplementationSystem, NotificationChain msgs) {
        return ((FeatureMap.Internal)((ESequence)getMixed()).featureMap()).basicAdd(SystemAssemblyPackage.eINSTANCE.getDocumentRoot_ImplementationSystem(), newImplementationSystem, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setImplementationSystem(SystemImplementation newImplementationSystem) {
        ((FeatureMap.Internal)((ESequence)getMixed()).featureMap()).set(SystemAssemblyPackage.eINSTANCE.getDocumentRoot_ImplementationSystem(), newImplementationSystem);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case SystemAssemblyPackage.DOCUMENT_ROOT__MIXED:
                return ((InternalEList)((ESequence)getMixed()).featureMap()).basicRemove(otherEnd, msgs);
                case SystemAssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                    return ((InternalEList)((EMap.InternalMapView)getXMLNSPrefixMap()).eMap()).basicRemove(otherEnd, msgs);
                case SystemAssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                    return ((InternalEList)((EMap.InternalMapView)getXSISchemaLocation()).eMap()).basicRemove(otherEnd, msgs);
                case SystemAssemblyPackage.DOCUMENT_ROOT__BINDING_SYSTEM:
                    return basicSetBindingSystem(null, msgs);
                case SystemAssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION_SYSTEM:
                    return basicSetImplementationSystem(null, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.DOCUMENT_ROOT__MIXED:
                return ((ESequence)getMixed()).featureMap();
            case SystemAssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                return ((EMap.InternalMapView)getXMLNSPrefixMap()).eMap();
            case SystemAssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                return ((EMap.InternalMapView)getXSISchemaLocation()).eMap();
            case SystemAssemblyPackage.DOCUMENT_ROOT__BINDING_SYSTEM:
                return getBindingSystem();
            case SystemAssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION_SYSTEM:
                return getImplementationSystem();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.DOCUMENT_ROOT__MIXED:
                ((ESequence)getMixed()).featureMap().clear();
                ((ESequence)getMixed()).featureMap().addAll((Collection)newValue);
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                getXMLNSPrefixMap().clear();
                ((EMap.InternalMapView)getXMLNSPrefixMap()).eMap().addAll((Collection)newValue);
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                getXSISchemaLocation().clear();
                ((EMap.InternalMapView)getXSISchemaLocation()).eMap().addAll((Collection)newValue);
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__BINDING_SYSTEM:
                setBindingSystem((SystemBinding)newValue);
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION_SYSTEM:
                setImplementationSystem((SystemImplementation)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.DOCUMENT_ROOT__MIXED:
                ((ESequence)getMixed()).featureMap().clear();
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                getXMLNSPrefixMap().clear();
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                getXSISchemaLocation().clear();
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__BINDING_SYSTEM:
                setBindingSystem((SystemBinding)null);
                return;
            case SystemAssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION_SYSTEM:
                setImplementationSystem((SystemImplementation)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.DOCUMENT_ROOT__MIXED:
                return mixed != null && !mixed.featureMap().isEmpty();
            case SystemAssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
            case SystemAssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
            case SystemAssemblyPackage.DOCUMENT_ROOT__BINDING_SYSTEM:
                return getBindingSystem() != null;
            case SystemAssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION_SYSTEM:
                return getImplementationSystem() != null;
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
        result.append(" (mixed: ");
        result.append(mixed);
        result.append(')');
        return result.toString();
    }

} //DocumentRootImpl
