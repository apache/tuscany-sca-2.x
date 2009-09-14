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

import org.osoa.sca.annotations.Reference;


public class HelloWorldReferenceImpl implements HelloWorldReference {
    
    @Reference
    protected HelloWorldService helloWorldServiceWrapSingle;
    
    @Reference
    protected HelloWorldService helloWorldServiceDontWrapSingle;
    
    public String getGreetingsWrapSingle(String firstName, String lastName){ 
    	Person person = new Person();
    	person.setFirstName(firstName);
    	person.setLastName(lastName);
    	
    	String returnString = "";
    	
        Person returnPerson =  helloWorldServiceWrapSingle.getPersonGreetings(person);
        returnString = returnPerson.getFirstName() + " " + returnPerson.getLastName();
        
        Person returnNullPerson = helloWorldServiceWrapSingle.getNullReturnGreetings(person); 
        
        if (returnNullPerson == null){
            returnString += " Hello2 null";
        }
        
        String returnGreeting = helloWorldServiceWrapSingle.getArrayGreeting(new String[]{firstName, lastName});
        returnString += " " + returnGreeting;
        
        returnGreeting = helloWorldServiceWrapSingle.getMultiArrayGreetings(new String[]{firstName, firstName},new String[]{lastName, lastName});
        returnString += " " + returnGreeting;
        
        returnGreeting = helloWorldServiceWrapSingle.getMultiGreetings(firstName, lastName);
        returnString += " " + returnGreeting;        

        returnGreeting = helloWorldServiceWrapSingle.getObjectGreeting(person);
        returnString += " " + returnGreeting; 
        
        returnGreeting = helloWorldServiceWrapSingle.getObjectArrayGreeting(new Object[]{person});
        returnString += " " + returnGreeting;      
        
        return returnString;
    }  
    
    public String getGreetingsDontWrapSingle(String firstName, String lastName){ 
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        
        String returnString = "";
        
        Person returnPerson =  helloWorldServiceDontWrapSingle.getPersonGreetings(person);
        returnString = returnPerson.getFirstName() + " " + returnPerson.getLastName();
        
        Person returnNullPerson = helloWorldServiceDontWrapSingle.getNullReturnGreetings(person); 
        
        if (returnNullPerson == null){
            returnString += " Hello2 null";
        }
        
        String returnGreeting = helloWorldServiceDontWrapSingle.getArrayGreeting(new String[]{firstName, lastName});
        returnString += " " + returnGreeting;
        
        returnGreeting = helloWorldServiceDontWrapSingle.getMultiArrayGreetings(new String[]{firstName, firstName},new String[]{lastName, lastName});
        returnString += " " + returnGreeting;
        
        returnGreeting = helloWorldServiceDontWrapSingle.getMultiGreetings(firstName, lastName);
        returnString += " " + returnGreeting;        

        returnGreeting = helloWorldServiceDontWrapSingle.getObjectGreeting(person);
        returnString += " " + returnGreeting; 
        
        returnGreeting = helloWorldServiceDontWrapSingle.getObjectArrayGreeting(new Object[]{person});
        returnString += " " + returnGreeting;
        
        try {
            helloWorldServiceDontWrapSingle.throwChecked(person);
        } catch (CheckedException e) {
            returnString += " " + e.getMessage();
        }
        
        try {
            helloWorldServiceDontWrapSingle.throwUnChecked(person);
        } catch (Exception e) {
            returnString += " " + e.getCause().getMessage();
        }           
        
        return returnString;
    }     
}

