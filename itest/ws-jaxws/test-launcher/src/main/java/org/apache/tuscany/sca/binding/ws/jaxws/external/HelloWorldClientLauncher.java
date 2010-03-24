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

package org.apache.tuscany.sca.binding.ws.jaxws.external;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.tuscany.sca.binding.ws.jaxws.provided.HelloWorld;

public class HelloWorldClientLauncher {
    
    public static void main(String[] args) throws Exception {
        System.out.println("Entering external JAXWS client ");
                
        // default JVM JAXWS support
        QName serviceName = new QName("http://helloworld", "HelloWorldService");
        QName portName = new QName("http://helloworld", "HelloWorldSoapPort");
        URL wsdlLocation = new File("../java-first-contribution/target/classes/helloworld-sca.wsdl").toURL();
        javax.xml.ws.Service webService = Service.create( wsdlLocation, serviceName );
        HelloWorld wsProxy = (HelloWorld) webService.getPort(portName, HelloWorld.class);
        
        System.out.println("Leaving external JAXWS client: " + wsProxy.getGreetings("Fred"));
    }
}
