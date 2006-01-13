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
import org.osoa.sca.model.Component;
import org.osoa.sca.model.Implementation;
import org.osoa.sca.model.PropertyValues;
import org.osoa.sca.model.ReferenceValues;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link ComponentImpl#getImplementation <em>Implementation</em>}</li>
 * <li>{@link ComponentImpl#getProperties <em>Properties</em>}</li>
 * <li>{@link ComponentImpl#getReferences <em>References</em>}</li>
 * <li>{@link ComponentImpl#getAny <em>Any</em>}</li>
 * <li>{@link ComponentImpl#getName <em>Name</em>}</li>
 * <li>{@link ComponentImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentImpl extends EDataObjectImpl implements Component {
    /**
     * The cached value of the '{@link #getImplementation() <em>Implementation</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getImplementation()
     */
    protected Implementation implementation = null;

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
    protected ComponentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getComponent();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Implementation getImplementation() {
        return implementation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetImplementation(Implementation newImplementation, NotificationChain msgs) {
        Implementation oldImplementation = implementation;
        implementation = newImplementation;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__IMPLEMENTATION, oldImplementation, newImplementation);
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
    public void setImplementation(Implementation newImplementation) {
        if (newImplementation != implementation) {
            NotificationChain msgs = null;
            if (implementation != null)
                msgs = ((InternalEObject) implementation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.COMPONENT__IMPLEMENTATION, null, msgs);
            if (newImplementation != null)
                msgs = ((InternalEObject) newImplementation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.COMPONENT__IMPLEMENTATION, null, msgs);
            msgs = basicSetImplementation(newImplementation, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__IMPLEMENTATION, newImplementation, newImplementation));
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__PROPERTIES, oldProperties, newProperties);
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
                msgs = ((InternalEObject) properties).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.COMPONENT__PROPERTIES, null, msgs);
            if (newProperties != null)
                msgs = ((InternalEObject) newProperties).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.COMPONENT__PROPERTIES, null, msgs);
            msgs = basicSetProperties(newProperties, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__PROPERTIES, newProperties, newProperties));
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__REFERENCES, oldReferences, newReferences);
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
                msgs = ((InternalEObject) references).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.COMPONENT__REFERENCES, null, msgs);
            if (newReferences != null)
                msgs = ((InternalEObject) newReferences).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AssemblyPackage.COMPONENT__REFERENCES, null, msgs);
            msgs = basicSetReferences(newReferences, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__REFERENCES, newReferences, newReferences));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.COMPONENT__ANY));
        }
        return any;
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
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.COMPONENT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.COMPONENT__ANY_ATTRIBUTE));
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
            case AssemblyPackage.COMPONENT__IMPLEMENTATION:
                return basicSetImplementation(null, msgs);
            case AssemblyPackage.COMPONENT__PROPERTIES:
                return basicSetProperties(null, msgs);
            case AssemblyPackage.COMPONENT__REFERENCES:
                return basicSetReferences(null, msgs);
            case AssemblyPackage.COMPONENT__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.COMPONENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.COMPONENT__IMPLEMENTATION:
            return getImplementation();
        case AssemblyPackage.COMPONENT__PROPERTIES:
            return getPropertyValues();
        case AssemblyPackage.COMPONENT__REFERENCES:
            return getReferenceValues();
        case AssemblyPackage.COMPONENT__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.COMPONENT__NAME:
            return getName();
        case AssemblyPackage.COMPONENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.COMPONENT__IMPLEMENTATION:
            setImplementation((Implementation) newValue);
            return;
        case AssemblyPackage.COMPONENT__PROPERTIES:
            setPropertyValues((PropertyValues) newValue);
            return;
        case AssemblyPackage.COMPONENT__REFERENCES:
            setReferenceValues((ReferenceValues) newValue);
            return;
        case AssemblyPackage.COMPONENT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.COMPONENT__NAME:
            setName((String) newValue);
            return;
        case AssemblyPackage.COMPONENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.COMPONENT__IMPLEMENTATION:
            setImplementation((Implementation) null);
            return;
        case AssemblyPackage.COMPONENT__PROPERTIES:
            setPropertyValues((PropertyValues) null);
            return;
        case AssemblyPackage.COMPONENT__REFERENCES:
            setReferenceValues((ReferenceValues) null);
            return;
        case AssemblyPackage.COMPONENT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.COMPONENT__NAME:
            setName(NAME_EDEFAULT);
            return;
        case AssemblyPackage.COMPONENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.COMPONENT__IMPLEMENTATION:
            return implementation != null;
        case AssemblyPackage.COMPONENT__PROPERTIES:
            return properties != null;
        case AssemblyPackage.COMPONENT__REFERENCES:
            return references != null;
        case AssemblyPackage.COMPONENT__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.COMPONENT__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case AssemblyPackage.COMPONENT__ANY_ATTRIBUTE:
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
        result.append(", name: ");
        result.append(name);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
		result.append(')');
		return result.toString();
	}

} //ComponentImpl
