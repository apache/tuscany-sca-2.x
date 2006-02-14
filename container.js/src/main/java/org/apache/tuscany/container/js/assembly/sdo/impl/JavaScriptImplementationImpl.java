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
package org.apache.tuscany.container.js.assembly.sdo.impl;

import commonj.sdo.Sequence;

import java.util.Collection;

import org.apache.tuscany.container.js.assembly.sdo.JavaScriptAssemblyPackage;
import org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.model.assembly.sdo.impl.ImplementationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Java Script Implementation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptImplementationImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptImplementationImpl#getScriptFile <em>Script File</em>}</li>
 *   <li>{@link org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptImplementationImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JavaScriptImplementationImpl extends ImplementationImpl implements JavaScriptImplementation {
	/**
	 * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAny()
	 * @generated
	 * @ordered
	 */
	protected ESequence any = null;

	/**
	 * The default value of the '{@link #getScriptFile() <em>Script File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScriptFile()
	 * @generated
	 * @ordered
	 */
	protected static final String SCRIPT_FILE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScriptFile() <em>Script File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScriptFile()
	 * @generated
	 * @ordered
	 */
	protected String scriptFile = SCRIPT_FILE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnyAttribute()
	 * @generated
	 * @ordered
	 */
	protected ESequence anyAttribute = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JavaScriptImplementationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return JavaScriptAssemblyPackage.eINSTANCE.getJavaScriptImplementation();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Sequence getAny() {
		if (any == null) {
			any = new BasicESequence(new BasicFeatureMap(this, JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY));
		}
		return any;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getScriptFile() {
		return scriptFile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setScriptFile(String newScriptFile) {
		String oldScriptFile = scriptFile;
		scriptFile = newScriptFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__SCRIPT_FILE, oldScriptFile, scriptFile));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Sequence getAnyAttribute() {
		if (anyAttribute == null) {
			anyAttribute = new BasicESequence(new BasicFeatureMap(this, JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE));
		}
		return anyAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY:
				return ((InternalEList)((ESequence)getAny()).featureMap()).basicRemove(otherEnd, msgs);
				case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE:
				return ((InternalEList)((ESequence)getAnyAttribute()).featureMap()).basicRemove(otherEnd, msgs);
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
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY:
				return ((ESequence)getAny()).featureMap();
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__SCRIPT_FILE:
				return getScriptFile();
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE:
				return ((ESequence)getAnyAttribute()).featureMap();
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
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY:
				((ESequence)getAny()).featureMap().clear();
				((ESequence)getAny()).featureMap().addAll((Collection)newValue);
				return;
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__SCRIPT_FILE:
				setScriptFile((String)newValue);
				return;
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE:
				((ESequence)getAnyAttribute()).featureMap().clear();
				((ESequence)getAnyAttribute()).featureMap().addAll((Collection)newValue);
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
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY:
				((ESequence)getAny()).featureMap().clear();
				return;
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__SCRIPT_FILE:
				setScriptFile(SCRIPT_FILE_EDEFAULT);
				return;
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE:
				((ESequence)getAnyAttribute()).featureMap().clear();
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
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY:
				return any != null && !any.featureMap().isEmpty();
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__SCRIPT_FILE:
				return SCRIPT_FILE_EDEFAULT == null ? scriptFile != null : !SCRIPT_FILE_EDEFAULT.equals(scriptFile);
			case JavaScriptAssemblyPackage.JAVA_SCRIPT_IMPLEMENTATION__ANY_ATTRIBUTE:
				return anyAttribute != null && !anyAttribute.featureMap().isEmpty();
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
		result.append(" (any: ");
		result.append(any);
		result.append(", scriptFile: ");
		result.append(scriptFile);
		result.append(", anyAttribute: ");
		result.append(anyAttribute);
		result.append(')');
		return result.toString();
	}

} //JavaScriptImplementationImpl
