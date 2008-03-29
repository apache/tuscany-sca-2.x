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

package org.apache.tuscany.sca.vtest.javaapi.impl;

import org.apache.tuscany.sca.vtest.javaapi.AService;
import org.apache.tuscany.sca.vtest.javaapi.BService;
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

    public boolean isB7SetterCalled() {
        return b7SetterCalled;
    }
}
