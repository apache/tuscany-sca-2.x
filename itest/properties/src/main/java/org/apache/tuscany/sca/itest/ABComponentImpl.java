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

import org.osoa.sca.annotations.Property;

/**
 * This class Implements the interface ABComponent and gives implementation for all methods which are declared in that
 * interface.
 */

public class ABComponentImpl implements ABComponent {

    private String aProperty;
    private String bProperty;
    private int intValue;
    private Collection<String> manyStringValues;
    private Collection<Integer> manyIntegerValues;
    private String zProperty;
    private String fProperty;

    /**
     * It is a method which injects the property value to the variable 'zProperty' from the SCA runtime environment
     * using Annotations.
     */
    @Property(name = "xpath")
    public void setZProperty(final String value) {
        this.zProperty = value;
    }

    /**
     * It is a method which injects collection of property values to the variable 'manyStringValues' from the SCA
     * runtime environment using Annotations.
     */
    @Property(name = "foobar")
    public void setManyStringValues(final Collection<String> value) {
        this.manyStringValues = value;
    }

    /**
     * It is a method which injects collection of integer property values to the variable 'manyIntegerValues' from the
     * SCA runtime environment using Annotations.
     */
    @Property(name = "fooInts")
    public void setManyIntegers(final Collection<Integer> value) {
        this.manyIntegerValues = value;
    }

    /**
     * It is a method which injects the property value to the variable 'aProperty' from the SCA runtime environment
     * using Annotations.
     */
    @Property
    public void setA(final String A) {
        this.aProperty = A;
    }

    /**
     * It is a method which injects the property value to the variable 'bProperty' from the SCA runtime environment
     * using Annotations.
     */
    @Property
    public void setB(final String B) {
        this.bProperty = B;
    }

    /**
     * It is a method which injects the property value to the variable 'fProperty' from the SCA runtime environment
     * using Annotations.
     */
    @Property
    public void setF(final String F) {
        this.fProperty = F;
    }

    /**
     * It is a method which injects integer property value to the variable 'intValue' from the SCA runtime environment
     * using Annotations.
     */
    @Property
    public void setOne(final int value) {
        this.intValue = value;
    }

    public String getA() {
        return this.aProperty;
    }

    public String getB() {
        return this.bProperty;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public String getZ() {
        return this.zProperty;
    }

    public String getF() {
        return this.fProperty;
    }

    public Collection<String> getManyStringValues() {
        return manyStringValues;
    }

    public Collection<Integer> getManyIntegers() {
        return manyIntegerValues;
    }
}
