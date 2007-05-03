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
package simplecallback;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * Demonstrates resolving the client service and initiating the callback sequence
 */
public class SimpleCallbackClient {

    public static void main(String[] args) throws Exception {
    	SCARuntime.start("simplecallback.composite");
    	
        // Locate the MyClient component and invoke it
        ComponentContext context = SCARuntime.getComponentContext("MyClientComponent");
        ServiceReference<MyClient> service = context.createSelfReference(MyClient.class);
        MyClient myClient = service.getService();
        
        System.out.println("Main thread " + Thread.currentThread());
        myClient.aClientMethod();
        Thread.sleep(500);
        
        SCARuntime.stop();
    }
}
