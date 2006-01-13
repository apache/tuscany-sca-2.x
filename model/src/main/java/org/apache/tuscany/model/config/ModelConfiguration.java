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
package org.apache.tuscany.model.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Config</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link ModelConfiguration#getGeneratedPackages <em>Generated Packages</em>}</li>
 * <li>{@link ModelConfiguration#getDynamicPackages <em>Dynamic Packages</em>}</li>
 * <li>{@link ModelConfiguration#getDynamicPackageLoaders <em>Dynamic Package Loaders</em>}</li>
 * <li>{@link ModelConfiguration#getUriMappings <em>Uri Mappings</em>}</li>
 * <li>{@link ModelConfiguration#getResourceFactories <em>Resource Factories</em>}</li>
 * <li>{@link ModelConfiguration#getAny <em>Any</em>}</li>
 * <li>{@link ModelConfiguration#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='ModelConfiguration' kind='elementOnly'"
 * @generated
 * @see ModelConfigurationPackage#getModelConfiguration()
 */
public interface ModelConfiguration extends EObject {
    /**
     * Returns the value of the '<em><b>Generated Packages</b></em>' containment reference list.
     * The list contents are of type {@link GeneratedPackage}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Generated Package</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Generated Packages</em>' containment reference list.
     * @model type="org.apache.tuscany.model.config.GeneratedPackage" containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='generatedPackage'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_GeneratedPackages()
     */
    List getGeneratedPackages();

    /**
     * Returns the value of the '<em><b>Dynamic Packages</b></em>' containment reference list.
     * The list contents are of type {@link DynamicPackage}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Dynamic Package</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Dynamic Packages</em>' containment reference list.
     * @model type="org.apache.tuscany.model.config.DynamicPackage" containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='dynamicPackage'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_DynamicPackages()
     */
    List getDynamicPackages();

    /**
     * Returns the value of the '<em><b>Dynamic Package Loaders</b></em>' containment reference list.
     * The list contents are of type {@link DynamicPackageLoader}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Dynamic Package Loader</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Dynamic Package Loaders</em>' containment reference list.
     * @model type="org.apache.tuscany.model.config.DynamicPackageLoader" containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='dynamicPackageLoader'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_DynamicPackageLoaders()
     */
    List getDynamicPackageLoaders();

    /**
     * Returns the value of the '<em><b>Uri Mappings</b></em>' containment reference list.
     * The list contents are of type {@link URIMapping}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Uri Mapping</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Uri Mappings</em>' containment reference list.
     * @model type="org.apache.tuscany.model.config.URIMapping" containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='uriMapping'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_UriMappings()
     */
    List getUriMappings();

    /**
     * Returns the value of the '<em><b>Resource Factories</b></em>' containment reference list.
     * The list contents are of type {@link ResourceFactory}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Resource Factory</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Resource Factories</em>' containment reference list.
     * @model type="org.apache.tuscany.model.config.ResourceFactory" containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='resourceFactory'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_ResourceFactories()
     */
    List getResourceFactories();

    /**
     * Returns the value of the '<em><b>Any</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Any</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Any</em>' attribute list.
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     * extendedMetaData="kind='elementWildcard' wildcards='##other' name=':5' processing='lax'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_Any()
     */
    FeatureMap getAny();

    /**
     * Returns the value of the '<em><b>Any Attribute</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Any Attribute</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Any Attribute</em>' attribute list.
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     * extendedMetaData="kind='attributeWildcard' wildcards='##any' name=':6' processing='lax'"
     * @generated
     * @see ModelConfigurationPackage#getModelConfiguration_AnyAttribute()
     */
    FeatureMap getAnyAttribute();

    /**
     * User code
     * <p/>
     * Returns the EClass for a given instanceClass from the
     * GeneratedPackages in this Config.
     *
     * @param instanceClass the instanceClass of the EClass.
     * @return the EClass for a given instanceClass from the
     *         GeneratedPackages in this Config.
     */

    /**
     * Returns the EClass for a given instanceClass from the
     * GeneratedPackages in this Config.
     * @param instanceClass the instanceClass of the EClass.
     * @return the EClass for a given instanceClass from the
     * GeneratedPackages in this Config.
     */
    EClass getEClass(Class instanceClass);

    /**
     * Returns the Loader in this Config matching this uri or null if none found.
     * @param uri the uri to load.
     * @return the Loader in this Config matching this uri or null if none found.
     */
    DynamicPackageLoader getDynamicPackageLoader(URI uri);

} // Config
