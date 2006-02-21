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
 * A representation of the literals of the enumeration '<em><b>Multiplicity</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl#getMultiplicity()
 * @generated
 */
public final class Multiplicity extends InternalMultiplicity
{
  /**
   * The '<em><b>01</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>01</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #_01_LITERAL
   * @generated
   * @ordered
   */
  public static final int _01 = 0;

  /**
   * The '<em><b>11</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>11</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #_11_LITERAL
   * @generated
   * @ordered
   */
  public static final int _11 = 1;

  /**
   * The '<em><b>0N</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>0N</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #_0N_LITERAL
   * @generated
   * @ordered
   */
  public static final int _0N = 2;

  /**
   * The '<em><b>1N</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>1N</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #_1N_LITERAL
   * @generated
   * @ordered
   */
  public static final int _1N = 3;

  /**
   * The '<em><b>01</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #_01
   * @generated
   * @ordered
   */
  public static final Multiplicity _01_LITERAL = new Multiplicity(_01, "_01", "0..1");

  /**
   * The '<em><b>11</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #_11
   * @generated
   * @ordered
   */
  public static final Multiplicity _11_LITERAL = new Multiplicity(_11, "_11", "1..1");

  /**
   * The '<em><b>0N</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #_0N
   * @generated
   * @ordered
   */
  public static final Multiplicity _0N_LITERAL = new Multiplicity(_0N, "_0N", "0..n");

  /**
   * The '<em><b>1N</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #_1N
   * @generated
   * @ordered
   */
  public static final Multiplicity _1N_LITERAL = new Multiplicity(_1N, "_1N", "1..n");

  /**
   * An array of all the '<em><b>Multiplicity</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final Multiplicity[] VALUES_ARRAY =
    new Multiplicity[]
    {
      _01_LITERAL,
      _11_LITERAL,
      _0N_LITERAL,
      _1N_LITERAL,
    };

  /**
   * A public read-only list of all the '<em><b>Multiplicity</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Multiplicity</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Multiplicity get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Multiplicity result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Multiplicity</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Multiplicity getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Multiplicity result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Multiplicity</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Multiplicity get(int value)
  {
    switch (value)
    {
      case _01: return _01_LITERAL;
      case _11: return _11_LITERAL;
      case _0N: return _0N_LITERAL;
      case _1N: return _1N_LITERAL;
    }
    return null;	
  }

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private Multiplicity(int value, String name, String literal)
  {
    super(value, name, literal);
  }

} //Multiplicity

/**
 * A private implementation class to construct the instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
class InternalMultiplicity extends org.eclipse.emf.common.util.AbstractEnumerator
{
  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InternalMultiplicity(int value, String name, String literal)
  {
    super(value, name, literal);
  }
}
