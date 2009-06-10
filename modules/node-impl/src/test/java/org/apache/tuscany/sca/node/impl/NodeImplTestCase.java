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

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for NodeImpl
 */
public class NodeImplTestCase {
    private static String composite =
        "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\"" + " xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\""
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

    @Test
    public void testNodeWithAbsoluteCompositeURI() {
        NodeFactory factory = new NodeFactoryImpl();
        Contribution contribution = new Contribution("c1", new File("target/test-classes").toURI().toString());
        String compositeURI = new File("target/test-classes/HelloWorld.composite").toURI().toString();
        Node node = factory.createNode(compositeURI, contribution);
        testNode(node);
    }

    @Test
    public void testDefaultNode() {
        testNode(new NodeFactoryImpl().createNode());
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
}
