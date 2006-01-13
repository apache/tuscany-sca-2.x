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
 * A representation of the model object '<em><b>Dynamic Package Loader</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link DynamicPackageLoader#getAny <em>Any</em>}</li>
 * <li>{@link DynamicPackageLoader#getClassName <em>Class Name</em>}</li>
 * <li>{@link DynamicPackageLoader#getExtension <em>Extension</em>}</li>
 * <li>{@link DynamicPackageLoader#getProtocol <em>Protocol</em>}</li>
 * <li>{@link DynamicPackageLoader#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='DynamicPackageLoader' kind='elementOnly'"
 * @generated
 * @see ModelConfigurationPackage#getDynamicPackageLoader()
 */
public interface DynamicPackageLoader extends EObject {
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
     * @see ModelConfigurationPackage#getDynamicPackageLoader_Any()
     */
    FeatureMap getAny();

    /**
     * Returns the value of the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Class Name</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='className'"
     * @generated
     * @see #setClassName(String)
     * @see ModelConfigurationPackage#getDynamicPackageLoader_ClassName()
     */
    String getClassName();

    /**
     * Sets the value of the '{@link DynamicPackageLoader#getClassName <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Class Name</em>' attribute.
     * @generated
     * @see #getClassName()
     */
    void setClassName(String value);

    /**
     * Returns the value of the '<em><b>Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Extension</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Extension</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='extension'"
     * @generated
     * @see #setExtension(String)
     * @see ModelConfigurationPackage#getDynamicPackageLoader_Extension()
     */
    String getExtension();

    /**
     * Sets the value of the '{@link DynamicPackageLoader#getExtension <em>Extension</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Extension</em>' attribute.
     * @generated
     * @see #getExtension()
     */
    void setExtension(String value);

    /**
     * Returns the value of the '<em><b>Protocol</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Protocol</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Protocol</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="kind='attribute' name='protocol'"
     * @generated
     * @see #setProtocol(String)
     * @see ModelConfigurationPackage#getDynamicPackageLoader_Protocol()
     */
    String getProtocol();

    /**
     * Sets the value of the '{@link DynamicPackageLoader#getProtocol <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Protocol</em>' attribute.
     * @generated
     * @see #getProtocol()
     */
    void setProtocol(String value);

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
     * extendedMetaData="kind='attributeWildcard' wildcards='##any' name=':4' processing='lax'"
     * @generated
     * @see ModelConfigurationPackage#getDynamicPackageLoader_AnyAttribute()
     */
    FeatureMap getAnyAttribute();

} // DynamicPackageLoader
