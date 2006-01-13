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
import java.util.Map;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
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
import org.osoa.sca.model.Binding;
import org.osoa.sca.model.ComponentType;
import org.osoa.sca.model.DocumentRoot;
import org.osoa.sca.model.Implementation;
import org.osoa.sca.model.Interface;
import org.osoa.sca.model.JavaInterface;
import org.osoa.sca.model.Module;
import org.osoa.sca.model.ModuleFragment;
import org.osoa.sca.model.SCABinding;
import org.osoa.sca.model.Subsystem;
import org.osoa.sca.model.WSDLPortType;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link DocumentRootImpl#getMixed <em>Mixed</em>}</li>
 * <li>{@link DocumentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 * <li>{@link DocumentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 * <li>{@link DocumentRootImpl#getBinding <em>Binding</em>}</li>
 * <li>{@link DocumentRootImpl#getBindingSca <em>Binding Sca</em>}</li>
 * <li>{@link DocumentRootImpl#getComponentType <em>Component Type</em>}</li>
 * <li>{@link DocumentRootImpl#getImplementation <em>Implementation</em>}</li>
 * <li>{@link DocumentRootImpl#getInterface <em>Interface</em>}</li>
 * <li>{@link DocumentRootImpl#getInterfaceJava <em>Interface Java</em>}</li>
 * <li>{@link DocumentRootImpl#getInterfaceWsdl <em>Interface Wsdl</em>}</li>
 * <li>{@link DocumentRootImpl#getModule <em>Module</em>}</li>
 * <li>{@link DocumentRootImpl#getModuleFragment <em>Module Fragment</em>}</li>
 * <li>{@link DocumentRootImpl#getSource <em>Source</em>}</li>
 * <li>{@link DocumentRootImpl#getSourceEpr <em>Source Epr</em>}</li>
 * <li>{@link DocumentRootImpl#getSourceUri <em>Source Uri</em>}</li>
 * <li>{@link DocumentRootImpl#getSubsystem <em>Subsystem</em>}</li>
 * <li>{@link DocumentRootImpl#getTarget <em>Target</em>}</li>
 * <li>{@link DocumentRootImpl#getTargetEpr <em>Target Epr</em>}</li>
 * <li>{@link DocumentRootImpl#getTargetUri <em>Target Uri</em>}</li>
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
     *
     * @generated
     * @ordered
     * @see #getMixed()
     */
    protected ESequence mixed = null;

    /**
     * The cached value of the '{@link #getXMLNSPrefixMap() <em>XMLNS Prefix Map</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getXMLNSPrefixMap()
     */
    protected EMap xMLNSPrefixMap = null;

    /**
     * The cached value of the '{@link #getXSISchemaLocation() <em>XSI Schema Location</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getXSISchemaLocation()
     */
    protected EMap xSISchemaLocation = null;

    /**
     * The default value of the '{@link #getSourceUri() <em>Source Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getSourceUri()
     */
    protected static final String SOURCE_URI_EDEFAULT = null;

    /**
     * The default value of the '{@link #getTargetUri() <em>Target Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getTargetUri()
     */
    protected static final String TARGET_URI_EDEFAULT = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected DocumentRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getDocumentRoot();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getMixed() {
        if (mixed == null) {
            mixed = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.DOCUMENT_ROOT__MIXED));
        }
        return mixed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Map getXMLNSPrefixMap() {
        if (xMLNSPrefixMap == null) {
            xMLNSPrefixMap = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, AssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        }
        return xMLNSPrefixMap.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Map getXSISchemaLocation() {
        if (xSISchemaLocation == null) {
            xSISchemaLocation = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, AssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        }
        return xSISchemaLocation.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Binding getBinding() {
        return (Binding) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Binding(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetBinding(Binding newBinding, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Binding(), newBinding, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setBinding(Binding newBinding) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Binding(), newBinding);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public SCABinding getBindingSca() {
        return (SCABinding) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_BindingSca(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetBindingSca(SCABinding newBindingSca, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_BindingSca(), newBindingSca, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setBindingSca(SCABinding newBindingSca) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_BindingSca(), newBindingSca);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ComponentType getComponentType() {
        return (ComponentType) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_ComponentType(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetComponentType(ComponentType newComponentType, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_ComponentType(), newComponentType, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setComponentType(ComponentType newComponentType) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_ComponentType(), newComponentType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Implementation getImplementation() {
        return (Implementation) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Implementation(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetImplementation(Implementation newImplementation, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Implementation(), newImplementation, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setImplementation(Implementation newImplementation) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Implementation(), newImplementation);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Interface getInterface() {
        return (Interface) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Interface(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetInterface(Interface newInterface, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Interface(), newInterface, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setInterface(Interface newInterface) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Interface(), newInterface);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public JavaInterface getInterfaceJava() {
        return (JavaInterface) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_InterfaceJava(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetInterfaceJava(JavaInterface newInterfaceJava, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_InterfaceJava(), newInterfaceJava, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setInterfaceJava(JavaInterface newInterfaceJava) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_InterfaceJava(), newInterfaceJava);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public WSDLPortType getInterfaceWsdl() {
        return (WSDLPortType) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_InterfaceWsdl(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetInterfaceWsdl(WSDLPortType newInterfaceWsdl, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_InterfaceWsdl(), newInterfaceWsdl, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setInterfaceWsdl(WSDLPortType newInterfaceWsdl) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_InterfaceWsdl(), newInterfaceWsdl);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Module getModule() {
        return (Module) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Module(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetModule(Module newModule, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Module(), newModule, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setModule(Module newModule) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Module(), newModule);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModuleFragment getModuleFragment() {
        return (ModuleFragment) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_ModuleFragment(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetModuleFragment(ModuleFragment newModuleFragment, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_ModuleFragment(), newModuleFragment, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setModuleFragment(ModuleFragment newModuleFragment) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_ModuleFragment(), newModuleFragment);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getSource() {
        return (Object) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Source(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetSource(EObject newSource, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Source(), newSource, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setSource(Object newSource) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Source(), newSource);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getSourceEpr() {
        return (Object) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_SourceEpr(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetSourceEpr(EObject newSourceEpr, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_SourceEpr(), newSourceEpr, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setSourceEpr(Object newSourceEpr) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_SourceEpr(), newSourceEpr);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getSourceUri() {
        return (String) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_SourceUri(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setSourceUri(String newSourceUri) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_SourceUri(), newSourceUri);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Subsystem getSubsystem() {
        return (Subsystem) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Subsystem(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetSubsystem(Subsystem newSubsystem, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Subsystem(), newSubsystem, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setSubsystem(Subsystem newSubsystem) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Subsystem(), newSubsystem);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getTarget() {
        return (Object) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_Target(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetTarget(EObject newTarget, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_Target(), newTarget, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setTarget(Object newTarget) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_Target(), newTarget);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getTargetEpr() {
        return (Object) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_TargetEpr(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetTargetEpr(EObject newTargetEpr, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getDocumentRoot_TargetEpr(), newTargetEpr, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setTargetEpr(Object newTargetEpr) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_TargetEpr(), newTargetEpr);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getTargetUri() {
        return (String) ((ESequence) getMixed()).featureMap().get(AssemblyPackage.eINSTANCE.getDocumentRoot_TargetUri(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setTargetUri(String newTargetUri) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AssemblyPackage.eINSTANCE.getDocumentRoot_TargetUri(), newTargetUri);
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
            case AssemblyPackage.DOCUMENT_ROOT__MIXED:
                return ((InternalEList) ((ESequence) getMixed()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                return ((InternalEList) ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                return ((InternalEList) ((EMap.InternalMapView) getXSISchemaLocation()).eMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__BINDING:
                return basicSetBinding(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__BINDING_SCA:
                return basicSetBindingSca(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__COMPONENT_TYPE:
                return basicSetComponentType(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION:
                return basicSetImplementation(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__INTERFACE:
                return basicSetInterface(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_JAVA:
                return basicSetInterfaceJava(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_WSDL:
                return basicSetInterfaceWsdl(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__MODULE:
                return basicSetModule(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__MODULE_FRAGMENT:
                return basicSetModuleFragment(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__SOURCE:
                return basicSetSource(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__SOURCE_EPR:
                return basicSetSourceEpr(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__SUBSYSTEM:
                return basicSetSubsystem(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__TARGET:
                return basicSetTarget(null, msgs);
            case AssemblyPackage.DOCUMENT_ROOT__TARGET_EPR:
                return basicSetTargetEpr(null, msgs);
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
        case AssemblyPackage.DOCUMENT_ROOT__MIXED:
            return ((ESequence) getMixed()).featureMap();
        case AssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            return ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap();
        case AssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            return ((EMap.InternalMapView) getXSISchemaLocation()).eMap();
        case AssemblyPackage.DOCUMENT_ROOT__BINDING:
            return getBinding();
        case AssemblyPackage.DOCUMENT_ROOT__BINDING_SCA:
            return getBindingSca();
        case AssemblyPackage.DOCUMENT_ROOT__COMPONENT_TYPE:
            return getComponentType();
        case AssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION:
            return getImplementation();
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE:
            return getInterface();
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_JAVA:
            return getInterfaceJava();
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_WSDL:
            return getInterfaceWsdl();
        case AssemblyPackage.DOCUMENT_ROOT__MODULE:
            return getModule();
        case AssemblyPackage.DOCUMENT_ROOT__MODULE_FRAGMENT:
            return getModuleFragment();
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE:
            return getSource();
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_EPR:
            return getSourceEpr();
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_URI:
            return getSourceUri();
        case AssemblyPackage.DOCUMENT_ROOT__SUBSYSTEM:
            return getSubsystem();
        case AssemblyPackage.DOCUMENT_ROOT__TARGET:
            return getTarget();
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_EPR:
            return getTargetEpr();
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_URI:
            return getTargetUri();
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
        case AssemblyPackage.DOCUMENT_ROOT__MIXED:
            ((ESequence) getMixed()).featureMap().clear();
            ((ESequence) getMixed()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            getXMLNSPrefixMap().clear();
            ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            getXSISchemaLocation().clear();
            ((EMap.InternalMapView) getXSISchemaLocation()).eMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__BINDING:
            setBinding((Binding) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__BINDING_SCA:
            setBindingSca((SCABinding) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__COMPONENT_TYPE:
            setComponentType((ComponentType) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION:
            setImplementation((Implementation) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE:
            setInterface((Interface) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_JAVA:
            setInterfaceJava((JavaInterface) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_WSDL:
            setInterfaceWsdl((WSDLPortType) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__MODULE:
            setModule((Module) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__MODULE_FRAGMENT:
            setModuleFragment((ModuleFragment) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE:
            setSource((Object) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_EPR:
            setSourceEpr((Object) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_URI:
            setSourceUri((String) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SUBSYSTEM:
            setSubsystem((Subsystem) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET:
            setTarget((Object) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_EPR:
            setTargetEpr((Object) newValue);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_URI:
            setTargetUri((String) newValue);
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
        case AssemblyPackage.DOCUMENT_ROOT__MIXED:
            ((ESequence) getMixed()).featureMap().clear();
            return;
        case AssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            getXMLNSPrefixMap().clear();
            return;
        case AssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            getXSISchemaLocation().clear();
            return;
        case AssemblyPackage.DOCUMENT_ROOT__BINDING:
            setBinding((Binding) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__BINDING_SCA:
            setBindingSca((SCABinding) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__COMPONENT_TYPE:
            setComponentType((ComponentType) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION:
            setImplementation((Implementation) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE:
            setInterface((Interface) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_JAVA:
            setInterfaceJava((JavaInterface) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_WSDL:
            setInterfaceWsdl((WSDLPortType) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__MODULE:
            setModule((Module) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__MODULE_FRAGMENT:
            setModuleFragment((ModuleFragment) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE:
            setSource((Object) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_EPR:
            setSourceEpr((Object) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_URI:
            setSourceUri(SOURCE_URI_EDEFAULT);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__SUBSYSTEM:
            setSubsystem((Subsystem) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET:
            setTarget((Object) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_EPR:
            setTargetEpr((Object) null);
            return;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_URI:
            setTargetUri(TARGET_URI_EDEFAULT);
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
        case AssemblyPackage.DOCUMENT_ROOT__MIXED:
            return mixed != null && !mixed.featureMap().isEmpty();
        case AssemblyPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
        case AssemblyPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
        case AssemblyPackage.DOCUMENT_ROOT__BINDING:
            return getBinding() != null;
        case AssemblyPackage.DOCUMENT_ROOT__BINDING_SCA:
            return getBindingSca() != null;
        case AssemblyPackage.DOCUMENT_ROOT__COMPONENT_TYPE:
            return getComponentType() != null;
        case AssemblyPackage.DOCUMENT_ROOT__IMPLEMENTATION:
            return getImplementation() != null;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE:
            return getInterface() != null;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_JAVA:
            return getInterfaceJava() != null;
        case AssemblyPackage.DOCUMENT_ROOT__INTERFACE_WSDL:
            return getInterfaceWsdl() != null;
        case AssemblyPackage.DOCUMENT_ROOT__MODULE:
            return getModule() != null;
        case AssemblyPackage.DOCUMENT_ROOT__MODULE_FRAGMENT:
            return getModuleFragment() != null;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE:
            return getSource() != null;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_EPR:
            return getSourceEpr() != null;
        case AssemblyPackage.DOCUMENT_ROOT__SOURCE_URI:
            return SOURCE_URI_EDEFAULT == null ? getSourceUri() != null : !SOURCE_URI_EDEFAULT.equals(getSourceUri());
        case AssemblyPackage.DOCUMENT_ROOT__SUBSYSTEM:
            return getSubsystem() != null;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET:
            return getTarget() != null;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_EPR:
            return getTargetEpr() != null;
        case AssemblyPackage.DOCUMENT_ROOT__TARGET_URI:
            return TARGET_URI_EDEFAULT == null ? getTargetUri() != null : !TARGET_URI_EDEFAULT.equals(getTargetUri());
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
