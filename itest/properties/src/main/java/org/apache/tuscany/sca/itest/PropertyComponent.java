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

import test.jaxb.props.ReturnCodeProperties;
/**
 * It is an Interface which has method declarations. Methods which are to be accessed as a service are declared in this
 * interface. Implementation for these methods is provided in PropertyComponentImpl Class
 */
public interface PropertyComponent {

    /**
     * This is a method to get a value for a property name 'location' from the SCA runtime environment. It is
     * implemented in the class PropertyComponentImpl
     */
    public String getLocation();

    /**
     * This is a method to get a value for a property name 'year' from the SCA runtime environment. It is implemented in
     * the class PropertyComponentImpl
     */
    public String getYear();

    /**
     * This method is used to test injecting a primitive String Array
     * 
     * @return The injected array
     */
    public String[] getDaysOfTheWeek();

    /**
     * This method is used to test injecting an Object Integer Array
     * 
     * @return The injected array
     */
    public Integer[] getIntegerNumbers();

    /**
     * This method is used to test injecting an int Array
     * 
     * @return The injected array
     */
    public int[] getIntNumbers();

    /**
     * This method is used to test injecting an Object Array
     * 
     * @return The injected array
     */
    public DataObject[] getSdoArrayProperty();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */

    public ComplexPropertyBean getComplexPropertyOne();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public ComplexPropertyBean getComplexPropertyTwo();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public ComplexPropertyBean getComplexPropertyThree();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public Collection<ComplexPropertyBean> getComplexPropertyFour();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */

    public ComplexPropertyBean getComplexPropertyFive();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public DataObject getSdoProperty();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public DataObject getCustomerSdo();
    
    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public ReturnCodeProperties getComplexJAXBPropertyOne();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * PropertyComponentImpl
     */
    public ReturnCodeProperties getComplexJAXBPropertyTwo();

    /**
     * @return
     */
    public String getLocationFromComponentContext();

}
