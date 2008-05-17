/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.implementation.bpel.example.helloworld.impl;

import commonj.sdo.helper.HelperContext;
import org.apache.tuscany.sdo.helper.HelperContextImpl;
import org.apache.tuscany.sdo.helper.TypeHelperImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

import org.apache.tuscany.implementation.bpel.example.helloworld.*;

import org.apache.tuscany.sdo.impl.FactoryBase;

import org.apache.tuscany.sdo.model.ModelFactory;

import org.apache.tuscany.sdo.model.impl.ModelFactoryImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * Generator information:
 * patternVersion=1.2; -prefix HelloWorld -noNotification -noUnsettable
 * <!-- end-user-doc -->
 * @generated
 */
public class HelloWorldFactoryImpl extends FactoryBase implements HelloWorldFactory
{

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String NAMESPACE_URI = "http://tuscany.apache.org/implementation/bpel/example/helloworld.wsdl";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String NAMESPACE_PREFIX = "tns";

	/**
	 * The version of the generator pattern used to generate this class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String PATTERN_VERSION = "1.2";
	
	public static final int HELLO = 1;
	
	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HelloWorldFactoryImpl()
	{
		super(NAMESPACE_URI, NAMESPACE_PREFIX, "org.apache.tuscany.implementation.bpel.example.helloworld");
	}

	/**
	 * Registers the Factory instance so that it is available within the supplied scope.
   * @argument scope a HelperContext instance that will make the types supported by this Factory available.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void register(HelperContext scope) 
	{
		if(scope == null) {
			throw new IllegalArgumentException("Scope can not be null");
		}

		if (((HelperContextImpl)scope).getExtendedMetaData().getPackage(NAMESPACE_URI) != null)
			return;
    
		// Register this package with provided scope   
		((HelperContextImpl)scope).getExtendedMetaData().putPackage(NAMESPACE_URI, this);
		
		//Register dependent packages with provided scope
		ModelFactory.INSTANCE.register(scope);
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
			case HELLO: return (DataObject)createhello();
			default:
				return super.create(typeNumber);
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public hello createhello()
	{
		helloImpl hello = new helloImpl();
		return hello;
	}
	
	// Following creates and initializes SDO metadata for the supported types.			
	protected Type helloType = null;

	public Type gethello()
	{
		return helloType;
	}
	

	private static HelloWorldFactoryImpl instance = null; 
	public static HelloWorldFactoryImpl init()
	{
		if (instance != null ) return instance;
		instance = new HelloWorldFactoryImpl();

		// Create package meta-data objects
		instance.createMetaData();

		// Initialize created meta-data
		instance.initializeMetaData();
		
		// Mark meta-data to indicate it can't be changed
		//theHelloWorldFactoryImpl.freeze(); //FB do we need to freeze / should we freeze ????

		return instance;
	}
  
	private boolean isCreated = false;

	public void createMetaData()
	{
		if (isCreated) return;
		isCreated = true;	

		// Create types and their properties
		helloType = createType(false, HELLO);
		createProperty(true, helloType,helloImpl._INTERNAL_MESSAGE); 
	}
	
	private boolean isInitialized = false;

	public void initializeMetaData()
	{
		if (isInitialized) return;
		isInitialized = true;

		// Obtain other dependent packages
		ModelFactoryImpl theModelPackageImpl = (ModelFactoryImpl)ModelFactoryImpl.init();
		Property property = null;

		// Add supertypes to types

		// Initialize types and properties
		initializeType(helloType, hello.class, "hello", false);
		property = getLocalProperty(helloType, 0);
		initializeProperty(property, theModelPackageImpl.getString(), "message", null, 1, 1, hello.class, false, false, false);

		createXSDMetaData(theModelPackageImpl);
	}
	  
	protected void createXSDMetaData(ModelFactoryImpl theModelPackageImpl)
	{
		super.initXSD();
		
		Property property = null;
		

		property = createGlobalProperty
		  ("hello",
		  this.gethello(),
			 new String[]
			 {
			 "kind", "element",
			 "name", "hello",
			 "namespace", "##targetNamespace"
			 });
                
		addXSDMapping
		  (helloType,
			 new String[] 
			 {
			 "name", "hello_._type",
			 "kind", "elementOnly"
			 });

		addXSDMapping
			(getLocalProperty(helloType, 0),
			 new String[]
			 {
			 "kind", "element",
			 "name", "message",
			 "namespace", "##targetNamespace"
			 });

  }
    
} //HelloWorldFactoryImpl
