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
package org.apache.tuscany.sca.runtime;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Test;

public class NodeXMLTestCase {

    @Test
    public void testHelloworldXML() throws ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNodeFromXML("src/test/resources/helloworldNode.xml");
        Assert.assertEquals("helloworld", node.getDomainName());
        List<String> cs = node.getInstalledContributionURIs();
        Assert.assertEquals(1, cs.size());
        Assert.assertEquals("sample-helloworld", cs.get(0));
        Map<String, List<String>> startedComposites = node.getStartedCompositeURIs();
        Assert.assertEquals(1, startedComposites.size());
        Assert.assertEquals("helloworld.composite", startedComposites.get("sample-helloworld").get(0));
    }

}
