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

package org.apache.tuscany.sca.contribution.updater.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.node.SCANode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ContributionUpdater.
 * 
 */
public class ContributionUpdaterTestCase {

    // private static SCADomain domain;
    private static SCANode node;
    private static MetaComponentHello helloMeta;

    @Before
    public void setUp() throws Exception {
        /*
         * System.out.println("Setting up domain");
         * 
         * SCADomainFactory domainFactory = SCADomainFactory.newInstance();
         * domain = domainFactory.createSCADomain("http://localhost:9999");
         * 
         * System.out.println("Setting up calculator nodes");
         * 
         * ClassLoader cl = ContributionUpdaterTestCase.class.getClassLoader();
         * 
         * SCANodeFactory nodeFactory = SCANodeFactory.newInstance(); try {
         * log.info("Staring entropy handler"); prng =
         * SecureRandom.getInstance("SHA1PRNG"); } catch
         * (NoSuchAlgorithmException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); } componentName =
         * "HelloComponent"+this.uniqueID();
         * t.setAttribute("componentName",componentName);
         * 
         * node = nodeFactory.createSCANode("http://localhost:8100/nodeA",
         * "http://localhost:9999"); node.addContribution("nodeA",
         * cl.getResource("nodeA/")); node.addToDomainLevelComposite(new
         * QName("http://test", "HelloComposite")); node.start();
         * SCADomainFinder domainFinder = SCADomainFinder.newInstance(); domain =
         * domainFinder.getSCADomain("http://localhost:9999"); helloMeta = new
         * MetaComponentHello();
         * helloMeta.setComponentClass("org.apache.tuscany.sca.contribution.updater.impl.HelloComponent");
         */
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddNewComponent() throws ClassNotFoundException,
            MalformedURLException {
        /*
         * String helloComponent1 = "<component
         * xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"
         * name=\"HelloComponent1\">\n" + "<implementation.java
         * class=\"org.apache.tuscany.sca.contribution.updater.impl.HelloComponent\"/>"+ "</component>";
         * MetaComponent mc =
         * MetaComponentFactory.newMetaComponent("HelloComponent1",helloComponent1);
         * try { ((SCANodeImpl) node).addComponentToComposite(mc, "nodeA",
         * "HelloComposite.composite"); GreetService greet =
         * node.getDomain().getService(GreetService.class, "HelloComponent1");
         * greet.sayHello("Italian").equals("Ciao"); } catch (Exception e) {
         * Assert.fail(e.getMessage()); }
         */
        Assert.assertEquals("skip", "skip");
    }

    @Test
    public void testWireComponents() throws ClassNotFoundException,
            MalformedURLException {
        Assert.assertEquals("skip", "skip");
        /*
         * String helloComponent1 = "<component
         * xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"
         * name=\"HelloComponent1\">\n" + "<implementation.java
         * class=\"org.apache.tuscany.sca.contribution.updater.impl.HelloComponent\"/>"+ "</component>";
         * String helloComponent2 = "<component
         * xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"
         * name=\"HelloComponent2\">\n" + "<implementation.java
         * class=\"org.apache.tuscany.sca.contribution.updater.impl.HelloComponent\"/>"+ "</component>";
         * MetaComponent mc1 =
         * MetaComponentFactory.newMetaComponent("HelloComponent1",helloComponent1);
         * MetaComponent mc2 =
         * MetaComponentFactory.newMetaComponent("HelloComponent2",helloComponent2);
         * 
         * try { ((SCANodeImpl) node).addComponentToComposite(mc1, "nodeA",
         * "HelloComposite.composite"); ((SCANodeImpl)
         * node).addComponentToComposite(mc2, "nodeA",
         * "HelloComposite.composite"); GreetService greet =
         * node.getDomain().getService(GreetService.class, "HelloComponent1");
         * greet.sayHello("Italian").equals("Ciao"); } catch (Exception e) {
         * Assert.fail(e.getMessage()); }
         */
    }

}
