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

public class HelloWorldService implements HelloWorld {

    public String getGreetings(String s) {
        String response = "Hello " + s;
        System.out.println("At service: " + response);
        return response;
    }
    
    public Foo getGreetingsComplex(Foo foo){
        Foo response = foo;
        Bar b3 = new Bar();
        b3.setS("simon");
        b3.setX(4);
        b3.setY(new Integer(5));
        b3.setB(Boolean.TRUE);
        response.getBars()[1] = b3;
        System.out.println("At sevice: " + response.getBars()[0].getS());
        return response;
    }    

}
