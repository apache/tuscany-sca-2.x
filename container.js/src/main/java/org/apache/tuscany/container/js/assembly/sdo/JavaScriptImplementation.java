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
package org.apache.tuscany.container.js.assembly.sdo;

import commonj.sdo.Sequence;

import org.osoa.sca.model.Implementation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Java Script Implementation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getScriptFile <em>Script File</em>}</li>
 *   <li>{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptAssemblyPackage#getJavaScriptImplementation()
 * @model extendedMetaData="name='JavaScriptImplementation' kind='elementOnly'"
 * @generated
 */
public interface JavaScriptImplementation extends Implementation {
	/**
	 * Returns the value of the '<em><b>Any</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Any</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Any</em>' attribute list.
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptAssemblyPackage#getJavaScriptImplementation_Any()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' wildcards='##other' name=':0' processing='lax'"
	 * @generated
	 */
	Sequence getAny();

	/**
	 * Returns the value of the '<em><b>Script File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Script File</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Script File</em>' attribute.
	 * @see #setScriptFile(String)
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptAssemblyPackage#getJavaScriptImplementation_ScriptFile()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='scriptFile'"
	 * @generated
	 */
	String getScriptFile();

	/**
	 * Sets the value of the '{@link org.apache.tuscany.container.js.assembly.sdo.JavaScriptImplementation#getScriptFile <em>Script File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Script File</em>' attribute.
	 * @see #getScriptFile()
	 * @generated
	 */
	void setScriptFile(String value);

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
	 * @see org.apache.tuscany.container.js.assembly.sdo.JavaScriptAssemblyPackage#getJavaScriptImplementation_AnyAttribute()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='attributeWildcard' wildcards='##any' name=':2' processing='lax'"
	 * @generated
	 */
	Sequence getAnyAttribute();

} // JavaScriptImplementation
