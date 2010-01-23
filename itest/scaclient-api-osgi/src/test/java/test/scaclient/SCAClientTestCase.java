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

package test.scaclient;

import java.net.URI;

import itest.HelloworldService;
import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * Test SCADomain.newInstance and invocation of a service.
 *
 * @version $Rev$ $Date$
 */
public class SCAClientTestCase extends TestCase {

    private static Node node;

    @Override
    protected void setUp() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation(HelloworldService.class);
        node = NodeFactory.getInstance().createNode("Helloworld.composite", new Contribution("test", "./target/classes"));      
        System.out.println("SCA Node API ClassLoader: " + node.getClass().getClassLoader());
        node.start();
    }

    public void testInvoke() throws NoSuchServiceException, NoSuchDomainException {
        HelloworldService service =
            SCAClientFactory.newInstance(URI.create("default")).getService(HelloworldService.class, "HelloworldComponent");
        String result = service.sayHello("petra");
        assertEquals("Hello petra", result);
        System.out.println("Result from SCAClient call = " + result);
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
