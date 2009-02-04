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

import org.apache.tuscany.sca.demos.aggregator.types.ConfigType;


public class ConfigTypeNonSDOImpl implements ConfigType
{
    private static final long serialVersionUID = 2522843951360675364L;
    
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
