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
package org.apache.tuscany.sca.binding.jms.format.jmsmessage.helloworld;

import javax.jms.TextMessage;

public class HelloWorldServiceImpl implements HelloWorldService {
    
    private static String greetings = "not set";
    
    public void onMessage(javax.jms.Message message){
         
        String name = null;
        
        try {
            name = ((TextMessage)message).getText();
        } catch (Exception ex) {
            name = "EXCEPTION";
        }
        greetings =  "Hello " + name;
    }
    
    public static String getGreetings(){
        return greetings;
    }
    
    // javax.jms.BytesMessage
    // javax.jms.MapMessage
    // javax.jms.ObjectMessage
    // javax.jms.StreamMessage
    // javax.jms.TextMessage
    

}

