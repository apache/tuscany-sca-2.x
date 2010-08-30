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
package org.apache.tuscany.sca.binding.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * HTTP binding unit tests for Helloworld service.
 */
public class HelloworldTestCase {

    private static Node node;
    
    @BeforeClass
    public static void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode("helloworld.composite", new String[] {"target/test-classes"});
        node.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

    @Test
    public void testGet() throws Exception {
        URL url = new URL("http://localhost:8080/HelloworldComponent/Helloworld/sayHello?name=Petra");
        InputStream is = url.openStream();
        Assert.assertEquals("\"Hello Petra\"", read(is));
    }

    @Test
    public void testGetArg0() throws Exception {
        URL url = new URL("http://localhost:8080/HelloworldComponent/Helloworld/sayHello?arg0=Petra");
        InputStream is = url.openStream();
        Assert.assertEquals("\"Hello Petra\"", read(is));
    }

    @Test
    public void testXml() throws Exception {
        URL url = new URL("http://localhost:8080/HelloworldXmlComponent/Helloworld/sayHello?arg0=Petra");
        InputStream is = url.openStream();
        Assert.assertTrue(read(is).endsWith(">Hello null</return>"));
    }

    private static String read(InputStream is) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
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

}
