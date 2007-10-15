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

package org.apache.tuscany.sca.itest;

import java.util.Collection;

import commonj.sdo.DataObject;


public interface PropertyComponent {
    public String getLocation();
    public String getLocationFromComponentContext();
    public String getYear();
    
    /**
     * This method is used to test injecting a primitive String Array
     * @return The injected array
     */
    public String[] getDaysOfTheWeek();

    /**
     * This method is used to test injecting an Object Integer Array
     * @return The injected array
     */
    public Integer[] getIntegerNumbers();
    
    /**
     * This method is used to test injecting an int Array
     * @return The injected array
     */
    public int[] getIntNumbers();

    /**
     * This method is used to test injecting an Object Array
     * @return The injected array
     */
    public DataObject[] getSdoArrayProperty();
    
    public ComplexPropertyBean getComplexPropertyOne();
    public ComplexPropertyBean getComplexPropertyTwo();
    public ComplexPropertyBean getComplexPropertyThree();
    public Collection<ComplexPropertyBean> getComplexPropertyFour();
    public DataObject getSdoProperty();
    public DataObject getCustomerSdo();
}
