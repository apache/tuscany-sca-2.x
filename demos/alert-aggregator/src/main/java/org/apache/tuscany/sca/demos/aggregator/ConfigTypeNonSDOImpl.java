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

import org.apache.tuscany.sca.demos.aggregator.types.ConfigType;
import org.apache.tuscany.sca.demos.aggregator.types.SourceType;
import org.apache.tuscany.sca.demos.aggregator.types.TypesFactory;

import org.apache.tuscany.sdo.impl.DataObjectBase;


public class ConfigTypeNonSDOImpl implements ConfigType
{
  
  protected List source = new ArrayList();
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigTypeNonSDOImpl()
  {
    super();
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List getSource()
  {
    return source;
  }


} //ConfigTypeImpl
