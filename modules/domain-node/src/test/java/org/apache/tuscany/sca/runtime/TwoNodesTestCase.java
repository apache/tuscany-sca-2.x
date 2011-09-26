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

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

import sample.Helloworld;

@Ignore("TUSCANY-3953")
public class TwoNodesTestCase {

    @Test
    public void testInstallDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        TuscanyRuntime runtime = TuscanyRuntime.newInstance();
        try {
        Node node1 = runtime.createNode("uri:TwoNodesTestCase?multicast=off&bind=127.0.0.1:44331");
        node1.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null);
        node1.startComposite("helloworld", "helloworld.composite");

        Node node2 = TuscanyRuntime.newInstance().createNode("uri:TwoNodesTestCase?multicast=off&bind=127.0.0.1:44332&wka=127.0.0.1:44331");

        Helloworld helloworldService = node2.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    } finally { runtime.stop(); }
    }

}
