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

package itest;

import itest.nodes.Helloworld;

import java.net.URI;

import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.node.NodeFactory;
import org.oasisopen.sca.client.SCAClientFactory;

public class SCAClient {

    public void testSCAClient() throws Exception {
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", RMIBinding.TYPE.toString());
        // The configuration required when running with sca-client-rmi and endpoint-hazelcast-rmi
        //SCAClientFactory factory = SCAClientFactory.newInstance(URI.create("tuscanyclient:default?remotes=192.168.247.1:14820"));
        
        // The configuration required when running with sca-client-impl and endpoint-hazelcast
        SCAClientFactory factory = SCAClientFactory.newInstance(URI.create("tuscany:default"));
        
        // Sleep 3 seconds so that the endpoint is populated into the EndpointRegistry
        Thread.sleep(3000);
        
        Helloworld service = factory.getService(Helloworld.class, "HelloworldService");
        
        String response = service.sayHello("test");
        if (response == null || !response.equals("Hello test")){
            throw new Exception("Test failed - expecting 'Hello test' got " + response);
        } else {
            System.out.println("Test success - " + response);
        }
        
        //TODO - When using the hazelcast registry (or client) it causes the 
        //       JVM to hang on shutdown as it created non-daemon threads
        //       So destroy the node factory here which should bring down 
        //       the runtime and hence hazelcast. 
        //       There's currently no interface on the client factory
        //       for doing this so we may need to talk to OASIS about adding one
        //       or just rely on the runtime hosting the classes using the SCAClient
        //       when it's on its way down
        NodeFactory.getInstance().destroy();
        System.clearProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding");
    }
   
    public static void main(String[] args) throws Exception {
        SCAClient client = new SCAClient();
        client.testSCAClient();
    }
}
