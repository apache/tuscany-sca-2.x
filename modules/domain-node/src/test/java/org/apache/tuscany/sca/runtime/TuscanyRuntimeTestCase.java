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
import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.impl.NodeImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

import sample.Helloworld;

public class TuscanyRuntimeTestCase {

    @Test
    public void testInstallDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null, true);

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testStopStart() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null, true);
        String ci = node.getStartedCompositeURIs("helloworld").get(0);

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));

        node.stop("helloworld", ci);
        try {
            node.getService(Helloworld.class, "HelloworldComponent");
            Assert.fail();
        } catch (NoSuchServiceException e) {
            // expected as there is no deployables
        }
        
        node.start("helloworld", ci);
        helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    @Ignore("Depdends on itest/T3558 which isn't in the build?")
    public void testInstallWithDependent() throws NoSuchServiceException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("store", "../../itest/T3558/src/test/resources/sample-store.jar", null, null, true);
        node.installContribution("store-client", "../../itest/T3558/src/test/resources/sample-store-client.jar", null, null, true);

        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testInstallNoDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld-nodeployable.jar", null, null, true);

        try {
            node.getService(Helloworld.class, "HelloworldComponent");
            Assert.fail();
        } catch (NoSuchServiceException e) {
            // expected as there is no deployables
        }

        node.start("helloworld", "helloworld.composite");
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testGetInstalledContributions() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("foo", "src/test/resources/sample-helloworld-nodeployable.jar", null, null, true);
        List<String> ics = node.getInstalledContributionURIs();
        Assert.assertEquals(1, ics.size());
        Assert.assertEquals("foo", ics.get(0));
    }

    @Test
    public void testGetDeployedCompostes() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, MalformedURLException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("foo", "src/test/resources/sample-helloworld.jar", null, null, true);
        List<String> dcs = node.getStartedCompositeURIs("foo");
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get(0));
    }

    @Test
    public void testRemoveComposte() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, MalformedURLException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("foo", "src/test/resources/sample-helloworld.jar", null, null, true);
        node.stop("foo", "helloworld.composite");
        List<String> dcs = node.getStartedCompositeURIs("foo");
        Assert.assertEquals(0, dcs.size());
    }

    @Test
    public void testInstallWithMetaData() throws ContributionReadException, ActivationException, ValidationException, NoSuchServiceException {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        ((NodeImpl)node).installContribution("helloworld", "src/test/resources/sample-helloworld-nodeployable.jar", "src/test/resources/sca-contribution-generated.xml", null, true);

        List<String> dcs = node.getStartedCompositeURIs("helloworld");
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get(0));

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
        List<String> dcs = node.getStartedCompositeURIs(cs.get(0));
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get(0));
    }

    @Test
    public void testStaticCreateWithNullComposite() {
        Node node = TuscanyRuntime.runComposite(null, "src/test/resources/sample-helloworld.jar");
        List<String> cs = node.getInstalledContributionURIs();
        Assert.assertEquals(1, cs.size());
        List<String> dcs = node.getStartedCompositeURIs(cs.get(0));
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("helloworld.composite", dcs.get(0));
    }
    @Test
    public void testRunComposite() throws NoSuchServiceException {
        Node node = TuscanyRuntime.runComposite("helloworld.composite", "src/test/resources/sample-helloworld.jar");
        try {
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
        } finally {
            node.stop();
        }
    }

    @Test
    public void testRunCompositeSharedRuntime() throws NoSuchServiceException {
        TuscanyRuntime runtime = TuscanyRuntime.newInstance();
        Node node = TuscanyRuntime.runComposite(runtime, "helloworld.composite", "src/test/resources/sample-helloworld.jar");
        try {
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
        } finally {
            node.stop();
        }
        runtime.stop();
    }
}
