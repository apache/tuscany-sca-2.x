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

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * This client program shows how to create an SCA runtime, start it, locate the Eager Init service and invoke it.
 */
public final class HelloWorldClient {
    private HelloWorldClient() {
    }

    public static void main(String[] args) throws Exception {
        String name = "World";

        CompositeContext compositeContext = CurrentCompositeContext.getContext();

        // Locate the Eager init service
        HelloWorldService helloWorldService =
                compositeContext.locateService(HelloWorldService.class, "HelloWorldServiceComponent");

        // Invoke the HelloWorld service
        String value = helloWorldService.getGreetings(name);

        System.out.println(value);
        System.out.flush();

    }
}
