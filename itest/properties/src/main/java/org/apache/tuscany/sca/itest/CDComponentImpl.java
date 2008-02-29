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
 * This class Implements the interface CDComponent and gives implementation for all methods which are declared in that
 * interface.
 */
public class CDComponentImpl implements CDComponent {

    private String cProperty;
    private String dProperty;
    private String nosource;
    private String fileProperty;
    private Collection<String> manyValuesFileProperty;
    private int overrideNumber;
    private String cProperty2;

    /**
     * It is a method which injects the property value to the variable 'cProperty2' from the SCA runtime environment
     * using
     * 
     * @Property Annotations.
     */
    @Property(name = "nonFileProperty")
    public void setC2(final String value) {
        this.cProperty2 = value;
    }

    /**
     * It is a method which injects the property value to the variable 'overrideNumber' from the SCA runtime environment
     * using
     * 
     * @Property Annotations.
     */
    @Property(name = "two")
    public void setOverrideNumber(final int value) {
        this.overrideNumber = value;
    }

    /**
     * It is a method which injects the property value to the variable 'fileProperty' from the SCA runtime environment
     * using
     * 
     * @Property Annotations.
     */
    @Property(name = "fileProperty")
    public void setFileProp(final String value) {
        this.fileProperty = value;
    }

    /**
     * It is a method which injects the property value to the variable 'manyValuesFileProperty' from the SCA runtime
     * environment using
     * 
     * @Property Annotations.
     */
    @Property(name = "manyValuesFileProperty")
    public void setFileManyValueProp(final Collection<String> values) {
        this.manyValuesFileProperty = values;
    }

    /**
     * It is a method which injects the property value to the variable 'cProperty' from the SCA runtime environment
     * using
     * 
     * @Property Annotations.
     */
    @Property
    public void setC(final String C) {
        this.cProperty = C;
    }

    /**
     * It is a method which injects the property value to the variable 'dProperty' from the SCA runtime environment
     * using
     * 
     * @Property Annotations.
     */
    @Property
    public void setD(final String D) {
        this.dProperty = D;
    }

    /**
     * It is a method which injects the property value to the variable 'nosource' from the SCA runtime environment using
     * 
     * @Property Annotations.
     */
    @Property
    public void setNosource(final String value) {
        this.nosource = value;
    }

    public String getFileProperty() {
        return this.fileProperty;
    }

    public String getC() {
        return this.cProperty;
    }

    public String getC2() {
        return this.cProperty2;
    }

    public String getD() {
        return this.dProperty;
    }

    public String getNoSource() {
        return this.nosource;
    }

    public int getOverrideValue() {
        return this.overrideNumber;
    }

    public Collection<String> getManyValuesFileProperty() {
        return this.manyValuesFileProperty;
    }
}
