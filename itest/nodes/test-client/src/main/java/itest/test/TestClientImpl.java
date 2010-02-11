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

package itest.test;

import itest.iface.NonSimpleType;
import itest.iface.SomeException;
import itest.iface.TestService;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
@EagerInit
public class TestClientImpl implements TestService {

    @Reference
    protected TestService service;

    public TestClientImpl() {
    }
    
    public String sayHello(String name) {
        return service.sayHello(name);
    }

    public NonSimpleType testCT(NonSimpleType name) throws SomeException {
        System.out.println(name.getClass().getClassLoader());
        return service.testCT(name);
    }
    
    @Init
    public void init() {
        try {
            System.out.println(sayHello("petra"));            
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(testCT(new NonSimpleType("beate")).getName());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(testCT(new NonSimpleType("bang")).getName());            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
