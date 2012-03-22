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
package org.apache.tuscany.sca.binding.rest;

import java.util.Arrays;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import services.echo.jaxrs.Echo;

public class RESTReferenceTestCase {
    private static final String ECHO_COMPONENT_WITH_REFERENCE = "EchoClientComponent/Echo";
    private static Node node;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(RESTReferenceTestCase.class);
            node =
                NodeFactory.newInstance().createNode("rest-reference.composite",
                                                     new Contribution("testClient", contribution));
            node.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }

    @Test
    public void testInvokeReference() throws Exception {
        Echo echoComponent = node.getService(Echo.class, ECHO_COMPONENT_WITH_REFERENCE);
        String result = echoComponent.echo("ABC");
        Assert.assertEquals("echo: ABC", result);
    }

}
