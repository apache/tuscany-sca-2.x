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

import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;

import com.example.customer.sdo.impl.CustomerImpl;
import commonj.sdo.DataObject;

import test.jaxb.props.ReturnCodeProperties;

/**
 * This class Implements the interface PropertyComponent and gives implementation for all methods which are declared in
 * that interface.
 */
public class PropertyComponentImpl implements PropertyComponent {
    @Context
    protected ComponentContext context;
    
    @Property
    protected CustomerImpl customerSdo;

    @Property
    protected DataObject sdoProperty;

    @Property
    protected ComplexPropertyBean complexPropertyOne;

    @Property
    protected ComplexPropertyBean complexPropertyTwo;

    @Property
    protected ComplexPropertyBean complexPropertyThree;

    @Property
    protected Collection<ComplexPropertyBean> complexPropertyFour;

    @Property
    protected ComplexPropertyBean complexPropertyFive;

    @Property(name = "location")
    protected String location;

    @Property(name = "year")
    protected String year;

    @Property(name = "daysOfTheWeek")
    protected String[] daysOfTheWeek;

    @Property(name = "integerNumbers")
    protected Integer[] integerNumbers;

    @Property(name = "intNumbers")
    protected int[] intNumbers;

    @Property(name = "sdoArray")
    protected DataObject[] sdoArray;

    @Property
    protected ReturnCodeProperties complexJAXBPropertyOne;

    @Property
    protected ReturnCodeProperties complexJAXBPropertyTwo;

    public String getLocation() {
        return location;
    }

    public String getYear() {
        return year;
    }

    public ComplexPropertyBean getComplexPropertyOne() {
        //System.out.println(complexPropertyOne);
        return complexPropertyOne;
    }

    public ComplexPropertyBean getComplexPropertyTwo() {
        //System.out.println(complexPropertyTwo);
        return complexPropertyTwo;
    }

    public ComplexPropertyBean getComplexPropertyThree() {
        //System.out.println(complexPropertyThree);
        return complexPropertyThree;
    }

    public Collection<ComplexPropertyBean> getComplexPropertyFour() {
        //System.out.println(complexPropertyThree);
        return complexPropertyFour;
    }

    public ComplexPropertyBean getComplexPropertyFive() {
        return complexPropertyFive;
    }

    public DataObject getSdoProperty() {
        return sdoProperty;
    }

    /**
     * It is a method which injects the property value to the variable 'sdoProperty' from the SCA runtime environment.
     */
    public void setSdoProperty(DataObject sdoProperty) {
        System.out.println("Reached here da machi");
        this.sdoProperty = sdoProperty;
    }

    public CustomerImpl getCustomerSdo() {
        return customerSdo;
    }

    /**
     * It is a method which injects the property value to the variable 'customerSdo' from the SCA runtime environment.
     */
    public void setCustomerSdo(CustomerImpl customerSdo) {
        this.customerSdo = customerSdo;
    }

    /**
     * This method is used to test injecting an Array
     * 
     * @return The injected array
     */
    public String[] getDaysOfTheWeek() {
        return daysOfTheWeek;
    }

    /**
     * This method is used to test injecting an Object Integer Array
     * 
     * @return The injected array
     */
    public Integer[] getIntegerNumbers() {
        return integerNumbers;
    }

    /**
     * This method is used to test injecting an int Array
     * 
     * @return The injected array
     */
    public int[] getIntNumbers() {
        return intNumbers;
    }

    /**
     * This method is used to test injecting an Object Array
     * 
     * @return The injected array
     */
    public DataObject[] getSdoArrayProperty() {
        return sdoArray;
    }
    
    public String getLocationFromComponentContext() {
        return context.getProperty(String.class, "location");
    }

    public ReturnCodeProperties getComplexJAXBPropertyOne() {
        return complexJAXBPropertyOne;
    }

    public ReturnCodeProperties getComplexJAXBPropertyTwo() {
        return complexJAXBPropertyTwo;
    }
}
