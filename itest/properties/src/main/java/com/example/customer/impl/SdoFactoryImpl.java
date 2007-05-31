/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.example.customer.impl;

import org.apache.tuscany.sdo.SDOFactory;
import org.apache.tuscany.sdo.helper.TypeHelperImpl;
import org.apache.tuscany.sdo.impl.FactoryBase;
import org.apache.tuscany.sdo.model.ModelFactory;
import org.apache.tuscany.sdo.model.impl.ModelFactoryImpl;
import org.apache.tuscany.sdo.model.internal.InternalFactory;
import org.apache.tuscany.sdo.util.SDOUtil;

import com.example.customer.Customer;
import com.example.customer.SdoFactory;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * Generator information:
 * patternVersion=1.1; -prefix Sdo -noNotification -noUnsettable
 * <!-- end-user-doc -->
 * @generated
 */
public class SdoFactoryImpl extends FactoryBase implements SdoFactory
{

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String NAMESPACE_URI = "http://www.example.com/Customer";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String NAMESPACE_PREFIX = "cust";

  /**
   * The version of the generator pattern used to generate this class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final String PATTERN_VERSION = "1.1";
  
  public static final int CUSTOMER = 1;
  
  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SdoFactoryImpl()
  {
    super(NAMESPACE_URI, NAMESPACE_PREFIX, "com.example.customer");
  }

  /**
   * Registers the Factory instance so that it is available within the supplied scope.
   * @argument scope a HelperContext instance that will make the types supported by this Factory available.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */	
  public void register(HelperContext scope) {
    if(scope == null) {
       throw new IllegalArgumentException("Scope can not be null");
    } 
    TypeHelperImpl th = (TypeHelperImpl)scope.getTypeHelper();
    th.getExtendedMetaData().putPackage(NAMESPACE_URI, this);
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataObject create(int typeNumber)
  {
    switch (typeNumber)
    {
      case CUSTOMER: return (DataObject)createCustomer();
      default:
        return super.create(typeNumber);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Customer createCustomer()
  {
    CustomerImpl customer = new CustomerImpl();
    return customer;
  }
  
  // Following creates and initializes SDO metadata for the supported types.		
  protected Type customerType = null;

  public Type getCustomer()
  {
    return customerType;
  }
  

  private static boolean isInited = false;

  public static SdoFactoryImpl init()
  {
    if (isInited) return (SdoFactoryImpl)FactoryBase.getStaticFactory(SdoFactoryImpl.NAMESPACE_URI);
    SdoFactoryImpl theSdoFactoryImpl = new SdoFactoryImpl();
    isInited = true;

    // Initialize simple dependencies
    SDOUtil.registerStaticTypes(SDOFactory.class);
    SDOUtil.registerStaticTypes(ModelFactory.class);
    SDOUtil.registerStaticTypes(InternalFactory.class);

    // Create package meta-data objects
    theSdoFactoryImpl.createMetaData();

    // Initialize created meta-data
    theSdoFactoryImpl.initializeMetaData();

    // Mark meta-data to indicate it can't be changed
    //theSdoFactoryImpl.freeze(); //FB do we need to freeze / should we freeze ????

    return theSdoFactoryImpl;
  }
  
  private boolean isCreated = false;

  public void createMetaData()
  {
    if (isCreated) return;
    isCreated = true;	

    // Create types and their properties
          customerType = createType(false, CUSTOMER);
    createProperty(true, customerType,CustomerImpl.INTERNAL_FIRST_NAME); 
    createProperty(true, customerType,CustomerImpl.INTERNAL_MIDDLE_NAME); 
    createProperty(true, customerType,CustomerImpl.INTERNAL_LAST_NAME); 
  }
  
  private boolean isInitialized = false;

  public void initializeMetaData()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Obtain other dependent packages
    ModelFactoryImpl theModelPackageImpl = (ModelFactoryImpl)FactoryBase.getStaticFactory(ModelFactoryImpl.NAMESPACE_URI);
    Property property = null;

    // Add supertypes to types

    // Initialize types and properties
    initializeType(customerType, Customer.class, "Customer", false);
    property = getProperty(customerType, CustomerImpl.INTERNAL_FIRST_NAME);
    initializeProperty(property, theModelPackageImpl.getObject(), "firstName", null, 1, 1, Customer.class, false, false, false);

    property = getProperty(customerType, CustomerImpl.INTERNAL_MIDDLE_NAME);
    initializeProperty(property, theModelPackageImpl.getObject(), "middleName", null, 1, 1, Customer.class, false, false, false);

    property = getProperty(customerType, CustomerImpl.INTERNAL_LAST_NAME);
    initializeProperty(property, theModelPackageImpl.getObject(), "lastName", null, 1, 1, Customer.class, false, false, false);

    createXSDMetaData(theModelPackageImpl);
  }
    
  protected void createXSDMetaData(ModelFactoryImpl theModelPackageImpl)
  {
    super.initXSD();
    
    Property property = null;
    

    addXSDMapping
      (customerType,
       new String[] 
       {
       "name", "Customer",
       "kind", "elementOnly"
       });

    addXSDMapping
      (getProperty(customerType, CustomerImpl.INTERNAL_FIRST_NAME),
       new String[]
       {
       "kind", "element",
       "name", "firstName"
       });

    addXSDMapping
      (getProperty(customerType, CustomerImpl.INTERNAL_MIDDLE_NAME),
       new String[]
       {
       "kind", "element",
       "name", "middleName"
       });

    addXSDMapping
      (getProperty(customerType, CustomerImpl.INTERNAL_LAST_NAME),
       new String[]
       {
       "kind", "element",
       "name", "lastName",
       "namespace", "http://www.w3.org/2001/XMLSchema"
       });

  }
    
} //SdoFactoryImpl
