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
package itest.helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class Mapper1TestCase {

    static {
        org.apache.tuscany.sca.http.jetty.JettyServer.portDefault = 8085;
    }
    
    Node node;
    
    @Test
    public void testSCABindingFactory() throws IOException {
        node = NodeFactory.newInstance().createNode("test.composite", new String[]{"target/test-classes"}) ;
        node.start();
        
        // test the service invocations work
        Helloworld helloworld = node.getService(Helloworld.class, "Client1");
        Assert.assertEquals("Hello Petra", helloworld.sayHello("Petra"));

        helloworld = node.getService(Helloworld.class, "Client2");
        Assert.assertEquals("Hello Petra", helloworld.sayHello("Petra"));
        
        // verify service1 is exposed as a WS endpoint and service2 is a jsonp endpoint
        URL wsService = new URL("http://localhost:8085/Service1/Helloworld?wsdl");
        Assert.assertTrue(getContent(wsService).contains("definitions name=\"HelloworldService\""));

        URL jsonpService = new URL("http://localhost:8085/Client2/Helloworld/sayHello?name=Petra");
        Assert.assertEquals("\"Hello Petra\"", getContent(jsonpService));
    }

    private String getContent(URL url) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @After
    public void shutdown() {
        node.stop();
    }

}
