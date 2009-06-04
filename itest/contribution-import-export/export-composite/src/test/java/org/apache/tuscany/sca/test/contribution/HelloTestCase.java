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

import hello.Hello;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/**
 * Tests that the helloworld server is available
 */
public class HelloTestCase extends TestCase {
    private ClassLoader cl;
    private EmbeddedSCADomain domain;

    @Override
    protected void setUp() throws Exception {
        // Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        // Start the domain
        domain.start();

        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();

        URL helloURL = getContributionURL(Hello.class);
        
        // File helloContrib = new File("./target/classes/");
        // URL helloURL = helloContrib.toURL();
        Contribution consumerContribution =
            contributionService.contribute("http://import-export/hello", helloURL, false);
        Composite consumerComposite = consumerContribution.getDeployables().get(0);
        domain.getDomainComposite().getIncludes().add(consumerComposite);
        domain.buildComposite(consumerComposite);

        // Start Components from my composite
        domain.getCompositeActivator().activate(consumerComposite);
        domain.getCompositeActivator().start(consumerComposite);
    }

    private URL getContributionURL(Class<?> cls) throws MalformedURLException {
        String flag = "/" + cls.getName().replace('.', '/') + ".class";
        URL url = cls.getResource(flag);
        String root = url.toExternalForm();
        root = root.substring(0, root.length() - flag.length() + 1);
        if (root.startsWith("jar:") && root.endsWith("!/")) {
            root = root.substring(4, root.length() - 2);
        }
        url = new URL(root);
        return url;
    }

    public void testHello() throws IOException {
        Hello hello = domain.getService(Hello.class, "HelloServiceComponent");
        assertNotNull(hello);
        assertEquals(hello.getGreetings("lresende"), "Hello lresende");
    }

    @Override
    public void tearDown() throws Exception {
        domain.close();
    }

}
