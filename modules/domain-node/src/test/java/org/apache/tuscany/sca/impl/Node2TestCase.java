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

import java.io.StringReader;
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
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.InstalledContribution;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class Node2TestCase {

    @Test
    public void localInstall() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/import.jar");

        Assert.assertEquals(1, node.getInstalledContributionURIs().size());
        Assert.assertEquals("import", node.getInstalledContributionURIs().get(0));
        Contribution c = node.getContribution("import");
        Assert.assertNotNull(c);
    }

    @Test
    public void remoteInstall() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("https://repository.apache.org/content/groups/snapshots/org/apache/tuscany/sca/samples/helloworld/2.0-SNAPSHOT/helloworld-2.0-SNAPSHOT.jar");

        Assert.assertEquals(1, node.getInstalledContributionURIs().size());
        Assert.assertEquals("helloworld", node.getInstalledContributionURIs().get(0));
        Contribution c = node.getContribution("helloworld");
        Assert.assertNotNull(c);
    }

    @Test
    public void DistributedInstall() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node nodeA = TuscanyRuntime.newInstance().createNode("uri:DistributedInstall");
        nodeA.installContribution("https://repository.apache.org/content/groups/snapshots/org/apache/tuscany/sca/samples/helloworld/2.0-SNAPSHOT/helloworld-2.0-SNAPSHOT.jar");
        nodeA.installContribution("src/test/resources/export.jar");

        Assert.assertEquals(2, nodeA.getInstalledContributionURIs().size());
        Assert.assertTrue(nodeA.getInstalledContributionURIs().contains("export"));
        Assert.assertTrue(nodeA.getInstalledContributionURIs().contains("helloworld"));
        Contribution cA = nodeA.getContribution("helloworld");
        Assert.assertNotNull(cA);
        
        Node nodeB = TuscanyRuntime.newInstance().createNode("uri:DistributedInstall");
        Assert.assertEquals(2, nodeB.getInstalledContributionURIs().size());
        Assert.assertTrue(nodeB.getInstalledContributionURIs().contains("export"));
        Assert.assertTrue(nodeB.getInstalledContributionURIs().contains("helloworld"));
        Contribution cB = nodeB.getContribution("helloworld");
        Assert.assertNotNull(cB);

        InstalledContribution ic = ((NodeImpl)nodeB).getInstalledContribution("export");
        Assert.assertEquals(1, ic.getJavaExports().size());
        Assert.assertEquals("sample", ic.getJavaExports().get(0));
    }
    
    @Test
    public void deployables() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/import.jar");

        Assert.assertEquals(1, node.getInstalledContributionURIs().size());
        Assert.assertEquals("import", node.getInstalledContributionURIs().get(0));
        List<String> ds = node.getDeployableCompositeURIs("import");
        Assert.assertEquals(1, ds.size());
        Assert.assertEquals("helloworld.composite", ds.get(0));

    }

    @Test
    public void exports() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/export.jar");

        Assert.assertEquals(1, node.getInstalledContributionURIs().size());
        Assert.assertEquals("export", node.getInstalledContributionURIs().get(0));
        
        InstalledContribution ic = ((NodeImpl)node).getInstalledContribution("export");
        Assert.assertEquals(1, ic.getJavaExports().size());
        Assert.assertEquals("sample", ic.getJavaExports().get(0));
    }

    @Test
    public void validValidate() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/sample-helloworld.jar");
        node.validateContribution("sample-helloworld");
    }

    @Test
    public void invalidValidate() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/import.jar");
        try {
            node.validateContribution("import");
        } catch (ValidationException e) {
            Assert.assertTrue(e.getMessage().endsWith("Unresolved import: Import = sample"));            
        }
    }

    @Test
    public void importExportValidate() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/import.jar");
        try {
            node.validateContribution("import");
        } catch (ValidationException e) {
            // expected
        }
        node.installContribution("src/test/resources/export.jar");
        node.validateContribution("import");
        node.startComposite("import", "helloworld.composite");
        Map<String, List<String>> scs = node.getStartedCompositeURIs();
        Assert.assertEquals(1, scs.size());            
    }

    @Test
    public void importExportDistributedValidate() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node nodeA = TuscanyRuntime.newInstance().createNode("uri:ImportTestCase");
        nodeA.installContribution("src/test/resources/import.jar");
        try {
            nodeA.validateContribution("import");
        } catch (ValidationException e) {
            // expected
        }
        Node nodeB = TuscanyRuntime.newInstance().createNode("uri:ImportTestCase");
        nodeB.installContribution("src/test/resources/export.jar");
        nodeA.validateContribution("import");
        nodeA.startComposite("import", "helloworld.composite");
        Map<String, List<String>> scs = nodeB.getStartedCompositeURIs();
        Assert.assertEquals(1, scs.size());            
    }

    @Test
    public void startTest() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("ImportTestCase");
        node.installContribution("src/test/resources/sample-helloworld.jar");
        Assert.assertEquals(0, node.getStartedCompositeURIs().size());

        node.startComposite("sample-helloworld", "helloworld.composite");
        Assert.assertEquals(1, node.getStartedCompositeURIs().size());
        Assert.assertEquals("helloworld.composite", node.getStartedCompositeURIs().get("sample-helloworld").get(0));
        
        node.stopComposite("sample-helloworld", "helloworld.composite");
//        Assert.assertEquals(0, node.getStartedComposites().size());
        node.startComposite("sample-helloworld", "helloworld.composite");
        Assert.assertEquals(1, node.getStartedCompositeURIs().size());
        Assert.assertEquals("helloworld.composite", node.getStartedCompositeURIs().get("sample-helloworld").get(0));
        node.stopComposite("sample-helloworld", "helloworld.composite");
    }

    @Test
    public void addDeploymentCompositeTest() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, XMLStreamException {
        Node node = TuscanyRuntime.newInstance().createNode("addDeploymentCompositeTest");
        String curi = node.installContribution("src/test/resources/sample-helloworld.jar");

        String compositeXML =
            "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\""
                + "     xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\""
                + "     targetNamespace=\"http://test/composite\""
                + "     name=\"TestComposite\">"
                + "   <component name=\"TestComponent\">"
                + "      <implementation.java class=\"sample.HelloworldImpl\"/>"
                + "   </component>"
                + "</composite>";
        String compositeURI = node.addDeploymentComposite(curi, new StringReader(compositeXML));

        node.startComposite(curi, compositeURI);
        Assert.assertEquals(1, node.getStartedCompositeURIs().size());
        
        Composite dc = node.getDomainComposite();
        Assert.assertEquals(1, dc.getIncludes().size());
        Composite runningComposite = dc.getIncludes().get(0);
        Assert.assertEquals("TestComposite", runningComposite.getName().getLocalPart());
    }
}
