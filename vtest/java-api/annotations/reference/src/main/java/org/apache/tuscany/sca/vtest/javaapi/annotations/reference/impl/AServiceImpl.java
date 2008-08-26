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

package org.apache.tuscany.sca.vtest.javaapi.annotations.reference.impl;

import java.util.List;

import org.apache.tuscany.sca.vtest.javaapi.annotations.reference.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.reference.BService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
public class AServiceImpl implements AService {

    @Reference
    protected BService b1; // field injection

    protected BService b2; // injected via constructor parameter

    protected BService b3; // setter injection

    public BService b4; // field injection (public, un-annotated)

    protected BService b5; // field injection (non-public, un-annotated)

    public BService b6; // setter injection (public, un-annotated)

    @Reference
    protected BService b7; // setter injection (field and setter annotated)

    @Reference(name="b8", required=false)
    protected BService bEight; // field injection (different reference and field name)

    protected BService bNine; // setter injection (different reference and field name)

    @Reference(required=false)
    protected BService b10; // multiplicity="0..1" and required=false

    @Reference(required=false)
    protected BService b11; // multiplicity="1..1" and required=false

    protected BService b12; // multiplicity="1..1" and required=true at setter

    @Reference(required=false)
    protected List<BService> b13; // multiplicity="0..n" and required=false

    protected List<BService> b14; // multiplicity="1..n" and required=false at setter

    @Reference(name="b15", required=true)
    protected BService[] b15s; // multiplicity="1..n" and required=true

	@Reference(required=false)
    protected BService b16;

	@Reference(required=false)
    public BService b17;
	
    protected boolean b7SetterCalled;

    public AServiceImpl(@Reference(name = "b2")
    BService b2) {
        super();
        this.b2 = b2;
        b7SetterCalled = false;
    }

    @Reference
    public void setB3(BService b3) {
        this.b3 = b3;
    }

    public void setB6(BService b6) {
        this.b6 = b6;
    }

    @Reference
    public void setB7(BService b7) {
        b7SetterCalled = true;
        this.b7 = b7;
    }

    @Reference(name="b9", required=false)
    public void setB9(BService bNine) {
        this.bNine = bNine;
    }
    
    @Reference(required=true)
    public void setB12(BService b12) {
        this.b12 = b12;
    }
    
    @Reference(required=true)
    public void setB14(List<BService> b14) {
        this.b14 = b14;
    }
    
    public String getName() {
        return "AService";
    }

    public String getB1Name() {
        return b1.getName();
    }

    public String getB2Name() {
        return b2.getName();
    }

    public String getB3Name() {
        return b3.getName();
    }

    public String getB4Name() {
        return b4.getName();
    }

    public String getB5Name() {
        return b5.getName();
    }

    public String getB6Name() {
        return b6.getName();
    }

    public String getB7Name() {
        return b7.getName();
    }

    public String getB8Name() {
        return bEight.getName();
    }
    
    public String getB9Name() {
        return bNine.getName();
    }
    
    public String getB10Name() {
        return b10.getName();
    }

    public String getB11Name() {
        return b11.getName();
    }
    
    public String getB12Name() {
        return b12.getName();
    }
    
    public String getB13Name(int i) {
    	BService b = (BService) b13.get(i);
        return b.getName();
    }
    
    public String getB14Name(int i) {
    	BService b = (BService) b14.get(i);
        return b.getName();
    }
    
    public String getB15Name(int i) {
        return b15s[i].getName();
    }
    
    public int getB13Size() {
    	return b13.size();
    }
    
    public int getB14Size() {
    	return b14.size();
    }
    
    public int getB15Size() {
    	return b15s.length;
    }
    
    public boolean isB7SetterCalled() {
        return b7SetterCalled;
    }
    
    public boolean isB16Null() {
        return b16 == null;
    }

    public boolean isB17Null() {
        return b17 == null;
    }

    public boolean isB4Null() {
        return b4 == null;
    }
    
    public boolean isB5Null() {
        return b5 == null;
    }
    
    public boolean isB6Null() {
        return b6 == null;
    }    
}
