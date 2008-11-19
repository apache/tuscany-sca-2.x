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

package org.apache.tuscany.sca.node.osgi.launcher;

import hello.HelloWorld;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 */
public class NodeLauncherTestCase {
    private static OSGiHost host;

    @BeforeClass
    public static void setUp() {
        System.setProperty("TUSCANY_HOME", "target/tuscany");
        host = NodeLauncherUtil.startOSGi();
    }

    @AfterClass
    public static void tearDown() {
        if (host != null) {
            NodeLauncherUtil.stopOSGi(host);
        }

    }

    @Test
    public void testLaunch() throws Exception {
        NodeLauncher launcher = NodeLauncher.newInstance();
        SCANode node = launcher.createNodeFromClassLoader("HelloWorld.composite", getClass().getClassLoader());
        node.start();

        HelloWorld hw = ((SCAClient)node).getService(HelloWorld.class, "HelloWorld");
        hw.hello("OSGi");

        node.stop();
    }

    @Test
    @Ignore("contribution-osgi issue")
    public void testLaunchDomain() throws Exception {
        DomainManagerLauncher.main(new String[] {});
    }

}
