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
package org.apache.tuscany.sca.impl;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Assert;
import org.junit.Test;

public class DeployerTestCase {

    @Test
    public void testArtifacts() throws ContributionReadException, ValidationException {

        Node node = TuscanyRuntime.newInstance().createNode();
        String curi = node.installContribution("src/test/resources/sample-helloworld.jar");
        node.validateContribution(curi);

        Contribution contribution = node.getContribution(curi);
        Composite composite = contribution.getArtifactModel("helloworld.composite");
        Component component = composite.getComponents().get(0);
        Service service = component.getImplementation().getServices().get(0);

        Assert.assertEquals("helloworld", composite.getName().getLocalPart());
        Assert.assertEquals("HelloworldComponent", component.getName());
        Assert.assertEquals("Helloworld", service.getName());
    }

}
