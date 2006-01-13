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
package org.apache.tuscany.model.assembly.sdo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Override Options</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 *
 * @model
 * @generated
 * @see org.apache.tuscany.model.assembly.binding.AssemblyPackage#getOverrideOptions()
 */
public final class OverrideOptions extends AbstractEnumerator {
    /**
     * The '<em><b>No</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>No</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="no"
     * @generated
     * @ordered
     * @see #NO_LITERAL
     */
    public static final int NO = 0;

    /**
     * The '<em><b>May</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>May</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="may"
     * @generated
     * @ordered
     * @see #MAY_LITERAL
     */
    public static final int MAY = 1;

    /**
     * The '<em><b>Must</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of '<em><b>Must</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @model name="must"
     * @generated
     * @ordered
     * @see #MUST_LITERAL
     */
    public static final int MUST = 2;

    /**
     * The '<em><b>No</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #NO
     */
    public static final OverrideOptions NO_LITERAL = new OverrideOptions(NO, "no");

    /**
     * The '<em><b>May</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #MAY
     */
    public static final OverrideOptions MAY_LITERAL = new OverrideOptions(MAY, "may");

    /**
     * The '<em><b>Must</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #MUST
     */
    public static final OverrideOptions MUST_LITERAL = new OverrideOptions(MUST, "must");

    /**
     * An array of all the '<em><b>Override Options</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private static final OverrideOptions[] VALUES_ARRAY =
            new OverrideOptions[]{
                    NO_LITERAL,
                    MAY_LITERAL,
                    MUST_LITERAL,
            };

    /**
     * A public read-only list of all the '<em><b>Override Options</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Override Options</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static OverrideOptions get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            OverrideOptions result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Override Options</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static OverrideOptions get(int value) {
        switch (value) {
        case NO:
            return NO_LITERAL;
        case MAY:
            return MAY_LITERAL;
        case MUST:
            return MUST_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private OverrideOptions(int value, String name) {
        super(value, name);
	}

} //OverrideOptions
