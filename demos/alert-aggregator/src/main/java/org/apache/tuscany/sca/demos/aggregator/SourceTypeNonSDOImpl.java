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

import commonj.sdo.Sequence;
import commonj.sdo.Type;

import org.apache.tuscany.sca.demos.aggregator.types.SourceType;
import org.apache.tuscany.sca.demos.aggregator.types.TypesFactory;

import org.apache.tuscany.sdo.impl.DataObjectBase;


public class SourceTypeNonSDOImpl implements SourceType
{

  public final static int NAME = 0;

  public final static int ADDRESS = 1;

  public final static int LAST_CHECKED = 2;

  public final static int FEED_ADDRESS = 3;

  public final static int POP_SERVER = 4;

  public final static int POP_USERNAME = 5;

  public final static int POP_PASSWORD = 6;

  public final static int ANY = -1;

  public final static int ID = 7;

  public final static int TYPE = 8;

  public final static int SDO_PROPERTY_COUNT = 9;

  public final static int EXTENDED_PROPERTY_COUNT = -1;


  /**
   * The internal feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_NAME = 0;

  /**
   * The internal feature id for the '<em><b>Address</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_ADDRESS = 1;

  /**
   * The internal feature id for the '<em><b>Last Checked</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_LAST_CHECKED = 2;

  /**
   * The internal feature id for the '<em><b>Feed Address</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_FEED_ADDRESS = 3;

  /**
   * The internal feature id for the '<em><b>Pop Server</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_POP_SERVER = 4;

  /**
   * The internal feature id for the '<em><b>Pop Username</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_POP_USERNAME = 5;

  /**
   * The internal feature id for the '<em><b>Pop Password</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_POP_PASSWORD = 6;

  /**
   * The internal feature id for the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_ANY = 7;

  /**
   * The internal feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_ID = 8;

  /**
   * The internal feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */ 
  public final static int INTERNAL_TYPE = 9;

  /**
   * The number of properties for this type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  public final static int INTERNAL_PROPERTY_COUNT = 10;




  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_DEFAULT_;

  /**
   * The default value of the '{@link #getAddress() <em>Address</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAddress()
   * @generated
   * @ordered
   */
  protected static final String ADDRESS_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getAddress() <em>Address</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAddress()
   * @generated
   * @ordered
   */
  protected String address = ADDRESS_DEFAULT_;

  /**
   * The default value of the '{@link #getLastChecked() <em>Last Checked</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLastChecked()
   * @generated
   * @ordered
   */
  protected static final String LAST_CHECKED_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getLastChecked() <em>Last Checked</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLastChecked()
   * @generated
   * @ordered
   */
  protected String lastChecked = LAST_CHECKED_DEFAULT_;

  /**
   * The default value of the '{@link #getFeedAddress() <em>Feed Address</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFeedAddress()
   * @generated
   * @ordered
   */
  protected static final String FEED_ADDRESS_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getFeedAddress() <em>Feed Address</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFeedAddress()
   * @generated
   * @ordered
   */
  protected String feedAddress = FEED_ADDRESS_DEFAULT_;

  /**
   * The default value of the '{@link #getPopServer() <em>Pop Server</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopServer()
   * @generated
   * @ordered
   */
  protected static final String POP_SERVER_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getPopServer() <em>Pop Server</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopServer()
   * @generated
   * @ordered
   */
  protected String popServer = POP_SERVER_DEFAULT_;

  /**
   * The default value of the '{@link #getPopUsername() <em>Pop Username</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopUsername()
   * @generated
   * @ordered
   */
  protected static final String POP_USERNAME_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getPopUsername() <em>Pop Username</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopUsername()
   * @generated
   * @ordered
   */
  protected String popUsername = POP_USERNAME_DEFAULT_;

  /**
   * The default value of the '{@link #getPopPassword() <em>Pop Password</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopPassword()
   * @generated
   * @ordered
   */
  protected static final String POP_PASSWORD_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getPopPassword() <em>Pop Password</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopPassword()
   * @generated
   * @ordered
   */
  protected String popPassword = POP_PASSWORD_DEFAULT_;

  /**
   * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAny()
   * @generated
   * @ordered
   */
  
  protected Sequence any = null;
  
  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final String ID_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected String id = ID_DEFAULT_;

  /**
   * The default value of the '{@link #getFeedType() <em>Feed Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFeedType()
   * @generated
   * @ordered
   */
  protected static final String FEED_TYPE_DEFAULT_ = null;

  /**
   * The cached value of the '{@link #getFeedType() <em>Feed Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFeedType()
   * @generated
   * @ordered
   */
  protected String feedType = FEED_TYPE_DEFAULT_;
  

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SourceTypeNonSDOImpl()
  {
    super();
  }



  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    name = newName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getAddress()
  {
    return address;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAddress(String newAddress)
  {
    address = newAddress;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLastChecked()
  {
    return lastChecked;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLastChecked(String newLastChecked)
  {
    lastChecked = newLastChecked;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getFeedAddress()
  {
    return feedAddress;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFeedAddress(String newFeedAddress)
  {
    feedAddress = newFeedAddress;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPopServer()
  {
    return popServer;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPopServer(String newPopServer)
  {
    popServer = newPopServer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPopUsername()
  {
    return popUsername;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPopUsername(String newPopUsername)
  {
    popUsername = newPopUsername;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPopPassword()
  {
    return popPassword;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPopPassword(String newPopPassword)
  {
    popPassword = newPopPassword;
  }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getId()
  {
    return id;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setId(String newId)
  {
    id = newId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getFeedType()
  {
    return feedType;
  }
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFeedType(String newType)
  {
    feedType = newType;
  }
  
  public Sequence getAny(){
      return null;
  }

} //SourceTypeImpl
