/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * \"License\"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.node.impl;

import hello.HelloWorld;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclarationParser;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.runtime.DomainRegistryFactoryExtensionPoint;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for NodeImpl
 */
public class NodeImplTestCase {
    private static String composite =
        "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\"" + " xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\""
            + " targetNamespace=\"http://sample/composite\""
            + " xmlns:sc=\"http://sample/composite\""
            + " name=\"HelloWorld2\">"
            + " <component name=\"HelloWorld2\">"
            + " <implementation.java class=\"hello.HelloWorldImpl\"/>"
            + " </component>"
            + " </composite>";

    @Test
    public void testNodeWithCompositeContent() {
        NodeFactory factory = new NodeFactoryImpl();
        Contribution contribution = new Contribution("c1", new File("target/test-classes").toURI().toString());
        Node node = factory.createNode(new StringReader(composite), contribution);
        testNode2(node);
    }

    @Test
    public void testNodeWithRelativeCompositeURI() {
        NodeFactory factory = new NodeFactoryImpl();
        Contribution contribution = new Contribution("c1", new File("target/test-classes").toURI().toString());
        String compositeURI = "HelloWorld.composite";
        Node node = factory.createNode(compositeURI, contribution);
        testNode(node);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testNodeWithAbsoluteCompositeURI() throws MalformedURLException {
        NodeFactory factory = new NodeFactoryImpl();
        Contribution contribution = new Contribution("c1", new File("target/test-classes").toURL().toString());
        String compositeURI = new File("target/test-classes/HelloWorld.composite").toURL().toString();
        Node node = factory.createNode(compositeURI, contribution);
        testNode(node);
    }

    @Test
    public void testDefaultNode() {
        testNode(new NodeFactoryImpl().createNode());
    }

    @Test
    public void testNodeWithURI() {
        testNode(new NodeFactoryImpl().createNode(URI.create("foo"),"target/test-classes"));
    }
    @Test
    public void testNodeWithURIandComposite() throws MalformedURLException {
        String compositeURI = new File("target/test-classes/HelloWorld.composite").toURI().toString();
        testNode(new NodeFactoryImpl().createNode(URI.create("foo"), compositeURI, new String[]{"target/test-classes"}));
    }

    @Test
    public void testGetServiceEndpoints() {
        NodeFactory factory = new NodeFactoryImpl();
        Contribution contribution = new Contribution("c1", new File("target/test-classes").toURI().toString());
        NodeImpl node = (NodeImpl)factory.createNode(new StringReader(composite), contribution);
        node.start();
        List<Endpoint> es = node.getServiceEndpoints();   
        Assert.assertEquals(1, es.size());
        Assert.assertEquals("HelloWorld2", es.get(0).getComponent().getName());
        node.stop();
    }

    private void testNode(Node node) {
        node.start();
        HelloWorld hw = node.getService(HelloWorld.class, "HelloWorld");
        Assert.assertEquals("Hello, Node", hw.hello("Node"));
        node.stop();
    }

    private void testNode2(Node node) {
        node.start();
        HelloWorld hw = node.getService(HelloWorld.class, "HelloWorld2");
        Assert.assertEquals("Hello, Node", hw.hello("Node"));
        node.stop();
    }
    
    @Test
    public void testNodeFactoryAttributes() {
        Map<String, Map<String, String>> attrs = new HashMap<String, Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("enabled", "false");
        attrs.put(ValidationSchemaExtensionPoint.class.getName(), map);

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("urn:MyDomain", "multicast://200.0.0.100:50000/MyDomain");
        attrs.put(DomainRegistryFactoryExtensionPoint.class.getName(), map2);

        NodeFactoryImpl factory = (NodeFactoryImpl)NodeFactory.newInstance(attrs);
        Assert.assertFalse(factory.getExtensionPoints().getExtensionPoint(ValidationSchemaExtensionPoint.class)
            .isEnabled());

        DomainRegistryFactoryExtensionPoint domainRegistryFactoryExtensionPoint =
            factory.getExtensionPoints().getExtensionPoint(DomainRegistryFactoryExtensionPoint.class);
        Map<String, String> mapping = domainRegistryFactoryExtensionPoint.getDomainRegistryMapping();
        Assert.assertEquals(1, mapping.size());
        Assert.assertEquals("multicast://200.0.0.100:50000/MyDomain", mapping.get("urn:MyDomain"));
    }
    
    @Test
    public void testLoadNodeFactoryProperties() throws Exception {
        URL url = getClass().getResource("/org/apache/tuscany/sca/node/configuration/test-node-factory.config");
        Collection<Map<String, String>> items = ServiceDeclarationParser.load(url, false);
        for (Map<String, String> attrs : items) {
            System.out.println(attrs);
        }
    }
}
