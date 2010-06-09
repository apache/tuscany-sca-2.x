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
package org.apache.tuscany.sca.something;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class DeployerTestCase {

    @Test
    public void testInstalledContribution() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, MalformedURLException {
        Section10 section10 = Section10Factory.createSection10();
        
        Deployer deployer = section10.getDeployer();
        Monitor monitor = deployer.createMonitor();
        Contribution contribution = deployer.loadContribution(URI.create("foo"), new File("src/test/resources/sample-helloworld-nodeployable.jar").toURI().toURL(), monitor);
        monitor.analyzeProblems();
        
        section10.installContribution(contribution, null, true);
        List<String> ics = section10.getInstalledContributions();
        Assert.assertEquals(1, ics.size());
        Assert.assertEquals("foo", ics.get(0));
    }

    @Test
    public void testAddDeploymentComposite() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, MalformedURLException, XMLStreamException {
        Section10 section10 = Section10Factory.createSection10();
        
        section10.installContribution("foo", "src/test/resources/sample-helloworld-nodeployable.jar", null, null, true);

        Deployer deployer = section10.getDeployer();
        Monitor monitor = deployer.createMonitor();
        Composite composite = deployer.loadXMLDocument(new File("src/test/resources/helloworld2.composite").toURI().toURL(), monitor);
        monitor.analyzeProblems();
        composite.setURI("helloworld2.composite");
        section10.addDeploymentComposite("foo", composite);
        List<String> dcs = section10.getDeployedCompostes("foo");
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("foo/helloworld2.composite", dcs.get(0));
    }

}
