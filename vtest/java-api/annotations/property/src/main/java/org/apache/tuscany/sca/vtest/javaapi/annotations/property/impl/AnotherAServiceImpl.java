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

import org.apache.tuscany.sca.vtest.javaapi.annotations.property.AService;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
public class AnotherAServiceImpl implements AService {

    public String p13;                // injected via field and un-annotated
    
    public String p14;                // injected via setter and un-annotated
    
    public boolean p14SetterIsCalled = false;
    
    public String getName() {
        return "AService";
    }
    
    public AnotherAServiceImpl() {
        super();
    }
 
    public void setP14(String p14) {
        p14SetterIsCalled = true;
        this.p14 = p14;
    }
    
    public String getP1() {
        return null;
    }

    public String getP2() {
        return null;
    }

    public String getP3() {
        return null;
    }

    public String getP4() {
        return null;
    }

    public String getP5() {
        return null;
    }

    public String getP6() {
        return null;
    }

    public String getP7AString() {
        return null;
    }

    public int getP7BInt() {
        return -1;
    }
    
    public String getP8AString() {
        return null;
    }

    public int getP8BInt() {
        return -1;
    }
    
    public String getP9AString() {
        return null;
    }

    public int getP9BInt() {
        return -1;
    }
    
    public String getP10AString() {
        return null;
    }

    public int getP10BInt() {
        return -1;
    }

    public String getP11AString() {
        return null;
    }

    public int getP11BInt() {
        return -1;
    }

    public String getP12AString() {
        return null;
    }

    public int getP12BInt() {
        return -1;
    }

    public String getP13() {
        return p13;
    }
    
    public String getP14() {
        return p14;
    }
    
    public boolean getP14SetterIsCalled() {
        return p14SetterIsCalled;
    }
    
    public String getP15() {
        return null;
    }

    public String getP16() {
        return null;
    }

    public String getP17() {
        return null;
    }

    public String getP18() {
        return null;
    }

    public int getP19Size() {
        return -1;
    }

    public String getP20(int i) {
        return null;
    }

    public int getP20Size() {
        return -1;
    }

    public int getP21(int i) {
        return -1;
    }

    public int getP21Size() {
        return -1;
    }
}
