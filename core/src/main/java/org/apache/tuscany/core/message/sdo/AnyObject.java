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
package org.apache.tuscany.core.message.sdo;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Any Object</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link AnyObject#getObject <em>Object</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='AnyObject' kind='elementOnly'"
 * @generated
 * @see AnyObjectPackage#getAnyObject()
 */
public interface AnyObject {
    /**
     * Returns the value of the '<em><b>Object</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Object</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Object</em>' attribute.
     * @model unique="false" dataType="org.apache.tuscany.message.object.Object" required="true"
     * extendedMetaData="kind='element' name='object'"
     * @generated
     * @see #setObject(Object)
     * @see AnyObjectPackage#getAnyObject_Object()
     */
    Object getObject();

    /**
     * Sets the value of the '{@link AnyObject#getObject <em>Object</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Object</em>' attribute.
     * @generated
     * @see #getObject()
     */
    void setObject(Object value);

} // AnyObject
