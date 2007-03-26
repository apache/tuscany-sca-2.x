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
package org.apache.tuscany.services.contribution.processor;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.ImplementationProcessorServiceImpl;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ResourceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.spi.deployer.ContributionProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.model.Contribution;
import org.apache.tuscany.spi.model.DeployedArtifact;

public class JavaContributionProcessorTestCase extends TestCase {
    private static final String CONTRIBUTION = "/repository/sample-calculator.jar";
    private static final String JAVA_ARTIFACT = "calculator/AddService.class";
    private IntrospectionRegistryImpl registry;
    private URI contributionId;
    private Contribution contribution;
    
    protected void setUp() throws Exception {
        super.setUp();
        registry = new IntrospectionRegistryImpl();
        registry.setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.Monitor.class));
        registry.registerProcessor(new DestroyProcessor());
        registry.registerProcessor(new InitProcessor());
        registry.registerProcessor(new ScopeProcessor());
        JavaInterfaceProcessorRegistryImpl interfaceProcessorRegistry = new JavaInterfaceProcessorRegistryImpl();
        ImplementationProcessorService service = new ImplementationProcessorServiceImpl(interfaceProcessorRegistry);
        registry.registerProcessor(new PropertyProcessor(service));
        registry.registerProcessor(new ReferenceProcessor(interfaceProcessorRegistry));
        registry.registerProcessor(new ResourceProcessor());
        
        contributionId = new URI("sca://contribution/001");
        contribution = new Contribution(contributionId);
        contribution.setLocation(getClass().getResource(CONTRIBUTION));
        
        DeployedArtifact classArtifact = new DeployedArtifact(contributionId);
        classArtifact.setLocation(this.getArtifactURL());
    }
    
    protected URL getArtifactURL() throws Exception {
        URL jarURL = getClass().getResource(CONTRIBUTION);
        return new URL("jar:" + jarURL.toString() + "!/" + JAVA_ARTIFACT);
    }

    public final void testProcessJavaArtifact() throws Exception {
        //ContributionProcessor javaContributionProcessor = new JavaContributionProcessor(registry);
        //URL artifactURL = this.getArtifactURL();
        //javaContributionProcessor.processContent(contribution, artifactURL.toURI(), artifactURL.openStream());
    }
}
