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

package org.apache.tuscany.sca.itest.databindings.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class DatabindingTestCase {

    private static SCADomain domain;

    /**
     * Runs once before running the tests
     */
    @BeforeClass
    public static void setUp() throws Exception {
        try { 
            domain = SCADomain.newInstance("helloservice.composite");
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs once after running the tests
     */
    @AfterClass
    public static void tearDown() {
        domain.close();
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetings.
     */
    @Test
    public void testSCA() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTest(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsArray.
     */
    @Test
    public void testSCAArray() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTestArray(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsList.
     */
    @Test
    public void testSCAList() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTestList(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsArrayList.
     */
    @Test
    public void testSCAArrayList() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTestArrayList(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsMap.
     */
    @Test
    public void testSCAMap() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTestMap(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsHashMap.
     */
    @Test
    public void testSCAHashMap() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTestHashMap(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsVarArgs.
     */
    @Test
    public void testSCAVarArgs() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientSCAComponent");
        performTestVarArgs(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetings.
     */
    @Test
    public void testWS() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTest(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetingsArray.
     */
    @Test
    public void testWSArray() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTestArray(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetingsList.
     */
    @Test
    public void testWSList() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTestList(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetingsArrayList.
     */
    @Test
    public void testWSArrayList() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTestArrayList(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetingsMap.
     */
    @Test
    public void testWSMap() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTestMap(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetingsHashMap.
     */
    @Test
    public void testWSHashMap() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTestHashMap(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloServiceSimple service using WS binding.
     * Service method invoked is getGreetingsVarArgs.
     */
    @Test
    public void testWSVarArgs() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloServiceSimpleClientWSComponent");
        performTestVarArgs(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetings.
     */
    @Test
    public void testSCALocal() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTest(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsArray.
     */
    @Test
    public void testSCALocalArray() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTestArray(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsList.
     */
    @Test
    public void testSCALocalList() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTestList(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsArrayList.
     */
    @Test
    public void testSCALocalArrayList() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTestArrayList(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsMap.
     */
    @Test
    public void testSCALocalMap() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTestMap(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsHashMap.
     */
    @Test
    public void testSCALocalHashMap() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTestHashMap(helloServiceSimpleClient);
    }

    /**
     * Invokes the HelloLocalServiceSimple service using SCA binding.
     * Service method invoked is getGreetingsVarArgs.
     */
    @Test
    public void testSCALocalVarArgs() throws Exception {
        HelloServiceSimpleClient helloServiceSimpleClient = domain.getService(HelloServiceSimpleClient.class, "HelloLocalServiceSimpleClientSCAComponent");
        performTestVarArgs(helloServiceSimpleClient);
    }

    private void performTest(HelloServiceSimpleClient helloServiceSimpleClient) {
        String name = "Pandu";
        String resp = helloServiceSimpleClient.getGreetingsForward(name);
        Assert.assertEquals("Hello "+name, resp);
    }

    private void performTestArray(HelloServiceSimpleClient helloServiceSimpleClient) {
        String[] names = {"Me", "Pandu"};
        String[] resps = helloServiceSimpleClient.getGreetingsArrayForward(names);
        for(int i = 0; i < names.length; ++i) {
            Assert.assertEquals("Hello "+names[i], resps[i]);
        }
    }

    private void performTestList(HelloServiceSimpleClient helloServiceSimpleClient) {
        List<String> namesList = new ArrayList<String>();
        namesList.add("Me");
        namesList.add("Pandu");
        namesList.add("Chinnipandu");
        List<String> respList = helloServiceSimpleClient.getGreetingsListForward(namesList);
        Assert.assertEquals(namesList.size(), respList.size());
        for(int i = 0; i < namesList.size(); ++i) {
            Assert.assertEquals("Hello "+namesList.get(i), respList.get(i));
        }
    }

    private void performTestArrayList(HelloServiceSimpleClient helloServiceSimpleClient) {
        ArrayList<String> namesList = new ArrayList<String>();
        namesList.add("Me");
        namesList.add("Pandu");
        namesList.add("Chinnipandu");
        ArrayList<String> respList = helloServiceSimpleClient.getGreetingsArrayListForward(namesList);
        Assert.assertEquals(namesList.size(), respList.size());
        for(int i = 0; i < namesList.size(); ++i) {
            Assert.assertEquals("Hello "+namesList.get(i), respList.get(i));
        }
    }

    private void performTestMap(HelloServiceSimpleClient helloServiceSimpleClient) {
        Map<String, String> namesMap = new HashMap<String, String>();
        namesMap.put("Me", null);
        namesMap.put("Pandu", null);
        namesMap.put("Chinnipandu", null);
        Map<String, String> respMap = helloServiceSimpleClient.getGreetingsMapForward(namesMap);
        Assert.assertEquals(namesMap.keySet().size(), respMap.keySet().size());
        for(Map.Entry<String, String> entry: namesMap.entrySet()) {
            Assert.assertEquals("Hello "+entry.getKey(), respMap.get(entry.getKey()));
        }
    }

    private void performTestHashMap(HelloServiceSimpleClient helloServiceSimpleClient) {
        HashMap<String, String> namesMap = new HashMap<String, String>();
        namesMap.put("Me", null);
        namesMap.put("Pandu", null);
        namesMap.put("Chinnipandu", null);
        Map<String, String> respMap = helloServiceSimpleClient.getGreetingsHashMapForward(namesMap);
        Assert.assertEquals(namesMap.keySet().size(), respMap.keySet().size());
        for(Map.Entry<String, String> entry: namesMap.entrySet()) {
            Assert.assertEquals("Hello "+entry.getKey(), respMap.get(entry.getKey()));
        }
    }

    private void performTestVarArgs(HelloServiceSimpleClient helloServiceSimpleClient) {
        String[] names = { "Me", "You", "Pandu" }; // Do not change the array size from 3.
        String expected = "Hello Me You Pandu";
        String actual = helloServiceSimpleClient.getGreetingsVarArgsForward(names[0], names[1], names[2]);
        Assert.assertEquals(expected, actual);
    }
}
