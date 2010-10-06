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

package org.apache.tuscany.sca.binding.ws;

import javax.jws.WebService;

import org.apache.tuscany.sca.binding.ws.jaxws.external.service.iface.Foo;
import org.apache.tuscany.sca.binding.ws.jaxws.external.service.iface.HelloWorldService;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.annotation.Reference;

@WebService
public class HelloWorldImpl implements HelloWorld, HelloWorldCallback {

    @Reference
    public HelloWorldService helloWorldExternal;
    
    @Reference
    public HelloWorldCallbackService helloWorldCallbackService;
    
    // HelloWorld operations
    
    public String getGreetings(String s) {
        System.out.println("Entering SCA HelloWorld.getGreetings: " + s);
        String response = helloWorldCallbackService.getGreetings(s);
        System.out.println("Leaving SCA HelloWorld.getGreetings: " + response);
        return response;
    }
    
    public String getGreetingsException(String s) throws ServiceRuntimeException {
        System.out.println("Entering SCA HelloWorld.getGreetingsException: " + s);
        String response = helloWorldCallbackService.getGreetings(s);
        System.out.println("Leaving SCA HelloWorld.getGreetings: " + response);
        throw new ServiceRuntimeException(response);
    }    

    public Foo getGreetingsComplex(Foo foo){
        System.out.println("Entering SCA HelloWorld.getGreetingsComplex: " + foo.getBars().get(0).getS());
        Foo response = helloWorldCallbackService.getGreetingsComplex(foo);
        System.out.println("Leaving SCA HelloWorld.getGreetingsComplex: " + foo.getBars().get(0).getS());
        return response;
    } 
    
    // HelloWorldCallback operations
    
    public String getGreetingsCallback(String s) {
        System.out.println("Entering SCA HelloWorld.getGreetingsCallback: " + s);
        String response = helloWorldExternal.getGreetings(s);
        System.out.println("Leaving SCA HelloWorld.getGreetingsCallback: " + response);
        return response;
    }
    
    public String getGreetingsExceptionCallback(String s) throws ServiceRuntimeException {
        System.out.println("Entering SCA HelloWorld.getGreetingsExceptionCallback: " + s);
        String response = helloWorldExternal.getGreetings(s);
        System.out.println("Leaving SCA HelloWorld.getGreetingsCallback: " + response);
        throw new ServiceRuntimeException(response);
    }    

    public Foo getGreetingsComplexCallback(Foo foo){
        System.out.println("Entering SCA HelloWorld.getGreetingsComplexCallback: " + foo.getBars().get(0).getS());
        Foo response = helloWorldExternal.getGreetingsComplex(foo);
        System.out.println("Leaving SCA HelloWorld.getGreetingsComplexCallback: " + foo.getBars().get(0).getS());
        return response;
    } 
    
}
