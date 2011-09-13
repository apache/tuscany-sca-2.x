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
package org.apache.tuscany.sca.itest.cdi;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConstructorPropertyInjectionTestCase {
    private static org.apache.tuscany.sca.node.Node node;
    
    @BeforeClass
    public static void init() throws Exception {
        try {
            String location = ContributionLocationHelper.getContributionLocation("ConstructorPropertyInjection.composite");
            node = NodeFactory.newInstance().createNode("ConstructorPropertyInjection.composite", new Contribution("c1", location));
            node.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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
    public void testFoo1() throws Exception {
        Bar foo = node.getService(Bar.class, "Foo1Component");
        assertEquals("fubar", foo.getBar());
    }

    @Test
    public void testFoo2() throws Exception {
        Bar foo = node.getService(Bar.class, "Foo2Component");
        assertEquals("fubar", foo.getBar());
    }

    @Test
    public void testFoo3() throws Exception {
        Bar foo = node.getService(Bar.class, "Foo3Component");
        assertEquals("fubar", foo.getBar());
    }
}
