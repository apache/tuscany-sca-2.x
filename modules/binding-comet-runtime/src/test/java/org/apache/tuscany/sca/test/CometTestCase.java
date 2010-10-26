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

package org.apache.tuscany.sca.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 * Test case for the comet binding.
 */
public class CometTestCase extends TestCase {

    /**
     * Test consisting in starting up a node containing services exposed via the
     * comet binding.
     */
    public void testComet() {
        JettyServer.portDefault = 8085;
        try {
            final String location = ContributionLocationHelper.getContributionLocation("test.composite");
            final Node node = NodeFactory.newInstance().createNode("test.composite", new Contribution("c1", location));
            node.start();
            // System.out.println("Press any key to stop the node.");
            // System.in.read();
            node.stop();
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}
