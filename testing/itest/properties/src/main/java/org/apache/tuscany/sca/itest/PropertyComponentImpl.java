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

import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Property;

import test.jaxb.props.ReturnCodeProperties;

/**
 * This class Implements the interface PropertyComponent and gives implementation for all methods which are declared in
 * that interface.
 */
public class PropertyComponentImpl implements PropertyComponent {
    @Context
    protected ComponentContext context;

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
    
    @Property
    public void setComplexPropertyOne(ComplexPropertyBean complexPropertyOne){
        this.complexPropertyOne = complexPropertyOne;
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
