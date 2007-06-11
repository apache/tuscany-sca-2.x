/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.sca.demos.aggregator;

import commonj.sdo.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.demos.aggregator.types.AlertType;
import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;
import org.apache.tuscany.sca.demos.aggregator.types.TypesFactory;

import org.apache.tuscany.sdo.impl.DataObjectBase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Alerts Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.sca.samples.aggregator.types.impl.AlertsTypeImpl#getAlert <em>Alert</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AlertsTypeNonSDOImpl implements AlertsType
{

  /**
   * The cached value of the '{@link #getAlert() <em>Alert</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAlert()
   * @generated
   * @ordered
   */
  
  protected List alert = new ArrayList();
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AlertsTypeNonSDOImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getAlert()
  {
    return alert;
  }

} //AlertsTypeImpl
