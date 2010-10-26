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
package org.apache.tuscany.sca.node2;

import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.Test;

public class NodeXMLTestCase {

    @Test
    public void testHelloworldXML() throws ContributionReadException, ActivationException, ValidationException {
        Node node = NodeFactory.newInstance().createNodeFromXML("src/test/resources/helloworldNode.xml");
        Assert.assertEquals("helloworld", node.getDomainName());
        List<String> cs = node.getInstalledContributions();
        Assert.assertEquals(1, cs.size());
        Assert.assertEquals("sample-helloworld", cs.get(0));
        List<String> compsoites = node.getDeployedComposites("sample-helloworld");
        Assert.assertEquals(1, compsoites.size());
        Assert.assertEquals("helloworld.composite", compsoites.get(0));
    }

}
