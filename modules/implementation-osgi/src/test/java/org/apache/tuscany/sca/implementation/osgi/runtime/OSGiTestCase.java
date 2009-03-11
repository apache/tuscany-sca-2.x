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

package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.io.File;
import java.lang.reflect.Proxy;

import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestBundles;
import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestImpl;
import org.apache.tuscany.sca.implementation.osgi.test.OSGiTestInterface;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.equinox.launcher.Contribution;
import org.apache.tuscany.sca.node.equinox.launcher.NodeLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Test the execution of an OSGi implementation type
 *
 * @version $Rev$ $Date$
 */
public class OSGiTestCase {
    private static NodeLauncher host;
    private static Node node;
    protected static String className;
    protected static String compositeName;

    @BeforeClass
    public static void setUp() throws Exception {
        host = NodeLauncher.newInstance();
        className = OSGiTestImpl.class.getName();
        compositeName = "osgitest.composite";
        OSGiTestBundles.createBundle("target/test-classes/OSGiTestService.jar",
                                     OSGiTestInterface.class.getName(),
                                     null,
                                     null,
                                     (String[]) null,
                                     OSGiTestImpl.class, OSGiTestInterface.class);

        node =
            host.createNode("osgitest.composite", new Contribution("c1", new File("target/test-classes").toURI()
                .toString()));
        node.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (host != null) {
            node.stop();
            host.destroy();
        }
    }

    @Test
    public void testOSGiComponent() throws Exception {

        OSGiTestInterface testService = node.getService(OSGiTestInterface.class, "OSGiTestServiceComponent");
        assert (testService != null);

        assert (testService instanceof Proxy);

        String str = testService.testService();

        Assert.assertEquals(className, str);

    }

}
