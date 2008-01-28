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
package helloworld;

import greetings.GreetingsService;

import org.osoa.sca.annotations.Reference;

/**
 * The HelloWorld service implementation
 */
public class HelloWorldServiceImpl implements HelloWorldService {
   
    protected GreetingsService greetingsService;

    public GreetingsService getGreetingsService() {
        System.out.println("Got Injected greetingsService");
        return greetingsService;
    }

    @Reference
    public void setGreetingsService(GreetingsService greetingsService) {
        System.out.println("Injected greetingsService");
        this.greetingsService = greetingsService;
    }
    
    public String hello(String name) {
        System.out.println("Calling greeting services getGreetings");
        return greetingsService.getGreetings(name);
    }
}