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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dynamic Package</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link DynamicPackage#getAny <em>Any</em>}</li>
 * <li>{@link DynamicPackage#getLoaderClassName <em>Loader Class Name</em>}</li>
 * <li>{@link DynamicPackage#getLocation <em>Location</em>}</li>
 * <li>{@link DynamicPackage#isPreLoad <em>Pre Load</em>}</li>
 * <li>{@link DynamicPackage#getUri <em>Uri</em>}</li>
 * <li>{@link DynamicPackage#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='DynamicPackage' kind='elementOnly'"
 * @generated
 * @see ModelConfigurationPackage#getDynamicPackage()
 */
public interface DynamicPackage extends EObject {
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
     * extendedMetaData="kind='elementWildcard' wildcards='##other' name=':0' processing='lax'"
     * @generated
     * @see ModelConfigurationPackage#getDynamicPackage_Any()
     */
    FeatureMap getAny();

    /**
     * Returns the value of the '<em><b>Loader Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Loader Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Loader Class Name</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='loaderClassName'"
     * @generated
     * @see #setLoaderClassName(String)
     * @see ModelConfigurationPackage#getDynamicPackage_LoaderClassName()
     */
    String getLoaderClassName();

    /**
     * Sets the value of the '{@link DynamicPackage#getLoaderClassName <em>Loader Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Loader Class Name</em>' attribute.
     * @generated
     * @see #getLoaderClassName()
     */
    void setLoaderClassName(String value);

    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Location</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Location</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='location'"
     * @generated
     * @see #setLocation(String)
     * @see ModelConfigurationPackage#getDynamicPackage_Location()
     */
    String getLocation();

    /**
     * Sets the value of the '{@link DynamicPackage#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Location</em>' attribute.
     * @generated
     * @see #getLocation()
     */
    void setLocation(String value);

    /**
     * Returns the value of the '<em><b>Pre Load</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Pre Load</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Pre Load</em>' attribute.
     * @model unique="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     * extendedMetaData="kind='attribute' name='preLoad'"
     * @generated
     * @see #isSetPreLoad()
     * @see #unsetPreLoad()
     * @see #setPreLoad(boolean)
     * @see ModelConfigurationPackage#getDynamicPackage_PreLoad()
     */
    boolean isPreLoad();

    /**
     * Sets the value of the '{@link DynamicPackage#isPreLoad <em>Pre Load</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Pre Load</em>' attribute.
     * @generated
     * @see #isSetPreLoad()
     * @see #unsetPreLoad()
     * @see #isPreLoad()
     */
    void setPreLoad(boolean value);

    /**
     * Unsets the value of the '{@link DynamicPackage#isPreLoad <em>Pre Load</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #isSetPreLoad()
     * @see #isPreLoad()
     * @see #setPreLoad(boolean)
     */
    void unsetPreLoad();

    /**
     * Returns whether the value of the '{@link DynamicPackage#isPreLoad <em>Pre Load</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return whether the value of the '<em>Pre Load</em>' attribute is set.
     * @generated
     * @see #unsetPreLoad()
     * @see #isPreLoad()
     * @see #setPreLoad(boolean)
     */
    boolean isSetPreLoad();

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Uri</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='uri'"
     * @generated
     * @see #setUri(String)
     * @see ModelConfigurationPackage#getDynamicPackage_Uri()
     */
    String getUri();

    /**
     * Sets the value of the '{@link DynamicPackage#getUri <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Uri</em>' attribute.
     * @generated
     * @see #getUri()
     */
    void setUri(String value);

    /**
     * Returns the value of the '<em><b>Any Attribute</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Any Attribute</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Any Attribute</em>' attribute list.
     * @see org.apache.tuscany.model.config.ModelConfigurationPackage#getDynamicPackage_AnyAttribute()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='attributeWildcard' wildcards='##any' name=':5' processing='lax'"
     * @generated
     */
    FeatureMap getAnyAttribute();

} // DynamicPackage
