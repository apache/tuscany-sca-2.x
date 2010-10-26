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
public class ComplexStuffTestCase {

    private static Node node;
    
    @BeforeClass
    public static void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode("complex.composite", new String[] {"target/test-classes"});
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
        URL url = new URL("http://localhost:8080/ComplexComponent/ComplexStuff/multiParams?x=1&s=petra&b=true");
        InputStream is = url.openStream();
        Assert.assertEquals("\"1petratrue\"", read(is));
    }

    @Test
    public void testNoArgs() throws Exception {
        URL url = new URL("http://localhost:8080/ComplexComponent/ComplexStuff/noArgs");
        InputStream is = url.openStream();
        Assert.assertEquals("\"noArgs\"", read(is));
    }

    @Test
    public void testEchoBean() throws Exception {
        URL url = new URL("http://localhost:8080/ComplexComponent/ComplexStuff/echoBeanA?x={\"s\":\"petra\",\"b\":true,\"y\":42,\"x\":1}");
        InputStream is = url.openStream();
        Assert.assertEquals("{\"s\":\"petra\",\"b\":true,\"y\":42,\"x\":1}", read(is));
    }

    @Test
    public void testVoidReturn() throws Exception {
        URL url = new URL("http://localhost:8080/ComplexComponent/ComplexStuff/voidReturn");
        InputStream is = url.openStream();
        Assert.assertEquals("", read(is));
    }

    @Test
    public void testCheckedException() throws Exception {
        URL url = new URL("http://localhost:8080/ComplexComponent/ComplexStuff/checkedException");
        try {
            InputStream is = url.openStream();
            Assert.fail();
        } catch (IOException e) {
            // expected            
            // TODO: what should happen with checked exceptions?
        }
    }

    @Test
    public void testRuntimeException() throws Exception {
        URL url = new URL("http://localhost:8080/ComplexComponent/ComplexStuff/runtimeException");
        try {
            InputStream is = url.openStream();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 500"));
        }
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
