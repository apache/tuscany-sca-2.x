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
package org.apache.tuscany.sca.binding.jms.format.jmsobject.helloworld;

public class HelloWorldServiceImpl implements HelloWorldService {
    
    public Person getPersonGreetings(Person person){
         
    	person.setFirstName("Hello1 " + person.getFirstName());
    	person.setLastName("Hello1 " + person.getLastName());
    	
        return person;
    }
    
    public Person getNullReturnGreetings(Person person){      
        return null;
    }    
    
    public String getArrayGreeting(String[] names) {
        return "Hello3 " + names[0];
    }
    
    public String getMultiArrayGreetings(String[] firstName, String[] lastName) {
        return "Hello4 " + firstName[0] + " " + lastName[0];
    }
    
    public String getMultiGreetings(String firstName, String lastName) {
        return "Hello5 " + firstName + " " + lastName;
    }
    
    public String getObjectGreeting(Object person) {
        return "Hello6 " + ((Person)person).getFirstName() + " " + ((Person)person).getLastName();
    }
    
    public String getObjectArrayGreeting(Object[] person) {
        return "Hello7 " + ((Person)person[0]).getFirstName() + " " + ((Person)person[0]).getLastName();
    }
    
    public void throwChecked(Person person) throws CheckedException {
        throw new CheckedException("foo");
    }

    public void throwUnChecked(Person person) {
        throw new RuntimeException("bla");
    }    
}

