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

package org.apache.tuscany.sca.policy.operations;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.policy.operations.helloworld.HelloWorld;

public class OperationsPolicyTestCase extends TestCase {
    
    private static final QName PERMIT_ALL = new QName(Constants.SCA11_TUSCANY_NS,"permitAll");

    private Node node;
    private HelloWorld helloWorld;

    @Override
    protected void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode(new Contribution("test", "target/classes"));
        node.start();
        helloWorld = node.getService(HelloWorld.class, "HelloWorldClient");
    }
    
    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

    public void testCalculator() throws Exception {
        assertEquals("Hello petraHello petra", helloWorld.getGreetings("petra"));
        Composite domainComposite = ((NodeImpl)node).getDomainComposite();
        
        // Check that the operation level policy is present
        assertEquals(PERMIT_ALL,
                     domainComposite.getComponents().get(1).getImplementation().getOperations().get(0).getPolicySets().get(0).getName());
        
        // Check that the class level policy is present
        assertEquals(PERMIT_ALL,
                     domainComposite.getComponents().get(2).getImplementation().getPolicySets().get(0).getName());        
    }
}
