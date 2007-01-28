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

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import commonj.sdo.DataObject;

/**
 * This class implements the HelloWorld service.
 */
@Service(HelloWorldService.class)
@Scope("COMPOSITE")
public class HelloWorldImpl implements HelloWorldService {

    private HelloWorldCallback helloWorldCallback;

    @Callback
    public void setHelloWorldCallback(HelloWorldCallback helloWorldCallback) {
        this.helloWorldCallback = helloWorldCallback;
    }

    public String getGreetings(String name) {
        try {
            helloWorldCallback.getGreetingsCallback("Hola " + name);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "This should not be seen";
    }

    public String getGreetings1(DataObject name) {
        String firstName = name.getString("firstName");
        String lastName = name.getString("lastName");
        return "Hi " + firstName + " " + lastName;
    }

    public void getGreetingsWithCallback(String name) {
        try {
            helloWorldCallback.getGreetingsCallback("Alo " + name);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
