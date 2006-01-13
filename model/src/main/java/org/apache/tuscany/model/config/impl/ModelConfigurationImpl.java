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
package org.apache.tuscany.model.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.config.DynamicPackageLoader;
import org.apache.tuscany.model.config.GeneratedPackage;
import org.apache.tuscany.model.config.ModelConfiguration;
import org.apache.tuscany.model.config.ModelConfigurationPackage;
import org.apache.tuscany.model.config.ResourceFactory;
import org.apache.tuscany.model.config.URIMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link ModelConfigurationImpl#getGeneratedPackages <em>Generated Packages</em>}</li>
 * <li>{@link ModelConfigurationImpl#getDynamicPackages <em>Dynamic Packages</em>}</li>
 * <li>{@link ModelConfigurationImpl#getDynamicPackageLoaders <em>Dynamic Package Loaders</em>}</li>
 * <li>{@link ModelConfigurationImpl#getUriMappings <em>Uri Mappings</em>}</li>
 * <li>{@link ModelConfigurationImpl#getResourceFactories <em>Resource Factories</em>}</li>
 * <li>{@link ModelConfigurationImpl#getAny <em>Any</em>}</li>
 * <li>{@link ModelConfigurationImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelConfigurationImpl extends EObjectImpl implements ModelConfiguration {
    /**
     * The cached value of the '{@link #getGeneratedPackages() <em>Generated Packages</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getGeneratedPackages()
     */
    protected EList generatedPackages = null;

    /**
     * The cached value of the '{@link #getDynamicPackages() <em>Dynamic Packages</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getDynamicPackages()
     */
    protected EList dynamicPackages = null;

    /**
     * The cached value of the '{@link #getDynamicPackageLoaders() <em>Dynamic Package Loaders</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getDynamicPackageLoaders()
     */
    protected EList dynamicPackageLoaders = null;

    /**
     * The cached value of the '{@link #getUriMappings() <em>Uri Mappings</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getUriMappings()
     */
    protected EList uriMappings = null;

    /**
     * The cached value of the '{@link #getResourceFactories() <em>Resource Factories</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getResourceFactories()
     */
    protected EList resourceFactories = null;

    /**
     * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAny()
     */
    protected FeatureMap any = null;

    /**
     * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAnyAttribute()
     */
    protected FeatureMap anyAttribute = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected ModelConfigurationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return ModelConfigurationPackage.eINSTANCE.getModelConfiguration();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getGeneratedPackages() {
        if (generatedPackages == null) {
            generatedPackages = new EObjectContainmentEList(GeneratedPackage.class, this, ModelConfigurationPackage.MODEL_CONFIGURATION__GENERATED_PACKAGES);
        }
        return generatedPackages;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getDynamicPackages() {
        if (dynamicPackages == null) {
            dynamicPackages = new EObjectContainmentEList(DynamicPackage.class, this, ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGES);
        }
        return dynamicPackages;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getDynamicPackageLoaders() {
        if (dynamicPackageLoaders == null) {
            dynamicPackageLoaders = new EObjectContainmentEList(DynamicPackageLoader.class, this, ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS);
        }
        return dynamicPackageLoaders;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getUriMappings() {
        if (uriMappings == null) {
            uriMappings = new EObjectContainmentEList(URIMapping.class, this, ModelConfigurationPackage.MODEL_CONFIGURATION__URI_MAPPINGS);
        }
        return uriMappings;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getResourceFactories() {
        if (resourceFactories == null) {
            resourceFactories = new EObjectContainmentEList(ResourceFactory.class, this, ModelConfigurationPackage.MODEL_CONFIGURATION__RESOURCE_FACTORIES);
        }
        return resourceFactories;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAny() {
        if (any == null) {
            any = new BasicFeatureMap(this, ModelConfigurationPackage.MODEL_CONFIGURATION__ANY);
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicFeatureMap(this, ModelConfigurationPackage.MODEL_CONFIGURATION__ANY_ATTRIBUTE);
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
            case ModelConfigurationPackage.MODEL_CONFIGURATION__GENERATED_PACKAGES:
                return ((InternalEList) getGeneratedPackages()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGES:
                return ((InternalEList) getDynamicPackages()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS:
                return ((InternalEList) getDynamicPackageLoaders()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.MODEL_CONFIGURATION__URI_MAPPINGS:
                return ((InternalEList) getUriMappings()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.MODEL_CONFIGURATION__RESOURCE_FACTORIES:
                return ((InternalEList) getResourceFactories()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY:
                return ((InternalEList) getAny()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY_ATTRIBUTE:
                return ((InternalEList) getAnyAttribute()).basicRemove(otherEnd, msgs);
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
        case ModelConfigurationPackage.MODEL_CONFIGURATION__GENERATED_PACKAGES:
            return getGeneratedPackages();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGES:
            return getDynamicPackages();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS:
            return getDynamicPackageLoaders();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__URI_MAPPINGS:
            return getUriMappings();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__RESOURCE_FACTORIES:
            return getResourceFactories();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY:
            return getAny();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY_ATTRIBUTE:
            return getAnyAttribute();
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
        case ModelConfigurationPackage.MODEL_CONFIGURATION__GENERATED_PACKAGES:
            getGeneratedPackages().clear();
            getGeneratedPackages().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGES:
            getDynamicPackages().clear();
            getDynamicPackages().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS:
            getDynamicPackageLoaders().clear();
            getDynamicPackageLoaders().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__URI_MAPPINGS:
            getUriMappings().clear();
            getUriMappings().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__RESOURCE_FACTORIES:
            getResourceFactories().clear();
            getResourceFactories().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY:
            getAny().clear();
            getAny().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY_ATTRIBUTE:
            getAnyAttribute().clear();
            getAnyAttribute().addAll((Collection) newValue);
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
        case ModelConfigurationPackage.MODEL_CONFIGURATION__GENERATED_PACKAGES:
            getGeneratedPackages().clear();
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGES:
            getDynamicPackages().clear();
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS:
            getDynamicPackageLoaders().clear();
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__URI_MAPPINGS:
            getUriMappings().clear();
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__RESOURCE_FACTORIES:
            getResourceFactories().clear();
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY:
            getAny().clear();
            return;
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY_ATTRIBUTE:
            getAnyAttribute().clear();
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
        case ModelConfigurationPackage.MODEL_CONFIGURATION__GENERATED_PACKAGES:
            return generatedPackages != null && !generatedPackages.isEmpty();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGES:
            return dynamicPackages != null && !dynamicPackages.isEmpty();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS:
            return dynamicPackageLoaders != null && !dynamicPackageLoaders.isEmpty();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__URI_MAPPINGS:
            return uriMappings != null && !uriMappings.isEmpty();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__RESOURCE_FACTORIES:
            return resourceFactories != null && !resourceFactories.isEmpty();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY:
            return any != null && !any.isEmpty();
        case ModelConfigurationPackage.MODEL_CONFIGURATION__ANY_ATTRIBUTE:
            return anyAttribute != null && !anyAttribute.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (any: ");
        result.append(any);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
        return result.toString();
    }

    /**
     * User code
     */

    private InstanceClassMap instanceClassMap;

    /**
     * A Map used to index instance classes
     */
    private class InstanceClassMap extends WeakHashMap {
        private List remainingPackages;

        public InstanceClassMap(ModelConfiguration config) {
            super(config.getGeneratedPackages().size() * 64);
            remainingPackages = new ArrayList(config.getGeneratedPackages());
        }

        private EClass getEClass(Class interfaceClass) {
            EClass result = (EClass) this.get(interfaceClass);
            if (result == null && !remainingPackages.isEmpty()) {
                result = searchPackages(interfaceClass);
            }
            return result;
        }

        private boolean inPackage(String ePackageClassName, Class interfaceClass) {
            int index = ePackageClassName.lastIndexOf('.');
            String p1 = (index != -1) ? ePackageClassName.substring(0, index) : "";
            index = interfaceClass.getName().lastIndexOf('.');
            String p2 = (index != -1) ? interfaceClass.getName().substring(0, index) : "";
            return p1.equals(p2);
        }

        private EClass searchPackages(Class interfaceClass) {
            Iterator remaining = remainingPackages.iterator();
            EPackage.Registry packageRegistry = eResource().getResourceSet().getPackageRegistry();
            while (remaining.hasNext()) {
                GeneratedPackage generatedPackage = (GeneratedPackage) remaining.next();
                if (inPackage(generatedPackage.getPackageClassName(), interfaceClass)) {
                    remaining.remove();
                    EPackage pkg = packageRegistry.getEPackage(generatedPackage.getUri());
                    EClass eClass = indexPackage(pkg, interfaceClass);
                    if (eClass != null) {
                        return eClass;
                    }
                }
            }
            return null;
        }

        private EClass indexPackage(EPackage pkg, Class interfaceClass) {
            EClass result = null;
            List classifiers = pkg.getEClassifiers();
            for (int x = 0, size = classifiers.size(); x < size; x++) {
                Object classifier = classifiers.get(x);
                if (classifier instanceof EClass) {
                    EClass eClass = (EClass) classifier;
                    Class instanceClass = eClass.getInstanceClass();
                    this.put(instanceClass, eClass);
                    if (interfaceClass == instanceClass) {
                        result = eClass;
                    }
                }
            }
            return result;
        }
    }

    /**
     * @see org.apache.tuscany.model.config.ModelConfiguration#getEClass(java.lang.Class)
     */
    public EClass getEClass(Class instanceClass) {
        if (instanceClassMap == null) {
            instanceClassMap = new InstanceClassMap(this);
        }
        return instanceClassMap.getEClass(instanceClass);
    }

    /**
     * @see org.apache.tuscany.model.config.ModelConfiguration#getDynamicPackageLoader(org.eclipse.emf.common.util.URI)
     */
    public DynamicPackageLoader getDynamicPackageLoader(URI uri) {
        String protocol = uri.scheme();
        String extension = uri.fileExtension();
        List loaders = getDynamicPackageLoaders();
        for (int x = 0, size = loaders.size(); x < size; x++) {
            DynamicPackageLoader loader = (DynamicPackageLoader) loaders.get(x);
            if (protocol != null && protocol.equals(loader.getProtocol()) || extension != null && extension.equals(loader.getExtension())) {
				return loader;
			}
		}
		return null;
	}

} //ConfigImpl
