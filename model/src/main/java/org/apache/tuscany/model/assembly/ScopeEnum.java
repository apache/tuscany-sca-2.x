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
package org.apache.tuscany.model.assembly;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Scope</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * FIXME this class references EMF explicitly
 * @model
 * @generated
 * @see org.apache.tuscany.model.runtime.AssemblyPackage#getScope()
 */
public final class ScopeEnum extends AbstractEnumerator {
    /**
     * The '<em><b>Instance</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>Instance</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="instance"
     * @generated
     * @ordered
     * @see #INSTANCE_LITERAL
     */
    public static final int INSTANCE = 0;

    /**
     * The '<em><b>Request</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>Request</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="request"
     * @generated
     * @ordered
     * @see #REQUEST_LITERAL
     */
    public static final int REQUEST = 1;

    /**
     * The '<em><b>Session</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>Session</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="session"
     * @generated
     * @ordered
     * @see #SESSION_LITERAL
     */
    public static final int SESSION = 2;

    /**
     * The '<em><b>Module</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>Module</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="module"
     * @generated
     * @ordered
     * @see #MODULE_LITERAL
     */
    public static final int MODULE = 3;

    /**
     * The '<em><b>Instance</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #INSTANCE
     */
    public static final ScopeEnum INSTANCE_LITERAL = new ScopeEnum(INSTANCE, "instance");

    /**
     * The '<em><b>Request</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #REQUEST
     */
    public static final ScopeEnum REQUEST_LITERAL = new ScopeEnum(REQUEST, "request");

    /**
     * The '<em><b>Session</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #SESSION
     */
    public static final ScopeEnum SESSION_LITERAL = new ScopeEnum(SESSION, "session");

    /**
     * The '<em><b>Module</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #MODULE
     */
    public static final ScopeEnum MODULE_LITERAL = new ScopeEnum(MODULE, "module");

    /**
     * An array of all the '<em><b>Scope</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private static final ScopeEnum[] VALUES_ARRAY =
            new ScopeEnum[]{
                    INSTANCE_LITERAL,
                    REQUEST_LITERAL,
                    SESSION_LITERAL,
                    MODULE_LITERAL,
            };

    /**
     * A public read-only list of all the '<em><b>Scope</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Scope</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static ScopeEnum get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ScopeEnum result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Scope</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static ScopeEnum get(int value) {
        switch (value) {
        case INSTANCE:
            return INSTANCE_LITERAL;
        case REQUEST:
            return REQUEST_LITERAL;
        case SESSION:
            return SESSION_LITERAL;
        case MODULE:
            return MODULE_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * FIXME hack added for now
     * @generated
     */
    public ScopeEnum(int value, String name) {
        super(value, name);
	}

} //Scope
