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

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

import sample.Helloworld;

public class TuscanyRuntimeTestCase {

    @Test
    public void testInstallDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null);
        node.startComposite("helloworld", "helloworld.composite");

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testStopStart() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null);
        node.startComposite("helloworld", "helloworld.composite");

        Map<String, List<String>> ci = node.getStartedCompositeURIs();
        Assert.assertEquals(1, ci.size());

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));

        node.stopComposite("helloworld", "helloworld.composite");
        try {
            node.getService(Helloworld.class, "HelloworldComponent");
            Assert.fail();
        } catch (NoSuchServiceException e) {
            // expected as there is no deployables
        }
        
        node.startComposite("helloworld", "helloworld.composite");
        helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    @Ignore("Depdends on itest/T3558 which isn't in the build?")
    public void testInstallWithDependent() throws NoSuchServiceException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("store", "../../testing/itest/T3558/src/test/resources/sample-store.jar", null, null);
        node.installContribution("store-client", "../../testing/itest/T3558/src/test/resources/sample-store-client.jar", null, null);
        node.startComposite("store", "store.composite");
        node.startComposite("store-client", "store-client.composite");

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testInstallNoDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld-nodeployable.jar", null, null);

        try {
            node.getService(Helloworld.class, "HelloworldComponent");
            Assert.fail();
        } catch (NoSuchServiceException e) {
            // expected as there is no deployables
        }

        node.startComposite("helloworld", "helloworld.composite");
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testGetInstalledContributions() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("foo", "src/test/resources/sample-helloworld-nodeployable.jar", null, null);
        List<String> ics = node.getInstalledContributionURIs();
        Assert.assertEquals(1, ics.size());
        Assert.assertEquals("foo", ics.get(0));
    }

    @Test
    public void testGetDeployedCompostes() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, MalformedURLException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("foo", "src/test/resources/sample-helloworld.jar", null, null);
        List<String> dcs = node.startDeployables("foo");
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get(0));
    }

    @Test
    public void testRemoveComposte() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, MalformedURLException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("foo", "src/test/resources/sample-helloworld.jar", null, null);
        List<String> dcs = node.startDeployables("foo");
        Assert.assertEquals(1, dcs.size());
        Map<String, List<String>> dcsx = node.getStartedCompositeURIs();
        Assert.assertEquals(1, dcsx.size());
        node.stopComposite("foo", "helloworld.composite");
        dcsx = node.getStartedCompositeURIs();
        Assert.assertEquals(0, dcsx.size());
    }

    @Test
    public void testInstallWithMetaData() throws ContributionReadException, ActivationException, ValidationException, NoSuchServiceException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld-nodeployable.jar", "src/test/resources/sca-contribution-generated.xml", null);
        node.startComposite("helloworld", "helloworld.composite");

        Map<String, List<String>> dcs = node.getStartedCompositeURIs();
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get("helloworld").get(0));

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testURI() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        String uri = node.installContribution("src/test/resources/sample-helloworld.jar");
        Assert.assertEquals("sample-helloworld", uri);
    }

    @Test
    public void testStaticCreate() {
        Node node = TuscanyRuntime.runComposite("helloworld.composite", "src/test/resources/sample-helloworld.jar");
        List<String> cs = node.getInstalledContributionURIs();
        Assert.assertEquals(1, cs.size());
        Map<String, List<String>> dcs = node.getStartedCompositeURIs();
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get("sample-helloworld").get(0));
    }

    @Test
    public void testStaticCreateWithNullComposite() {
        Node node = TuscanyRuntime.runComposite(null, "src/test/resources/sample-helloworld.jar");
        List<String> cs = node.getInstalledContributionURIs();
        Assert.assertEquals(1, cs.size());
        Map<String, List<String>> dcs = node.getStartedCompositeURIs();
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get("sample-helloworld").get(0));
    }
    @Test
    public void testRunComposite() throws NoSuchServiceException {
        Node node = TuscanyRuntime.runComposite("helloworld.composite", "src/test/resources/sample-helloworld.jar");
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testRunCompositeSharedRuntime() throws NoSuchServiceException {
        Node node = TuscanyRuntime.runComposite(URI.create("default"), "helloworld.composite", "src/test/resources/sample-helloworld.jar");
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }
}
