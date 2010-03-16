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

package org.apache.tuscany.sca.binding.ws.axis2;

import org.oasisopen.sca.annotation.Reference;

public class HelloWorldClient implements HelloWorld {

    @Reference
    public HelloWorld helloWorldWS;
    
    public String getGreetings(String s) {
        String response = helloWorldWS.getGreetings(s);
        System.out.println("At client: " + response);
        return response;
    }

    public Foo getGreetingsComplex(Foo foo){
        Foo response = helloWorldWS.getGreetingsComplex(foo);
        System.out.println("At client: " + response.getBars()[0].getS());
        return response;
    }
}
