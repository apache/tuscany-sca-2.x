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

/**
 * This is a Java Bean which has setter and getter methods for for the variables used in the property tests.
 */
public class ComplexPropertyBean {

    /**
     * Variable declarations
     */
    protected int integerNumber = 25;
    protected float floatNumber = 50;
    protected double doubleNumber = 75;
    protected int[] intArray = null;
    protected double[] doubleArray = null;
    protected String[] stringArray = null;

    ComplexPropertyBean numberSet;
    protected ComplexPropertyBean[] numberSetArray = null;

    public ComplexPropertyBean() {

    }

    public double getDoubleNumber() {
        return doubleNumber;
    }

    /**
     * It is a setter method to set a value for the variable doubleNumber
     */
    public void setDoubleNumber(double doubleNumber) {
        this.doubleNumber = doubleNumber;
    }

    /**
     * It is a getter method to get a value for the variable floatNumber
     */
    public float getFloatNumber() {
        return floatNumber;
    }

    /**
     * It is a setter method to set a value for the variable floatNumber
     */
    public void setFloatNumber(float floatNumber) {
        this.floatNumber = floatNumber;
    }

    /**
     * It is a getter method to get a value for the variable floatNumber
     */
    public int getIntegerNumber() {
        return integerNumber;
    }

    /**
     * It is a getter method to get a value for the variable integerNumber
     */
    public void setIntegerNumber(int integerNumber) {
        this.integerNumber = integerNumber;
    }

    /**
     * It is a getter method to set a value for the variable numberSet
     */
    public ComplexPropertyBean getNumberSet() {
        return numberSet;
    }

    /**
     * It is a setter method to set a value for the variable numberSet
     */
    public void setNumberSet(ComplexPropertyBean numberSet) {
        this.numberSet = numberSet;
    }

    /**
     * It is a method which implements toString() function
     */
    @Override
    public String toString() {
        return Double.toString(integerNumber) + " - "
            + Double.toString(floatNumber)
            + " - "
            + Double.toString(doubleNumber)
            + " \n"
            + ((intArray == null) ? "" : intArray[0] + " - " + intArray[1] + " \n ")
            + ((doubleArray == null) ? "" : doubleArray[0] + " - " + doubleArray[1] + " \n ")
            + ((stringArray == null) ? "" : stringArray[0] + " - " + stringArray[1] + " \n ")
            + ((numberSetArray == null) ? "" : numberSetArray[0] + " - " + numberSetArray[1] + " \n ")
            + ((numberSet == null) ? "" : numberSet.toString());
    }

    /**
     * It is a setter method to set values for the variable stringArray
     */
    public String[] getStringArray() {
        return stringArray;
    }

    /**
     * It is a setter method to set values for the variable stringArray
     */
    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public double[] getDoubleArray() {
        return doubleArray;
    }

    public void setDoubleArray(double[] doubleArray) {
        this.doubleArray = doubleArray;
    }

    public ComplexPropertyBean[] getNumberSetArray() {
        return numberSetArray;
    }

    public void setNumberSetArray(ComplexPropertyBean[] numberSetArray) {
        this.numberSetArray = numberSetArray;
    }
}
