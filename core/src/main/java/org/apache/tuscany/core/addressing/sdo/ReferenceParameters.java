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
package org.apache.tuscany.core.addressing.sdo;

import java.util.Map;

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reference Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link ReferenceParameters#getAny <em>Any</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='ReferenceParametersType' kind='elementOnly'"
 * @generated
 * @see AddressingElementPackage#getReferenceParameters()
 */
public interface ReferenceParameters {
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
     * extendedMetaData="kind='elementWildcard' wildcards='##any' name=':0' processing='lax'"
     * @generated
     * @see AddressingElementPackage#getReferenceParameters_Any()
     */
    Sequence getAny();

    /**
     * Custom code
     * <p/>
     * Returns the endpoint reference parameters..
     *
     * @return The collection of reference parameters.
     */

    /**
     * Returns the endpoint reference parameters..
     * @return The collection of reference parameters.
     */
    Map getReferenceParameters();

    /**
     * Returns the specified header.
     *
     * @param name
     * @return
     */
    Object getReferenceParameter(String name);

    /**
     * Sets the specified reference parameter.
     *
     * @param name
     * @param value
     */
    void setReferenceParameter(String name, Object value);

} // ReferenceParameters
