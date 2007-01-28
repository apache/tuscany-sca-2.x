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
 * This client program shows how to invoke a web service asynchronously with a callback
 */

public class HelloWorldClient {

    public final static void main(String[] args) throws Exception {

        CompositeContext compositeContext = CurrentCompositeContext.getContext();
        HelloWorldLocal helloWorldLocal =
            compositeContext.locateService(HelloWorldLocal.class, "HelloWorldServiceComponent");
        helloWorldLocal.getGreetings("John");

        // Sleep for 5 seconds to wait the callback to happen
        Thread.sleep(5000);
    }
}
