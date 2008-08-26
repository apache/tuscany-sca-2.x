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

import org.apache.tuscany.sca.vtest.javaapi.annotations.reference.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.reference.BService;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
public class AUnannotatedServiceImpl implements AService {

    public BService b4; // field injection (public, un-annotated)

    protected BService b5; // field injection (non-public, un-annotated)
    
    public BService b6; // setter injection (public, un-annotated)

    public AUnannotatedServiceImpl() {
    }

    public String getName() {
        return "AService";
    }

    public String getB4Name() {
        return b4.getName();
    }

    public String getB5Name() {
        return b5.getName();
    }

    public void setB6(BService b6) {
        this.b6 = b6;
    }

    public String getB6Name() {
        return b6.getName();
    }

    public String getB1Name() {
        return null;
    }

    public String getB2Name() {
        return null;
    }

    public String getB3Name() {
        return null;
    }

    public String getB7Name() {
        return null;
    }

    public String getB8Name() {
        return null;
    }

    public String getB9Name() {
        return null;
    }

    public boolean isB7SetterCalled() {
        return false;
    }

    public String getB10Name() {
        return null;
    }

    public String getB11Name() {
        return null;
    }

    public String getB12Name() {
        return null;
    }

    public String getB13Name(int i) {
        return null;
    }

    public int getB13Size() {
        return 0;
    }

    public String getB14Name(int i) {
        return null;
    }

    public int getB14Size() {
        return 0;
    }

    public String getB15Name(int i) {
        return null;
    }

    public int getB15Size() {
        return 0;
    }
    
    public boolean isB16Null() {
        return true;
    }

    public boolean isB17Null() {
        return true;
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
