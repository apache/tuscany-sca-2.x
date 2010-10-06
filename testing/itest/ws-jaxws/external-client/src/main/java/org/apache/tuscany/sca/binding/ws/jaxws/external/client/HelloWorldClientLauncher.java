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

package org.apache.tuscany.sca.binding.ws.jaxws.external.client;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.tuscany.sca.binding.ws.jaxws.sca.Exception_Exception;
import org.apache.tuscany.sca.binding.ws.jaxws.sca.HelloWorldImpl;

public class HelloWorldClientLauncher {
    
    public HelloWorldImpl wsProxy;
    
    public HelloWorldClientLauncher(){
    }
    
    public void createClient() throws Exception{
        System.out.println(">>> Starting external JAXWS client ");
        
        // default JVM JAXWS support
        QName serviceName = new QName("http://jaxws.ws.binding.sca.tuscany.apache.org/", "HelloWorldImplService");
        QName portName = new QName("http://jaxws.ws.binding.sca.tuscany.apache.org/", "HelloWorldImplPort");
        //URL wsdlLocation = new File("../external-client/target/classes/helloworld-sca.wsdl").toURL();
        URL wsdlLocation = new File("../external-client/target/classes/HelloWorldImplService.wsdl").toURL();
        javax.xml.ws.Service webService = Service.create(wsdlLocation, serviceName);
        wsProxy = (HelloWorldImpl) webService.getPort(portName, HelloWorldImpl.class);
    }
    
    public void destroyClient(){
        System.out.println(">>> Stopping external JAXWS client: ");
        // TODO 
    }
    
    public String getGreetings(String name){
        System.out.println("Entering External Client HelloWorld.getGreetings: " + name);
        String response = wsProxy.getGreetings(name);
        System.out.println("Leaving External Client HelloWorld.getGreetings: " + response);
        return response;
    }
    
    public String getGreetingsException(String name) throws Exception_Exception {
        return wsProxy.getGreetingsException(name);
    }    
    
    public static void main(String[] args) throws Exception {
        HelloWorldClientLauncher launcher = new HelloWorldClientLauncher();
        launcher.createClient();
        launcher.getGreetings("Fred");
        launcher.destroyClient();
    }
    
}
