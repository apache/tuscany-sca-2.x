/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.example.creditscore.doclit;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @generated
 */
public class DoclitFactory extends EFactoryImpl
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final DoclitFactory eINSTANCE = init();

  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final DoclitFactory INSTANCE = org.example.creditscore.doclit.DoclitFactory.eINSTANCE;

  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static DoclitFactory init()
  {
    try
    {
      DoclitFactory theDoclitFactory = (DoclitFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.example.org/creditscore/doclit/"); 
      if (theDoclitFactory != null)
      {
        return theDoclitFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DoclitFactory();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DoclitFactory()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case DoclitPackage.CREDIT_REPORT: return (EObject)createCreditReport();
      case DoclitPackage.CUSTOMER: return (EObject)createCustomer();
      case DoclitPackage.DOCUMENT_ROOT: return (EObject)createDocumentRoot();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CreditReport createCreditReport()
  {
    CreditReport creditReport = new CreditReport();
    return creditReport;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Customer createCustomer()
  {
    Customer customer = new Customer();
    return customer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject createDocumentRoot()
  {
    EObject documentRoot = super.create(DoclitPackage.Literals.DOCUMENT_ROOT);
    return documentRoot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DoclitPackage getDoclitPackage()
  {
    return (DoclitPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  public static DoclitPackage getPackage()
  {
    return DoclitPackage.eINSTANCE;
  }

} //DoclitFactory
