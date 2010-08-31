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

package sample.impl;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static sample.impl.EmbedUtil.component;
import static sample.impl.EmbedUtil.composite;
import static sample.impl.EmbedUtil.contrib;
import static sample.impl.EmbedUtil.deploy;
import static sample.impl.EmbedUtil.extensionPoints;
import static sample.impl.EmbedUtil.implementation;
import static sample.impl.EmbedUtil.node;
import static sample.impl.EmbedUtil.providerFactories;
import static sample.impl.EmbedUtil.reference;
import static sample.impl.EmbedUtil.service;
import static sample.impl.EmbedUtil.wsdli;
import static sample.impl.TestUtil.here;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sample.Client;
import sample.ClientTest;
import sample.Hello;
import sample.JelloTest;
import sample.Upper;
import sample.UpperTest;
import sample.WelloTest;

/**
 * Test how to assemble a contribution, a SCDL composite and run it on an embedded
 * Tuscany runtime node. Same as EmbedTestCase but shows how stay in control and
 * pass in a ProviderFactory instead of having it loaded and constructed by the
 * runtime node.
 * 
 * @version $Rev$ $Date$
 */
public class InControlTestCase {
    static Node node;

    @BeforeClass
    public static void setUp() throws Exception {
        
        // Load the test WSDL definitions (could also construct
        // the WSDL and XSD models in code but that'd be quite
        // painful, so just load them from XML for now)
        final Contribution contrib = contrib("test", here());
        WSDLInterface Hello_wsdl = wsdli("Hello.wsdl", "http://sample", "Hello", contrib);
        WSDLInterface Upper_wsdl = wsdli("Upper.wsdl", "http://sample", "Upper", contrib);

        // Assemble a test composite model (see EmbedUtil
        // for the little DSL used here, much more concise
        // than using the assembly model interfaces)
        final Composite comp =
        composite("http://sample", "test",
           component("client-test",
               implementation(ClientTest.class,
                   service(Client.class),
                   reference("jello", Hello.class),
                   reference("wello", Hello_wsdl)),
               reference("jello", "jello-test"),
               reference("wello", "wello-test")),
           component("wello-test",
               implementation(WelloTest.class,
                   service(Hello_wsdl),
                   reference("upper", Upper_wsdl)),
               reference("upper", "upper-test")),
           component("jello-test",
               implementation(JelloTest.class,
                   service(Hello.class),
                   reference("upper", Upper.class)),
               reference("upper", "upper-test")),
           component("upper-test",
               implementation(UpperTest.class,
                   service(Upper.class))));
        
        // Register a test instance of our sample implementation ProviderFactory
        providerFactories().addProviderFactory(testProviderFactory());

        // Run with it
        node = node(deploy(contrib, comp));
        node.start();
    }
    
    static ProviderFactory<SampleImplementation> testProviderFactory() {
        // This shows that the embedder can take control and get called
        // when a provider is created, and return whatever provider, then
        // invoker, implementation instance etc
        return new SampleProviderFactory(extensionPoints()) {
            public ImplementationProvider createImplementationProvider(RuntimeComponent comp, SampleImplementation impl) {
                out.println("Creating a provider for component " + comp.getName());
                return super.createImplementationProvider(comp, impl);
            }
        };
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }

    @Test
    public void jello() {
        out.println("RunTestCase.jello");
        final String r = client().jello("Java"); 
        out.println(r);
        assertEquals("HELLO JAVA", r);
    }

    @Test
    public void wello() {
        out.println("RunTestCase.wello");
        final String r = client().wello("WSDL");
        out.println(r);
        assertEquals("HELLO WSDL", r);
    }

    static Client client() {
        return node.getService(Client.class, "client-test/Client");
    }
}
