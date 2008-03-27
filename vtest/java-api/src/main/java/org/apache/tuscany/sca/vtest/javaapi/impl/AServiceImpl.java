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
   
    protected BService b3; // injected via setter

    public AServiceImpl(@Reference(name = "b2") BService b2) {
        super();
        this.b2 = b2;
    }
    
    @Reference
    public void setB3(BService b3) {
            this.b3 = b3;
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

}
