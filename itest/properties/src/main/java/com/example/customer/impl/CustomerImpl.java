/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.example.customer.impl;

import com.example.customer.Customer;
import com.example.customer.SdoFactory;

import commonj.sdo.Type;

import org.apache.tuscany.sdo.impl.DataObjectBase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Customer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.example.customer.impl.CustomerImpl#getFirstName <em>First Name</em>}</li>
 *   <li>{@link com.example.customer.impl.CustomerImpl#getMiddleName <em>Middle Name</em>}</li>
 *   <li>{@link com.example.customer.impl.CustomerImpl#getLastName <em>Last Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CustomerImpl extends DataObjectBase implements Customer
{

  public final static int FIRST_NAME = 0;

  public final static int MIDDLE_NAME = 1;

  public final static int LAST_NAME = 2;

  public final static int SDO_PROPERTY_COUNT = 3;

  public final static int EXTENDED_PROPERTY_COUNT = 0;


  /**
   * The internal feature id for the '<em><b>First Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_FIRST_NAME = 0;

  /**
   * The internal feature id for the '<em><b>Middle Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_MIDDLE_NAME = 1;

  /**
   * The internal feature id for the '<em><b>Last Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_LAST_NAME = 2;

  /**
   * The number of properties for this type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public final static int INTERNAL_PROPERTY_COUNT = 3;

  protected int internalConvertIndex(int internalIndex)
  {
    switch (internalIndex)
    {
      case INTERNAL_FIRST_NAME: return FIRST_NAME;
      case INTERNAL_MIDDLE_NAME: return MIDDLE_NAME;
      case INTERNAL_LAST_NAME: return LAST_NAME;
    }
    return super.internalConvertIndex(internalIndex);
  }


  /**
   * The default value of the '{@link #getFirstName() <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirstName()
   * @generated
   * @ordered
   */
  protected static final Object FIRST_NAME_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getFirstName() <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirstName()
   * @generated
   * @ordered
   */
  protected Object firstName = FIRST_NAME_DEFAULT_;

  /**
   * The default value of the '{@link #getMiddleName() <em>Middle Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMiddleName()
   * @generated
   * @ordered
   */
  protected static final Object MIDDLE_NAME_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getMiddleName() <em>Middle Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMiddleName()
   * @generated
   * @ordered
   */
  protected Object middleName = MIDDLE_NAME_DEFAULT_;

  /**
   * The default value of the '{@link #getLastName() <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLastName()
   * @generated
   * @ordered
   */
  protected static final Object LAST_NAME_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getLastName() <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLastName()
   * @generated
   * @ordered
   */
  protected Object lastName = LAST_NAME_DEFAULT_;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CustomerImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Type getType()
  {
    return ((SdoFactoryImpl)SdoFactory.INSTANCE).getCustomer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getFirstName()
  {
    return firstName;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFirstName(Object newFirstName)
  {
    firstName = newFirstName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getMiddleName()
  {
    return middleName;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMiddleName(Object newMiddleName)
  {
    middleName = newMiddleName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getLastName()
  {
    return lastName;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLastName(Object newLastName)
  {
    lastName = newLastName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object get(int propertyIndex, boolean resolve)
  {
    switch (propertyIndex)
    {
      case FIRST_NAME:
        return getFirstName();
      case MIDDLE_NAME:
        return getMiddleName();
      case LAST_NAME:
        return getLastName();
    }
    return super.get(propertyIndex, resolve);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void set(int propertyIndex, Object newValue)
  {
    switch (propertyIndex)
    {
      case FIRST_NAME:
        setFirstName((Object)newValue);
        return;
      case MIDDLE_NAME:
        setMiddleName((Object)newValue);
        return;
      case LAST_NAME:
        setLastName((Object)newValue);
        return;
    }
    super.set(propertyIndex, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unset(int propertyIndex)
  {
    switch (propertyIndex)
    {
      case FIRST_NAME:
        setFirstName(FIRST_NAME_DEFAULT_);
        return;
      case MIDDLE_NAME:
        setMiddleName(MIDDLE_NAME_DEFAULT_);
        return;
      case LAST_NAME:
        setLastName(LAST_NAME_DEFAULT_);
        return;
    }
    super.unset(propertyIndex);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSet(int propertyIndex)
  {
    switch (propertyIndex)
    {
      case FIRST_NAME:
        return FIRST_NAME_DEFAULT_ == null ? firstName != null : !FIRST_NAME_DEFAULT_.equals(firstName);
      case MIDDLE_NAME:
        return MIDDLE_NAME_DEFAULT_ == null ? middleName != null : !MIDDLE_NAME_DEFAULT_.equals(middleName);
      case LAST_NAME:
        return LAST_NAME_DEFAULT_ == null ? lastName != null : !LAST_NAME_DEFAULT_.equals(lastName);
    }
    return super.isSet(propertyIndex);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (isProxy(this)) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (firstName: ");
    result.append(firstName);
    result.append(", middleName: ");
    result.append(middleName);
    result.append(", lastName: ");
    result.append(lastName);
    result.append(')');
    return result.toString();
  }

} //CustomerImpl
