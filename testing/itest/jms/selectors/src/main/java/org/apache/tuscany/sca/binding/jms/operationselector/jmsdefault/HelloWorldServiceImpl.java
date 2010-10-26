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
package org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault;

public class HelloWorldServiceImpl implements HelloWorldService {

    public String getGreetingsOne(String name){
        String response =  "Hello One " + name;
        System.out.println("getGreetingsOne: " + response);
        return response;
    }
 
    public String getGreetingsTwo(String name){
        String response =  "Hello Two " + name;
        System.out.println("getGreetingsTwo: " + response);
        return response;
    }
    
    public String getGreetingsThree(String name){
        String response =  "Hello Three " + name;
        System.out.println("getGreetingsThree: " + response);
        return response;
    }
    
    public String getGreetingsFour(String name){
        String response =  "Hello Four " + name;
        System.out.println("getGreetingsFour: " + response);
        return response;
    }
}

