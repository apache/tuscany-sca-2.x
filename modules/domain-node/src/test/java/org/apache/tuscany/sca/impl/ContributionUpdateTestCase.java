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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.Assert;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class ContributionUpdateTestCase {

    @Test
    public void updateTest1() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, XMLStreamException, FileNotFoundException {
        Node node = TuscanyRuntime.newInstance().createNode("updateTest1");
        String curi = node.installContribution("src/test/resources/sample-helloworld.jar");
        node.startDeployables(curi);
        
        Collection<Endpoint> eps = ((NodeImpl)node).getEndpointRegistry().getEndpoints();
        Assert.assertEquals(1, eps.size());
        Assert.assertEquals("HelloworldComponent#service-binding(Helloworld/Helloworld)", eps.iterator().next().getURI());
        
        ((NodeImpl)node).updateContribution(curi, "src/test/resources/sample-helloworld2.jar", null, null);
        
        eps = ((NodeImpl)node).getEndpointRegistry().getEndpoints();
        Assert.assertEquals(1, eps.size());
        Assert.assertEquals("Helloworld2Component#service-binding(Helloworld/Helloworld)", eps.iterator().next().getURI());
    }

    @Test
    public void updateWithAdditionalDeployablesTest() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, XMLStreamException, FileNotFoundException {
        Node node = TuscanyRuntime.newInstance().createNode("updateWithAdditionalDeployablesTest");
        String curi = node.installContribution("src/test/resources/sample-helloworld.jar");
        String compURI = node.addDeploymentComposite(curi, new FileReader("src/test/resources/helloworld2.composite"));
        node.startComposite(curi, compURI);
        
        Collection<Endpoint> eps = ((NodeImpl)node).getEndpointRegistry().getEndpoints();
        Assert.assertEquals(1, eps.size());
        Assert.assertEquals("Helloworld2Component#service-binding(Helloworld/Helloworld)", eps.iterator().next().getURI());
        
        ((NodeImpl)node).updateContribution(curi, "src/test/resources/sample-helloworld.jar", null, null);
        
        eps = ((NodeImpl)node).getEndpointRegistry().getEndpoints();
        Assert.assertEquals(1, eps.size());
        Assert.assertEquals("Helloworld2Component#service-binding(Helloworld/Helloworld)", eps.iterator().next().getURI());
    }

}
