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
package helloworld;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;


/**
 * Tests that the helloworld server is available
 */
public class DomainNodeTestCase{

    private Node node;

    @Before
	public void startServer() throws Exception {
        node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "target/classes", null, null);
        try {
            node.startComposite("helloworld", "helloworld.composite");
        } catch (Exception ex) {
            System.out.println("Caught exception on composite start : " + ex);
        }
	}
    
    @Ignore
    @Test
    public void testWaitForInput() {
        System.out.println("Press a key to end");
        try {
            System.in.read();
        } catch (Exception ex) {
        }
        System.out.println("Shutting down");
    }  
    
    @Test
    public void testContributionUninstall() throws IOException, NoSuchServiceException {
        List<String> installedContributions = node.getInstalledContributionURIs();
        System.out.println(installedContributions);
        
        assertEquals(1, installedContributions.size());
        
        try {
            node.stopCompositeAndUninstallUnused("helloworld", "helloworld.composite");
        } catch (Exception ex) {
            System.out.println("Caught exception on composite stop : " + ex);
        }   
        
        installedContributions = node.getInstalledContributionURIs();
        assertEquals(0, installedContributions.size());
    }


	@After
	public void stopServer() throws Exception {
            if (node != null) {
                node.stop();
            }
	}

}
