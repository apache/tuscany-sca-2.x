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

package org.apache.tuscany.sca.extensibility.equinox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Test the Equinox service discoverer.
 *
 * @version $Rev$ $Date$
 */
public class EquinoxServiceDiscovererTestCase {
    private static EquinoxServiceDiscoverer discoverer;
    private static Bundle testBundle1;
    private static Bundle testBundle2;
    private static TestEquinoxHost host;

    private static String getState(Bundle b) {
        StringBuffer sb = new StringBuffer();
        int s = b.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append("UNINSTALLED ");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append("INSTALLED ");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append("RESOLVED ");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append("STARTING ");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append("STOPPING ");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append("ACTIVE ");
        }
        return sb.toString();

    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        host = new TestEquinoxHost();
        BundleContext context = host.start();

        InputStream is = EquinoxServiceDiscovererTestCase.class.getResourceAsStream("/test-bundle-v1.jar");
        testBundle1 = context.installBundle("test-bundle-v1", is);
        is.close();
        
        is = EquinoxServiceDiscovererTestCase.class.getResourceAsStream("/test-bundle-v2.jar");
        testBundle2 = context.installBundle("test-bundle-v2", is);
        is.close();

        discoverer = new EquinoxServiceDiscoverer(context);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (testBundle1 != null) {
            // Uninstall the bundle to clean up the cache
            testBundle1.uninstall();
        }
        if (testBundle2 != null) {
            // Uninstall the bundle to clean up the cache
            testBundle2.uninstall();
        }
        host.stop();
        System.out.println("Done");
    }

    @Test
    public void testDiscovery() throws IOException {
        // Both version 1 and 2 should be found because test.TestService is not a Tuscany service
        Collection<ServiceDeclaration> descriptors = discoverer.getServiceDeclarations("test.TestService");
        Assert.assertEquals(2, descriptors.size());
        descriptors = discoverer.getServiceDeclarations("notthere");
        Assert.assertEquals(0, descriptors.size());
    }
    
    @Test
    public void testDiscoverResources() throws IOException {
        Collection<ServiceDeclaration> descriptors = discoverer.getServiceDeclarations("/META-INF/services/test.TestService");
        Assert.assertEquals(2, descriptors.size());
    }

    @Test
    public void testDiscoveryFirst() throws IOException {
        ServiceDeclaration descriptor = discoverer.getServiceDeclaration("test.TestService");
        Assert.assertNotNull(descriptor);
        descriptor = discoverer.getServiceDeclaration("notthere");
        Assert.assertNull(descriptor);
    }

    @Test
    public void testTuscanyDiscovery() throws IOException {
        Collection<ServiceDeclaration> descriptors = discoverer.getServiceDeclarations("org.apache.tuscany.sca.test.TestService");
        // Only the version 2 should be found
        Assert.assertEquals(1, descriptors.size());
        ServiceDeclaration sd = descriptors.iterator().next();
        Assert.assertEquals("2", sd.getAttributes().get("version"));
    }

}
