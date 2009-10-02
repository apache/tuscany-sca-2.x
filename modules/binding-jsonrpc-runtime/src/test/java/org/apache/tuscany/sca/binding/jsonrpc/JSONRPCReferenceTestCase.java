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
package org.apache.tuscany.sca.binding.jsonrpc;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import echo.Echo;

public class JSONRPCReferenceTestCase {
    private static final String SERVICE_PATH = "/EchoService";
    private static final String SERVICE_URL = "http://localhost:8085/SCADomain" + SERVICE_PATH;

    private static Node nodeServer;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(JSONRPCReferenceTestCase.class);
            nodeServer = NodeFactory.newInstance().createNode("JSONRPCBinding.composite", new Contribution("testServer", contribution));
            nodeServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        nodeServer.stop();
        nodeServer.destroy();
    }
    
    @Test
    public void testInvokeReference() throws Exception {
        Node node = null;

        String contribution = ContributionLocationHelper.getContributionLocation(JSONRPCReferenceTestCase.class);
        node = NodeFactory.newInstance().createNode("JSONRPCReference.composite", new Contribution("testClient", contribution));
        node.start();

        Echo echoComponent = node.getService(Echo.class,"EchoComponentWithReference");
        String result = echoComponent.echo("ABC");
        Assert.assertEquals("echo: ABC", result);
        if (node != null) {
            node.stop();
            node.destroy();
        }
    }
}
