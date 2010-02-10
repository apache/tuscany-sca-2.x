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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.client.SCAClientFactory;

public class SCAClient {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // do nothing
    }

    @Test
    public void testSCAClient() throws Exception {
        // The configuration required when running with sca-client-rmi and endpoint-hazelcast-rmi
        // Helloworld service = SCAClientFactory.newInstance(URI.create("tuscany:default?remotes=192.168.247.1:14820")).getService(Helloworld.class, "HelloworldService");
        
        // The configuration required when running with sca-client-impl and endpoint-hazelcast
        Helloworld service = SCAClientFactory.newInstance(URI.create("tuscany:default")).getService(Helloworld.class, "HelloworldService");
        
        String response = service.sayHello("test");
        if (response == null || !response.equals("Hello test")){
            throw new Exception("Test failed - expecting 'Hello test' got " + response);
        } else {
            System.out.println(response);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
         // do nothing
    }
    
    public static void main(String[] args) throws Exception {
        SCAClient client = new SCAClient();
        client.testSCAClient();
    }
}
