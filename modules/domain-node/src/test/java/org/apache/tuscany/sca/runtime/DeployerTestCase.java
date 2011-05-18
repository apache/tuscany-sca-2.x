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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class DeployerTestCase {

    @Test
    public void testInstalledContribution() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, MalformedURLException {
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();
        Node node = tuscanyRuntime.createNode("myDomain");
        
        Deployer deployer = tuscanyRuntime.getDeployer();
        Monitor monitor = deployer.createMonitor();
        Contribution contribution = deployer.loadContribution(URI.create("foo"), new File("src/test/resources/sample-helloworld-nodeployable.jar").toURI().toURL(), monitor);
        monitor.analyzeProblems();
        
        node.installContribution(contribution, null);
        List<String> ics = node.getInstalledContributionURIs();
        Assert.assertEquals(1, ics.size());
        Assert.assertEquals("foo", ics.get(0));
    }

    @Test
    public void testAddDeploymentComposite() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, MalformedURLException, XMLStreamException {
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();
        Node node = tuscanyRuntime.createNode("myDomain");
        
        node.installContribution("foo", "src/test/resources/sample-helloworld-nodeployable.jar", null, null);

        Deployer deployer = tuscanyRuntime.getDeployer();
        Monitor monitor = deployer.createMonitor();
        Composite composite = deployer.loadXMLDocument(new File("src/test/resources/helloworld2.composite").toURI().toURL(), monitor);
        monitor.analyzeProblems();
        String cmpuri = node.addDeploymentComposite("foo", composite);
        node.startComposite("foo", cmpuri);
        Map<String, List<String>> dcs = node.getStartedCompositeURIs();
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld2.composite", dcs.get("foo").get(0));
    }

    @Test
    public void testAddDeploymentCompositeXML() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, MalformedURLException, XMLStreamException, FileNotFoundException {
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();
        Node node = tuscanyRuntime.createNode("myDomain");
        
        node.installContribution("foo", "src/test/resources/sample-helloworld-nodeployable.jar", null, null);

        String cmpuri = node.addDeploymentComposite("foo", new FileReader(new File("src/test/resources/helloworld2.composite")));
        node.startComposite("foo", cmpuri);
        Map<String, List<String>> dcs = node.getStartedCompositeURIs();
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld2.composite", dcs.get("foo").get(0));
    }

}
