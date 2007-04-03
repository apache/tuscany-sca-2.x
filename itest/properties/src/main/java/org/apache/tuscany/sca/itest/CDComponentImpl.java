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

public class CDComponentImpl implements CDComponent {

    private String cProperty;
    private String dProperty;
    private String nosource;
    private String fileProperty;
    private Collection<String> manyValuesFileProperty;
    private int overrideNumber;
    private String cProperty2;

    @Property(name="nonFileProperty")
    public void setC2(final String value) {
        this.cProperty2 = value;
    }
    @Property(name="two")
    public void setOverrideNumber(final int value) {
        this.overrideNumber = value;
    }
    
    @Property(name="fileProperty")
    public void setFileProp(final String value) {
        this.fileProperty = value;
    }
    
    @Property(name="manyValuesFileProperty")
    public void setFileManyValueProp(final Collection<String> values) {
        this.manyValuesFileProperty = values;
    }
    
    @Property
    public void setC(final String C) {
        this.cProperty = C;
    }

    @Property
    public void setD(final String D) {
        this.dProperty = D;
    }
    
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

