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

public class ComplexPropertyBean {

    protected int integerNumber = 25;
    public float floatNumber = 50;
    public double doubleNumber = 75;
    public int[] intArray = null;
    public double[] doubleArray = null;
    protected String[] stringArray = null;
    
    ComplexPropertyBean numberSet;
    public ComplexPropertyBean[] numberSetArray = null;
    
    public ComplexPropertyBean() {
        
    }

    public double getDoubleNumber() {
        return doubleNumber;
    }

    public void setDoubleNumber(double doubleNumber) {
        this.doubleNumber = doubleNumber;
    }

    public float getFloatNumber() {
        return floatNumber;
    }

    public void setFloatNumber(float floatNumber) {
        this.floatNumber = floatNumber;
    }

    public int getIntegerNumber() {
        return integerNumber;
    }

    public void setIntegerNumber(int integerNumber) {
        this.integerNumber = integerNumber;
    }

    public ComplexPropertyBean getNumberSet() {
        return numberSet;
    }

    public void setNumberSet(ComplexPropertyBean numberSet) {
        this.numberSet = numberSet;
    }
    
    public String toString() {
        return Double.toString(integerNumber) + " - " + 
                Double.toString(floatNumber) + " - " + 
                Double.toString(doubleNumber) + " \n" + 
                ((intArray == null ) ? "" : intArray[0] + " - " + intArray[1] + " \n " ) +
                ((doubleArray == null ) ? "" : doubleArray[0] + " - " + doubleArray[1] + " \n " ) +
                ((stringArray == null ) ? "" : stringArray[0] + " - " + stringArray[1] + " \n " ) +
                ((numberSetArray == null ) ? "" : numberSetArray[0] + " - " + numberSetArray[1] + " \n " ) +
                ((numberSet == null ) ? "" : numberSet.toString());
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }
}
