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
package calculator;

import javax.ws.rs.core.MediaType;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * This shows how to test the Calculator composition.
 */
public class CalculatorTestCase {
    private final static String SERVICE_URL = "http://localhost:8085/calculator/";

    private static Node node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("Calculator.composite");
        node = NodeFactory.newInstance().createNode("Calculator.composite", new Contribution("calculator", location));
        System.out.println("SCA Node API ClassLoader: " + node.getClass().getClassLoader());
        node.start();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

    @Test
    public void testAdd() throws Exception {
        String queryString = "/add/3/2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", MediaType.TEXT_PLAIN);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("5.0", response.getText());
    }

    @Test
    public void testSubtract() throws Exception {
        String queryString = "/subtract/3/2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", MediaType.TEXT_PLAIN);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("1.0", response.getText());
    }

    @Test
    public void testMultiply() throws Exception {
        String queryString = "/multiply/3/2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", MediaType.TEXT_PLAIN);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("6.0", response.getText());
    }

    @Test
    public void testDivide() throws Exception {
        String queryString = "/divide/3/2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", MediaType.TEXT_PLAIN);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("1.5", response.getText());
    }

    @Test
    public void testFormula() throws Exception {
        String queryString = "/calculate/3+2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", MediaType.TEXT_PLAIN);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("5.0", response.getText());
    }

}
