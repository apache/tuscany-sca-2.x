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
package org.apache.tuscany.core.services.deployment.contribution;

import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.TestCase;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ResourceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.monitor.NullMonitorFactory;

public class JavaContributionProcessorTestCase extends TestCase {
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    private IntrospectionRegistryImpl registry;

    protected void setUp() throws Exception {
        super.setUp();
        registry = new IntrospectionRegistryImpl();
        registry.setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.Monitor.class));
        registry.registerProcessor(new DestroyProcessor());
        registry.registerProcessor(new InitProcessor());
        registry.registerProcessor(new ScopeProcessor());
        JavaInterfaceProcessorRegistryImpl interfaceProcessorRegistry = new JavaInterfaceProcessorRegistryImpl();
        registry.registerProcessor(new PropertyProcessor());
        ReferenceProcessor referenceProcessor = new ReferenceProcessor();
        referenceProcessor.setInterfaceProcessorRegistry(interfaceProcessorRegistry);
        registry.registerProcessor(referenceProcessor);
        registry.registerProcessor(new ResourceProcessor());
    }

    protected URL getClassURL() throws Exception {
        URL jarURL = getClass().getResource(JAR_CONTRIBUTION);
        JarInputStream jar = new JarInputStream(getClass().getResourceAsStream(JAR_CONTRIBUTION));
        URL rootURL = new URL("jar:" + jarURL.toString() + "!/");
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

    public final void testProcessJarArtifacts() throws Exception {
        //ContributionProcessor javaContributionProcessor = new JavaContributionProcessor(registry);

        //URL jarURL = this.getClassURL();
        //javaContributionProcessor.processContent(null, jarURL, jarURL.openStream());
    }
}
