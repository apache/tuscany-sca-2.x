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

import junit.framework.Assert;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.Test;

/**
 * Test case for NodeImpl
 */
public class NodeImplTestCase {
    private static String composite =
        "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"" + " xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.0\""
            + " targetNamespace=\"http://sample/composite\""
            + " xmlns:sc=\"http://sample/composite\""
            + " name=\"HelloWorld\">"
            + " <component name=\"HelloWorld\">"
            + " <implementation.java class=\"hello.HelloWorldImpl\"/>"
            + " </component>"
            + " </composite>";

    @Test
    public void testNodeWithCompositeContent() {
        SCANodeFactory factory = new NodeFactoryImpl();
        SCAContribution contribution = new SCAContribution("c1", new File("target/test-classes").toURI().toString());
        SCANode node = factory.createSCANode("HelloWorld.composite", composite, contribution);
        testNode(node);
    }
    
    @Test
    public void testNodeWithCompositeContentAndNoContribution() {
        SCANodeFactory factory = new NodeFactoryImpl();
        SCANode node = factory.createSCANode("HelloWorld.composite", composite);
        testNode(node);
    }    

    @Test
    public void testNodeWithoutCompositeURI() {
        SCANodeFactory factory = new NodeFactoryImpl();
        SCAContribution contribution = new SCAContribution("c1", new File("target/test-classes").toURI().toString());
        SCANode node = factory.createSCANode(null, contribution);
        testNode(node);
    }
    
    @Test
    public void testNodeWithCompositeURI() {
        SCANodeFactory factory = new NodeFactoryImpl();
        SCAContribution contribution = new SCAContribution("c1", new File("target/test-classes").toURI().toString());
        String compositeURI = new File("target/test-classes/HelloWorld.composite").toURI().toString();
        SCANode node = factory.createSCANode(compositeURI, contribution);
        testNode(node);
    }

    @Test
    public void testNodeWithRelativeCompositeURI() {
        SCANodeFactory factory = new NodeFactoryImpl();
        SCAContribution contribution = new SCAContribution("c1", new File("target/test-classes").toURI().toString());
        String compositeURI = "HelloWorld.composite";
        SCANode node = factory.createSCANode(compositeURI, contribution);
        testNode(node);
    }

    @Test
    public void testNodeWithRelativeCompositeURIAndNoContribution() {
        SCANodeFactory factory = new NodeFactoryImpl();
        String compositeURI = "HelloWorld.composite";
        SCANode node = factory.createSCANode(compositeURI, new SCAContribution[0]);
        testNode(node);
    }

    @Test
    public void testNodeWithClassLoader() {
        SCANodeFactory factory = new NodeFactoryImpl();
        String compositeURI = "HelloWorld.composite";
        SCANode node = factory.createSCANodeFromClassLoader(compositeURI, HelloWorld.class.getClassLoader());
        testNode(node);
    }

    @Test
    public void testNodeWithClassLoaderAndNullComposite() {
        SCANodeFactory factory = new NodeFactoryImpl();
        SCANode node = factory.createSCANodeFromClassLoader(null, HelloWorld.class.getClassLoader());
        testNode(node);
    }
    
    private void testNode(SCANode node) {
        node.start();
        HelloWorld hw = ((SCAClient)node).getService(HelloWorld.class, "HelloWorld");
        Assert.assertEquals("Hello, Node", hw.hello("Node"));
        node.stop();
    }
        
}
