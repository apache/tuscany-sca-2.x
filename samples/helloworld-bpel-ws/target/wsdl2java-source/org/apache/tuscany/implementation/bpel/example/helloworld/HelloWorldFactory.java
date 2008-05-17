/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.implementation.bpel.example.helloworld;

import commonj.sdo.helper.HelperContext;
import org.apache.tuscany.sdo.helper.HelperContextImpl;


/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @generated
 */
public interface HelloWorldFactory
{

	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	HelloWorldFactory INSTANCE = org.apache.tuscany.implementation.bpel.example.helloworld.impl.HelloWorldFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>hello</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>hello</em>'.
	 * @generated
	 */
	hello createhello();

  /**
   * Registers the types supported by this Factory within the supplied scope.argument
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param scope an instance of HelperContext used to manage the scoping of types.
	 * @generated
   */
  public void register(HelperContext scope);
   
} //HelloWorldFactory
