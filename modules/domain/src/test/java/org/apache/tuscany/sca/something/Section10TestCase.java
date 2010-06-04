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

import java.net.MalformedURLException;
import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class Section10TestCase {

    @Test
    public void testInstallDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Section10 section10 = Section10Factory.createSection10();
        section10.installContribution("src/test/resources/sample-helloworld.jar");

//        Helloworld helloworldService = section10.getService(Helloworld.class, "HelloworldComponent");
//        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Ignore("TODO: fails with Sun JDK due to SCA properties issue")
    @Test
    public void testInstallWithDependent() throws NoSuchServiceException, ContributionReadException, ActivationException, ValidationException {
        Section10 section10 = Section10Factory.createSection10();
        section10.installContribution("/Tuscany/svn/2.x-trunk/itest/T3558/src/test/resources/sample-store.jar");
        section10.installContribution("/Tuscany/svn/2.x-trunk/itest/T3558/src/test/resources/sample-store-client.jar");

//        Helloworld helloworldService = section10.getService(Helloworld.class, "HelloworldComponent");
//        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testInstallNoDeployable() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Section10 section10 = Section10Factory.createSection10();
        String contributionURI = section10.installContribution("src/test/resources/sample-helloworld-nodeployable.jar");

//        SCAClientFactory scaClientFactory = section10.getSCAClientFactory();
//        try {
//            scaClientFactory.getService(Helloworld.class, "HelloworldComponent");
//            Assert.fail();
//        } catch (NoSuchServiceException e) {
//            // expected as there is no deployables
//        }

        section10.addToDomainLevelComposite(contributionURI + "/helloworld.composite");
//        Helloworld helloworldService = scaClientFactory.getService(Helloworld.class, "HelloworldComponent");
//        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
    }

    @Test
    public void testGetInstalledContributions() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Section10 section10 = Section10Factory.createSection10();
        section10.installContribution("foo", "src/test/resources/sample-helloworld-nodeployable.jar");
        List<String> ics = section10.getInstalledContributions();
        Assert.assertEquals(1, ics.size());
        Assert.assertEquals("foo", ics.get(0));
    }

    @Test
    public void testGetDeployedCompostes() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, MalformedURLException, ActivationException, ValidationException {
        Section10 section10 = Section10Factory.createSection10();
        section10.installContribution("foo", "src/test/resources/sample-helloworld.jar");
        List<String> dcs = section10.getDeployedCompostes("foo");
        Assert.assertEquals(1, dcs.size());
        Assert.assertEquals("foo/helloworld.composite", dcs.get(0));
    }

    @Test
    public void testRemoveComposte() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, MalformedURLException, ActivationException, ValidationException {
        Section10 section10 = Section10Factory.createSection10();
        section10.installContribution("foo", "src/test/resources/sample-helloworld.jar");
        section10.removeFromDomainLevelComposite("foo/helloworld.composite");
        List<String> dcs = section10.getDeployedCompostes("foo");
        Assert.assertEquals(0, dcs.size());
    }

}
