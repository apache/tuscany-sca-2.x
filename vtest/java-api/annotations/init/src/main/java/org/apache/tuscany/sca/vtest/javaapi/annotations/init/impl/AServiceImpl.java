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
package org.apache.tuscany.sca.vtest.javaapi.annotations.init.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.init.AService;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

/**
 * This class implements AService.
 */
@Service(AService.class)
public class AServiceImpl implements AService {

    private boolean initProper;
    
    @Property
    public String someProperty;
    
    @Reference
    public AService someReference;

    @Init
    public void init() {
        initProper = someProperty != null && someReference != null;
    }
    
    public String getGreetings(String name) {
        return "Hello " + name;
    }
    
    public boolean isInitProper() {
        return initProper;
    }
}
