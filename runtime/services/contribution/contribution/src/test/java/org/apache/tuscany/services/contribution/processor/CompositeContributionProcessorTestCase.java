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

import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.TestCase;

public class CompositeContributionProcessorTestCase extends TestCase {
    private static final String CONTRIBUTION = "/repository/sample-calculator.jar";
    private static final String ARTIFACT_URL = "/META-INF/sca/default.scdl";
    private IntrospectionRegistryImpl registry;
    
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
    }
    
    protected URL getArtifactURL() throws Exception {
        URL jarURL = getClass().getResource(CONTRIBUTION);
        JarInputStream jar = new JarInputStream(getClass().getResourceAsStream(CONTRIBUTION));
        URL rootURL =  new URL("jar:" + jarURL.toString() + "!/");
        URL classURL = null;
        
        try {
            while (true) {
                JarEntry entry = jar.getNextJarEntry();
                if (entry.getName().endsWith(".class")) {
                    classURL = new URL(rootURL, entry.getName());
                    break;
                }
            }
        } finally {
            jar.close();
        }
        return classURL;
    }

    public final void testProcessScdl() throws Exception {
        //ContributionProcessor scdlContributionProcessor = new scdlContributionProcessor(registry);
        //URL jarURL = this.getClassURL();
        //javaContributionProcessor.processContent(null, jarURL, jarURL.openStream());
    }
}
