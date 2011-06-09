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
package test.scaclient;

import itest.HelloworldService;

import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class StopStartTestCase {

    @Test
    public void startStopInstall() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        TuscanyRuntime runtime = TuscanyRuntime.newInstance();
        Node node1 = runtime.createNode("uri:StartStopTestCase?wka=127.0.0.1:9876");
        Assert.assertEquals("Hello Amelia", node1.getService(HelloworldService.class, "HelloworldComponent").sayHello("Amelia"));
        
        String curi = node1.getInstalledContributionURIs().get(0);
        
        node1.stopComposite(curi, "Helloworld.composite");
        try {
            node1.getService(HelloworldService.class, "HelloworldComponent").sayHello("Amelia");
            Assert.fail();
        } catch (NoSuchServiceException e) {
            // expected
        }

        List<String> nodes = node1.getNodeNames();
        nodes.remove(node1.getLocalNodeName());
        String remoteNode = nodes.get(0);
        node1.startComposite(curi, "Helloworld.composite", remoteNode);

// TUSCANY-3870: this next invoke doesn't work:        
//        Assert.assertEquals("Hello Amelia", node1.getService(HelloworldService.class, "HelloworldComponent").sayHello("Amelia"));
    }
}
