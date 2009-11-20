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

package org.apache.tuscany.sca.deployment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class DeployerImplTestCase {
    private static ExtensionPointRegistry registry;
    private static Deployer deployer;

    private static URL contributionURL;
    private static URL compositeURL;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        compositeURL = DeployerImplTestCase.class.getResource("HelloWorld.composite");
        contributionURL = new URL(compositeURL, "../");
        registry = new DefaultExtensionPointRegistry();
        deployer = new DeployerImpl(registry);
        deployer.start();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        deployer.stop();
    }

    /**
     * Test method for {@link org.apache.tuscany.sca.deployment.impl.DeployerImpl#build(java.util.List, java.util.Map, org.apache.tuscany.sca.monitor.Monitor)}.
     */
    @Test
    public void testBuild() throws Exception {
        Monitor monitor = deployer.createMonitor();
        Contribution contribution = deployer.loadContribution(contributionURL.toURI(), contributionURL, monitor);
        Composite composite = deployer.build(Arrays.asList(contribution), null, monitor);
        assertTrue(composite != null);
    }

    /**
     * Test method for {@link org.apache.tuscany.sca.deployment.impl.DeployerImpl#loadArtifact(java.net.URI, java.net.URL, org.apache.tuscany.sca.monitor.Monitor)}.
     */
    @Test
    public void testLoadArtifact() throws Exception {
        Monitor monitor = deployer.createMonitor();
        Artifact artifact = deployer.loadArtifact(compositeURL.toURI(), compositeURL, monitor);
        assertTrue(artifact.getModel() instanceof Composite);
        Composite composite = artifact.getModel();
        assertEquals(composite.getName(), new QName("http://sample/composite", "HelloWorld1"));
    }

    /**
     * Test method for {@link org.apache.tuscany.sca.deployment.impl.DeployerImpl#loadDocument(java.net.URI, java.net.URL, org.apache.tuscany.sca.monitor.Monitor)}.
     */
    @Test
    public void testLoadDocument() throws Exception {
        Monitor monitor = deployer.createMonitor();
        Composite composite = deployer.loadDocument(compositeURL.toURI(), compositeURL, monitor);
        assertEquals(composite.getName(), new QName("http://sample/composite", "HelloWorld1"));
    }

    /**
     * Test method for {@link org.apache.tuscany.sca.deployment.impl.DeployerImpl#loadXMLDocument(java.net.URL, org.apache.tuscany.sca.monitor.Monitor)}.
     */
    @Test
    public void testLoadXMLDocumentURLMonitor() throws Exception {
        Monitor monitor = deployer.createMonitor();
        Composite composite = deployer.loadXMLDocument(compositeURL, monitor);
        assertEquals(composite.getName(), new QName("http://sample/composite", "HelloWorld1"));
        Writer sw = new StringWriter();
        deployer.saveXMLDocument(composite, sw, monitor);
        assertTrue(sw.toString() != null);
    }

    /**
     * Test method for {@link org.apache.tuscany.sca.deployment.impl.DeployerImpl#loadContribution(java.net.URI, java.net.URL, org.apache.tuscany.sca.monitor.Monitor)}.
     * @throws URISyntaxException 
     * @throws ContributionReadException 
     */
    @Test
    public void testLoadContribution() throws Exception {
        Monitor monitor = deployer.createMonitor();
        Contribution contribution = deployer.loadContribution(contributionURL.toURI(), contributionURL, monitor);
        Composite composite = contribution.getDeployables().get(0);
        assertEquals(composite.getName(), new QName("http://sample/composite", "HelloWorld1"));
        assertTrue(contribution.getArtifacts().size() >= 2);
    }

}
