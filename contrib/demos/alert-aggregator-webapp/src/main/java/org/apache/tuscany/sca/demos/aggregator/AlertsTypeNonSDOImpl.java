/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.demos.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;

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
    private static final long serialVersionUID = -3784576466148158776L;
    
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
