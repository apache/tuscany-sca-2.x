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

package org.apache.tuscany.sca.itest.databindings.jaxb.topdown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.itest.databindings.jaxb.HelloServiceClient;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.Contribution;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 * @version $Rev$ $Date$
 */
public class DatabindingTestCase {

    private static Client client;
    private static Node node;

    /**
     * Runs once before running the tests
     */
    @BeforeClass
    public static void setUp() throws Exception {
        try {
        NodeFactory factory = NodeFactory.newInstance();
        node = factory.createNode(new File("src/main/resources/wsdl/wrapped/helloservice.composite").toURI().toURL().toString(),
                new Contribution("TestContribution", new File("src/main/resources/wsdl/wrapped/").toURI().toURL().toString()));
        node.start();
        client = (Client)node;
        }catch(Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs once after running the tests
     */
    @AfterClass
    public static void tearDown() {
        node.stop();
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetings.
     */
    @Test
    public void testW2W() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTest(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsArray.
     */
    @Test
    public void testW2WArray() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTestArray(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsList.
     */
    @Test
    public void testW2WList() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTestList(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsArrayList.
     */
    @Test
    public void testW2WArrayList() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTestArrayList(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsMap.
     */
    @Test
    public void testW2WMap() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTestMap(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsHashMap.
     */
    @Test
    public void testW2WHashMap() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTestHashMap(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsVarArgs.
     */
    @Test
    public void testW2WVarArgs() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2WComponent");
        performTestVarArgs(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetings.
     */
    @Test
    public void testJ2W() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTest(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsArray.
     */
    @Test
    public void testJ2WArray() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTestArray(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsList.
     */
    @Test
    public void testJ2WList() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTestList(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsArrayList.
     */
    @Test
    public void testJ2WArrayList() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTestArrayList(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsMap.
     */
    @Test
    public void testJ2WMap() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTestMap(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsHashMap.
     */
    @Test
    public void testJ2WHashMap() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTestHashMap(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsVarArgs.
     */
    @Test
    public void testJ2WVarArgs() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientJ2WComponent");
        performTestVarArgs(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetings.
     */
    @Test
    public void testW2J() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTest(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsArray.
     */
    @Test
    public void testW2JArray() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTestArray(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsList.
     */
    @Test
    public void testW2JList() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTestList(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsArrayList.
     */
    @Test
    public void testW2JArrayList() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTestArrayList(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsMap.
     */
    @Test
    public void testW2JMap() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTestMap(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsHashMap.
     */
    @Test
    public void testW2JHashMap() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTestHashMap(helloServiceClient);
    }

    /**
     * Invokes the HelloService service using WS binding.
     * Service method invoked is getGreetingsVarArgs.
     */
    @Test
    public void testW2JVarArgs() throws Exception {
        HelloServiceClient helloServiceClient = client.getService(HelloServiceClient.class, "HelloServiceClientW2JComponent");
        performTestVarArgs(helloServiceClient);
    }

    private void performTest(HelloServiceClient helloServiceClient) {
        String name = "Pandu";
        String resp = helloServiceClient.getGreetingsForward(name);
        Assert.assertEquals("Hello "+name, resp);
    }

    private void performTestArray(HelloServiceClient helloServiceClient) {
        String[] names = {"Me", "Pandu"};
        String[] resps = helloServiceClient.getGreetingsArrayForward(names);
        for(int i = 0; i < names.length; ++i) {
            Assert.assertEquals("Hello "+names[i], resps[i]);
        }
    }

    private void performTestList(HelloServiceClient helloServiceClient) {
        List<String> namesList = new ArrayList<String>();
        namesList.add("Me");
        namesList.add("Pandu");
        namesList.add("Chinnipandu");
        List<String> respList = helloServiceClient.getGreetingsListForward(namesList);
        Assert.assertEquals(namesList.size(), respList.size());
        for(int i = 0; i < namesList.size(); ++i) {
            Assert.assertEquals("Hello "+namesList.get(i), respList.get(i));
        }
    }

    private void performTestArrayList(HelloServiceClient helloServiceClient) {
        ArrayList<String> namesList = new ArrayList<String>();
        namesList.add("Me");
        namesList.add("Pandu");
        namesList.add("Chinnipandu");
        ArrayList<String> respList = helloServiceClient.getGreetingsArrayListForward(namesList);
        Assert.assertEquals(namesList.size(), respList.size());
        for(int i = 0; i < namesList.size(); ++i) {
            Assert.assertEquals("Hello "+namesList.get(i), respList.get(i));
        }
    }

    private void performTestMap(HelloServiceClient helloServiceClient) {
        Map<String, String> namesMap = new HashMap<String, String>();
        namesMap.put("Me", null);
        namesMap.put("Pandu", null);
        namesMap.put("Chinnipandu", null);
        Map<String, String> respMap = helloServiceClient.getGreetingsMapForward(namesMap);
        Assert.assertEquals(namesMap.keySet().size(), respMap.keySet().size());
        for(Map.Entry<String, String> entry: namesMap.entrySet()) {
            Assert.assertEquals("Hello "+entry.getKey(), respMap.get(entry.getKey()));
        }
    }

    private void performTestHashMap(HelloServiceClient helloServiceClient) {
        HashMap<String, String> namesMap = new HashMap<String, String>();
        namesMap.put("Me", null);
        namesMap.put("Pandu", null);
        namesMap.put("Chinnipandu", null);
        Map<String, String> respMap = helloServiceClient.getGreetingsHashMapForward(namesMap);
        Assert.assertEquals(namesMap.keySet().size(), respMap.keySet().size());
        for(Map.Entry<String, String> entry: namesMap.entrySet()) {
            Assert.assertEquals("Hello "+entry.getKey(), respMap.get(entry.getKey()));
        }
    }

    private void performTestVarArgs(HelloServiceClient helloServiceClient) {
        String[] names = { "Me", "You", "Pandu" }; // Do not change the array size from 3.
        String expected = "Hello Me You Pandu";
        String actual = helloServiceClient.getGreetingsVarArgsForward(names[0], names[1], names[2]);
        Assert.assertEquals(expected, actual);
    }
}
