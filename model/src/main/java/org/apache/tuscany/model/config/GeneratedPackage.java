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
 * A representation of the model object '<em><b>Generated Package</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link GeneratedPackage#getAny <em>Any</em>}</li>
 * <li>{@link GeneratedPackage#getPackageClassName <em>Package Class Name</em>}</li>
 * <li>{@link GeneratedPackage#isPreLoad <em>Pre Load</em>}</li>
 * <li>{@link GeneratedPackage#getUri <em>Uri</em>}</li>
 * <li>{@link GeneratedPackage#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='GeneratedPackage' kind='elementOnly'"
 * @generated
 * @see ModelConfigurationPackage#getGeneratedPackage()
 */
public interface GeneratedPackage extends EObject {
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
     * @see ModelConfigurationPackage#getGeneratedPackage_Any()
     */
    FeatureMap getAny();

    /**
     * Returns the value of the '<em><b>Package Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Package Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Package Class Name</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='packageClassName'"
     * @generated
     * @see #setPackageClassName(String)
     * @see ModelConfigurationPackage#getGeneratedPackage_PackageClassName()
     */
    String getPackageClassName();

    /**
     * Sets the value of the '{@link GeneratedPackage#getPackageClassName <em>Package Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Package Class Name</em>' attribute.
     * @generated
     * @see #getPackageClassName()
     */
    void setPackageClassName(String value);

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
     * @see ModelConfigurationPackage#getGeneratedPackage_PreLoad()
     */
    boolean isPreLoad();

    /**
     * Sets the value of the '{@link GeneratedPackage#isPreLoad <em>Pre Load</em>}' attribute.
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
     * Unsets the value of the '{@link GeneratedPackage#isPreLoad <em>Pre Load</em>}' attribute.
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
     * Returns whether the value of the '{@link GeneratedPackage#isPreLoad <em>Pre Load</em>}' attribute is set.
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
     * @see ModelConfigurationPackage#getGeneratedPackage_Uri()
     */
    String getUri();

    /**
     * Sets the value of the '{@link GeneratedPackage#getUri <em>Uri</em>}' attribute.
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
     * @see org.apache.tuscany.model.config.ModelConfigurationPackage#getGeneratedPackage_AnyAttribute()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='attributeWildcard' wildcards='##any' name=':4' processing='lax'"
     * @generated
     */
    FeatureMap getAnyAttribute();

} // GeneratedPackage
