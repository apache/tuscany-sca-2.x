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

package org.apache.tuscany.sca.itest.ctcalc;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This tests how properties are calculated in the case where the implementation class extends another class. Two 
 * cases are considered where the implementation class has:
 * - SCA annotations
 * - No SCA annotations
 * 
 * In particular we're checking that the artifacts of the base class are ignored in both cases. 
 * 
 */
public class CTCalcTestCase {

    private static Node node;
    
    private static CTCalcComponent annotatedComponent;
    private static CTCalcComponent unannotatedComponent;


    /**
     * Method annotated with
     * 
     * @BeforeClass is used for one time set Up, it executes before every tests. This method is used to create a test
     *              Embedded SCA node, to start the SCA node and to get a reference to the 'outerABService' service
     */
    @BeforeClass
    public static void init() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("CTCalcTest.composite");
        node = NodeFactory.newInstance().createNode("CTCalcTest.composite", new Contribution("c1", location));
        node.start();
        annotatedComponent = node.getService(CTCalcComponent.class, "AnnotatedComponent");
        unannotatedComponent = node.getService(CTCalcComponent.class, "UnannotatedComponent");
    }

    /**
     * Method annotated with
     * 
     * @AfterClass is used for one time Tear Down, it executes after every tests. This method is used to close the
     *             node, close any previously opened connections etc
     */
    @AfterClass
    public static void destroy() throws Exception {
        node.stop();
    }

    @Test
    public void testAnnotatedExtension() {
        assertEquals("Annotated", annotatedComponent.test());
        assertEquals(2, ((NodeImpl)node).getDomainComposite().getComponents().get(0).getProperties().size());
        System.out.println(((NodeImpl)node).getDomainComposite().getComponents().get(0).getProperties());
    }

    @Test
    public void testUnannotatedExtension() {
        assertEquals("Unannotated", unannotatedComponent.test());
        assertEquals(2, ((NodeImpl)node).getDomainComposite().getComponents().get(1).getProperties().size());
        System.out.println(((NodeImpl)node).getDomainComposite().getComponents().get(1).getProperties());
    }

    @Test
    public void testAnnotated1Extension() {
        assertEquals("Annotated", annotatedComponent.test());
        assertEquals(1, ((NodeImpl)node).getDomainComposite().getComponents().get(2).getProperties().size());
        System.out.println(((NodeImpl)node).getDomainComposite().getComponents().get(2).getProperties());
    }
}
