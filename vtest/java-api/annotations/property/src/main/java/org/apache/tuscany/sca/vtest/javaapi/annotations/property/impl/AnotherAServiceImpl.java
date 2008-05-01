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

package org.apache.tuscany.sca.vtest.javaapi.annotations.property.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.property.AnotherAService;
import org.osoa.sca.annotations.Service;

@Service(AnotherAService.class)
public class AnotherAServiceImpl implements AnotherAService {

    public String p13;              // injected via field and un-annotated
    
    public String p14;              // injected via setter and un-annotated
    
    protected String p22;           // unannotated protected field should not be injected

    protected String p23;			// un-annotated protected and has protected setter 

    protected String p24;			// un-annotated protected field and has public setter

    private String p25;				// un-annotated private field and has public setter
    
    public boolean p14SetterIsCalled = false;
    
    public boolean p23SetterIsCalled = false;
    
    public boolean p24SetterIsCalled = false;
    
    public boolean p25SetterIsCalled = false;
    
    
    public String getName() {
        return "AService";
    }
    
    public void setP14(String p14) {
        p14SetterIsCalled = true;
        this.p14 = p14;
    }

    protected void setP23(String p23) {
        p23SetterIsCalled = true;
        this.p23 = p23;
    }

    public void setP24(String p24) {
        p24SetterIsCalled = true;
        this.p24 = p24;
    }

    public void setP25(String p25) {
        p25SetterIsCalled = true;
        this.p25 = p25;
    }

    public String getP13() {
        return p13;
    }
    
    public String getP14() {
        return p14;
    }
    
    public String getP22() {
        return p22;
    }

    public String getP23() {
        return p23;
    }

    public String getP24() {
        return p24;
    }

    public String getP25() {
        return p25;
    }

    public boolean getP14SetterIsCalled() {
        return p14SetterIsCalled;
    }
    

    public boolean getP23SetterIsCalled() {
        return p23SetterIsCalled;
    }
    

    public boolean getP24SetterIsCalled() {
        return p24SetterIsCalled;
    }
    

    public boolean getP25SetterIsCalled() {
        return p25SetterIsCalled;
    }

}
