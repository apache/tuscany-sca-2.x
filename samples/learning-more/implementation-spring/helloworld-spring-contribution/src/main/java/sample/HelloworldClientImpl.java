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
package sample;

import org.oasisopen.sca.annotation.Reference;

public class HelloworldClientImpl implements Helloworld {
    private Helloworld helloworld;

    // SCA reference
    @Reference(required = false)
    private DateService dateService;

    public HelloworldClientImpl() {
        System.out.println("HelloworldClientImpl()");
    }

    public String sayHello(String name) {
        System.out.println("HelloworldClientImpl.sayHello(" + name + ")");
        if (dateService == null) {
            return "Hello " + name;
        }
        return "[" + dateService.getDate() + "] " + helloworld.sayHello(name);
    }

    // Setter for spring injection
    public void setHelloworld(Helloworld helloworld) {
        System.out.println("Injected with " + helloworld);
        this.helloworld = helloworld;
    }

}
