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

package org.apache.tuscany.sca.binding.ws.jaxws.external.service;

import javax.xml.ws.Endpoint;

public class HelloWorldServiceLauncher {
    
    public Endpoint endpoint;
    
    public HelloWorldServiceLauncher(){
        
    }
    
    public void createService(){
        System.out.println(">>> Starting external JAXWS service at http://localhost:8086/External/HelloWorld");
        
        // default JVM JAXWS support. 
        endpoint= Endpoint.publish("http://localhost:8086/External/HelloWorld",
                                   new HelloWorldService());
    }
    
    public void destoryService(){
        System.out.println(">>> Stopping external JAXWS service at http://localhost:8086/External/HelloWorld");
        endpoint.stop();
    }
    
    public static void main(String[] args) throws Exception {
        HelloWorldServiceLauncher launcher = new HelloWorldServiceLauncher();
        launcher.createService();
    }    
}
