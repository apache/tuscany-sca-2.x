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

package itest;

import java.io.File;

import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.domain.node.DomainNode;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Service {

    private static Node serviceNode;
//    private static DomainNode serviceNode;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", RMIBinding.TYPE.toString());
        NodeFactory factory = NodeFactory.newInstance();

        serviceNode = factory.createNode(new File("server-config.xml").toURI().toURL());
        serviceNode.start();
        
//        serviceNode = new DomainNode("tribes:default", new String[]{"../helloworld-service/target/classes"});

    }

    @Test
    public void testNothing() throws Exception {
        synchronized (this) {
            this.wait();
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (serviceNode != null) {
            serviceNode.stop();
        }
        System.clearProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding");
    }    
}
