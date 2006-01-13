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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.ModuleComponent;
import org.osoa.sca.model.PropertyValues;
import org.osoa.sca.model.ReferenceValues;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Component</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link ModuleComponentImpl#getProperties <em>Properties</em>}</li>
 * <li>{@link ModuleComponentImpl#getReferences <em>References</em>}</li>
 * <li>{@link ModuleComponentImpl#getAny <em>Any</em>}</li>
 * <li>{@link ModuleComponentImpl#getModule <em>Module</em>}</li>
 * <li>{@link ModuleComponentImpl#getName <em>Name</em>}</li>
 * <li>{@link ModuleComponentImpl#getUri <em>Uri</em>}</li>
 * <li>{@link ModuleComponentImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModuleComponentImpl extends EDataObjectImpl implements ModuleComponent {
    /**
     * The cached value of the '{@link #getPropertyValues() <em>Properties</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getPropertyValues()
     */
    protected PropertyValues properties = null;

    /**
     * The cached value of the '{@link #getReferenceValues() <em>References</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getReferenceValues()
     */
    protected ReferenceValues references = null;

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
     * The default value of the '{@link #getModule() <em>Module</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getModule()
     */
    protected static final String MODULE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getModule() <em>Module</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getModule()
     */
    protected String module = MODULE_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getName()
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getName()
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getUri()
     */
    protected static final String URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getUri()
     */
    protected String uri = URI_EDEFAULT;

    /**
     * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAnyAttribute()
     */
    protected ESequence anyAttribute = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected ModuleComponentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getModuleComponent();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public PropertyValues getPropertyValues() {
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetProperties(PropertyValues newProperties, NotificationChain msgs) {
        PropertyValues oldProperties = properties;
        properties = newProperties;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__PROPERTIES, oldProperties, newProperties);
            if (msgs == null) msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setPropertyValues(PropertyValues newProperties) {
        if (newProperties != properties) {
            NotificationChain msgs = null;
            if (properties != null)
                msgs = ((InternalEObject) properties).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.MODULE_COMPONENT__PROPERTIES, null, msgs);
            if (newProperties != null)
                msgs = ((InternalEObject) newProperties).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.MODULE_COMPONENT__PROPERTIES, null, msgs);
            msgs = basicSetProperties(newProperties, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__PROPERTIES, newProperties, newProperties));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReferenceValues getReferenceValues() {
        return references;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetReferences(ReferenceValues newReferences, NotificationChain msgs) {
        ReferenceValues oldReferences = references;
        references = newReferences;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__REFERENCES, oldReferences, newReferences);
            if (msgs == null) msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setReferenceValues(ReferenceValues newReferences) {
        if (newReferences != references) {
            NotificationChain msgs = null;
            if (references != null)
                msgs = ((InternalEObject) references).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.MODULE_COMPONENT__REFERENCES, null, msgs);
            if (newReferences != null)
                msgs = ((InternalEObject) newReferences).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.MODULE_COMPONENT__REFERENCES, null, msgs);
            msgs = basicSetReferences(newReferences, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__REFERENCES, newReferences, newReferences));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.MODULE_COMPONENT__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getModule() {
        return module;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setModule(String newModule) {
        String oldModule = module;
        module = newModule;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__MODULE, oldModule, module));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getUri() {
        return uri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setUri(String newUri) {
        String oldUri = uri;
        uri = newUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_COMPONENT__URI, oldUri, uri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.MODULE_COMPONENT__ANY_ATTRIBUTE));
        }
        return anyAttribute;
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
            case AssemblyPackage.MODULE_COMPONENT__PROPERTIES:
                return basicSetProperties(null, msgs);
            case AssemblyPackage.MODULE_COMPONENT__REFERENCES:
                return basicSetReferences(null, msgs);
            case AssemblyPackage.MODULE_COMPONENT__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE_COMPONENT__ANY_ATTRIBUTE:
                return ((InternalEList) ((ESequence) getAnyAttribute()).featureMap()).basicRemove(otherEnd, msgs);
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
        case AssemblyPackage.MODULE_COMPONENT__PROPERTIES:
            return getPropertyValues();
        case AssemblyPackage.MODULE_COMPONENT__REFERENCES:
            return getReferenceValues();
        case AssemblyPackage.MODULE_COMPONENT__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.MODULE_COMPONENT__MODULE:
            return getModule();
        case AssemblyPackage.MODULE_COMPONENT__NAME:
            return getName();
        case AssemblyPackage.MODULE_COMPONENT__URI:
            return getUri();
        case AssemblyPackage.MODULE_COMPONENT__ANY_ATTRIBUTE:
            return ((ESequence) getAnyAttribute()).featureMap();
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
        case AssemblyPackage.MODULE_COMPONENT__PROPERTIES:
            setPropertyValues((PropertyValues) newValue);
            return;
        case AssemblyPackage.MODULE_COMPONENT__REFERENCES:
            setReferenceValues((ReferenceValues) newValue);
            return;
        case AssemblyPackage.MODULE_COMPONENT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE_COMPONENT__MODULE:
            setModule((String) newValue);
            return;
        case AssemblyPackage.MODULE_COMPONENT__NAME:
            setName((String) newValue);
            return;
        case AssemblyPackage.MODULE_COMPONENT__URI:
            setUri((String) newValue);
            return;
        case AssemblyPackage.MODULE_COMPONENT__ANY_ATTRIBUTE:
            ((ESequence) getAnyAttribute()).featureMap().clear();
            ((ESequence) getAnyAttribute()).featureMap().addAll((Collection) newValue);
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
        case AssemblyPackage.MODULE_COMPONENT__PROPERTIES:
            setPropertyValues((PropertyValues) null);
            return;
        case AssemblyPackage.MODULE_COMPONENT__REFERENCES:
            setReferenceValues((ReferenceValues) null);
            return;
        case AssemblyPackage.MODULE_COMPONENT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.MODULE_COMPONENT__MODULE:
            setModule(MODULE_EDEFAULT);
            return;
        case AssemblyPackage.MODULE_COMPONENT__NAME:
            setName(NAME_EDEFAULT);
            return;
        case AssemblyPackage.MODULE_COMPONENT__URI:
            setUri(URI_EDEFAULT);
            return;
        case AssemblyPackage.MODULE_COMPONENT__ANY_ATTRIBUTE:
            ((ESequence) getAnyAttribute()).featureMap().clear();
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
        case AssemblyPackage.MODULE_COMPONENT__PROPERTIES:
            return properties != null;
        case AssemblyPackage.MODULE_COMPONENT__REFERENCES:
            return references != null;
        case AssemblyPackage.MODULE_COMPONENT__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.MODULE_COMPONENT__MODULE:
            return MODULE_EDEFAULT == null ? module != null : !MODULE_EDEFAULT.equals(module);
        case AssemblyPackage.MODULE_COMPONENT__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case AssemblyPackage.MODULE_COMPONENT__URI:
            return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
        case AssemblyPackage.MODULE_COMPONENT__ANY_ATTRIBUTE:
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
        result.append(", module: ");
        result.append(module);
        result.append(", name: ");
        result.append(name);
        result.append(", uri: ");
        result.append(uri);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
		result.append(')');
		return result.toString();
	}

} //ModuleComponentImpl
