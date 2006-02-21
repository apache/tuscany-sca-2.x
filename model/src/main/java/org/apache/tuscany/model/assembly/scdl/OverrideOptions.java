/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Override Options</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getOverrideOptions()
 * @generated
 */
public final class OverrideOptions extends InternalOverrideOptions
{
  /**
   * The '<em><b>No</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>No</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #NO_LITERAL
   * @generated
   * @ordered
   */
  public static final int NO = 0;

  /**
   * The '<em><b>May</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>May</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MAY_LITERAL
   * @generated
   * @ordered
   */
  public static final int MAY = 1;

  /**
   * The '<em><b>Must</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Must</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MUST_LITERAL
   * @generated
   * @ordered
   */
  public static final int MUST = 2;

  /**
   * The '<em><b>No</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #NO
   * @generated
   * @ordered
   */
  public static final OverrideOptions NO_LITERAL = new OverrideOptions(NO, "no", "no");

  /**
   * The '<em><b>May</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MAY
   * @generated
   * @ordered
   */
  public static final OverrideOptions MAY_LITERAL = new OverrideOptions(MAY, "may", "may");

  /**
   * The '<em><b>Must</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MUST
   * @generated
   * @ordered
   */
  public static final OverrideOptions MUST_LITERAL = new OverrideOptions(MUST, "must", "must");

  /**
   * An array of all the '<em><b>Override Options</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final OverrideOptions[] VALUES_ARRAY =
    new OverrideOptions[]
    {
      NO_LITERAL,
      MAY_LITERAL,
      MUST_LITERAL,
    };

  /**
   * A public read-only list of all the '<em><b>Override Options</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Override Options</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OverrideOptions get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OverrideOptions result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Override Options</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OverrideOptions getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OverrideOptions result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Override Options</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OverrideOptions get(int value)
  {
    switch (value)
    {
      case NO: return NO_LITERAL;
      case MAY: return MAY_LITERAL;
      case MUST: return MUST_LITERAL;
    }
    return null;	
  }

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private OverrideOptions(int value, String name, String literal)
  {
    super(value, name, literal);
  }

} //OverrideOptions

/**
 * A private implementation class to construct the instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
class InternalOverrideOptions extends org.eclipse.emf.common.util.AbstractEnumerator
{
  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InternalOverrideOptions(int value, String name, String literal)
  {
    super(value, name, literal);
  }
}
