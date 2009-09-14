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
package org.apache.tuscany.sca.binding.jms.format.jmstextxml.helloworld;

import org.osoa.sca.annotations.Reference;

public class HelloWorldReferenceImpl implements HelloWorldReference {
    
    @Reference
    protected HelloWorldService helloWorldService1;
    
    @Reference
    protected HelloWorldService helloWorldService2;
    
    @Reference
    protected HelloWorldService helloWorldService3;

    public String getGreetings(String name){
        String stringValue = helloWorldService1.getGreetings(name) + " " +
                             helloWorldService2.getGreetings(name) + " " +
                             helloWorldService3.getGreetings(name);
        
        return stringValue;
    }
    
    public String getPersonGreetings(Person person){
        String stringValue = helloWorldService1.getPersonGreetings(person) + " " + 
                             helloWorldService2.getPersonGreetings(person) + " " +
                             helloWorldService3.getPersonGreetings(person);
        
        try {
            helloWorldService1.throwChecked(person.getLastName());
        } catch (CheckedException e) {
            stringValue += " " + e.getMessage();
        }
        
        try {
            helloWorldService1.throwUnChecked(person.getLastName());
        } catch (Exception e) {
            stringValue += " " + e.getMessage();
        }
        
        return stringValue;
    }
}

