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

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sample.Client;

/**
 * Test run.
 * 
 * @version $Rev$ $Date$
 */
public class RunTestCase {
    static Node node;

    @BeforeClass
    public static void setUp() throws Exception {
        final NodeFactory nf = NodeFactory.newInstance();
        final String here = RunTestCase.class.getProtectionDomain().getCodeSource().getLocation().toString();
        node = nf.createNode(new Contribution("test", here));
        node.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }

    Client client() {
        return node.getService(Client.class, "client-test/Client");
    }

    @Test
    public void jello() {
        out.println("RunTestCase.jello");
        out.println(client().jello("Java"));
    }

    @Test
    public void wello() {
        out.println("RunTestCase.wello");
        out.println(client().wello("WSDL"));
    }

}
