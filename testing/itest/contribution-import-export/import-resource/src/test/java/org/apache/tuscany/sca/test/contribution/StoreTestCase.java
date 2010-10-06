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
package org.apache.tuscany.sca.test.contribution;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/**
 * Tests that the helloworld server is available
 */
public class StoreTestCase extends TestCase {
    private ClassLoader cl;
    private EmbeddedSCADomain domain;
    private Contribution storeContribution;
    private Contribution resourceContribution;

    @Override
    protected void setUp() throws Exception {
        //Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();

        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();

        URL javaContribURL = getContributionURL(cl, "ufservices/store.html");
        resourceContribution = contributionService.contribute("http://import-export/export-resource", javaContribURL, false);
        for (Composite deployable : resourceContribution.getDeployables()) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.buildComposite(deployable);
        }

        URL storeContributionURL = getContributionURL(cl, "store.composite");

        storeContribution =
            contributionService.contribute("http://import-export/store", storeContributionURL, false);
        for (Composite deployable : storeContribution.getDeployables()) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.buildComposite(deployable);
        }

        // Start Components from my composite
        for (Composite deployable : storeContribution.getDeployables()) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }
    }

    public void testPing() throws IOException {
    	new Socket("127.0.0.1", 8085);
    }


    @Override
    public void tearDown() throws Exception {
        ContributionService contributionService = domain.getContributionService();

        // Remove the contribution from the in-memory repository
        contributionService.remove("http://import-export/store");
        contributionService.remove("http://import-export/export-resource");

        // Stop Components from my composite
        for (Composite deployable : storeContribution.getDeployables()) {
            domain.getCompositeActivator().stop(deployable);
            domain.getCompositeActivator().deactivate(deployable);
        }

        domain.stop();

        domain.close();
    }

    
    /**
     * Utility methods
     */
    

    private URL getContributionURL(ClassLoader cl, Class<?> cls) throws MalformedURLException {
        String flag = "/" + cls.getName().replace('.', '/') + ".class";
        URL url = cl.getResource(flag);
        String root = url.toExternalForm();
        root = root.substring(0, root.length() - flag.length() + 1);
        if (root.startsWith("jar:") && root.endsWith("!/")) {
            root = root.substring(4, root.length() - 2);
        }
        url = new URL(root);
        return url;
    }
    
    private URL getContributionURL(ClassLoader cl, String uri) throws MalformedURLException {
        URL url = cl.getResource(uri);
        String root = url.toExternalForm();
        root = root.substring(0, root.length() - uri.length());
        if (root.startsWith("jar:") && root.endsWith("!/")) {
            root = root.substring(4, root.length() - 2);
        }
        url = new URL(root);
        return url;
    }        
}
