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

package org.apache.tuscany.sca.extensibility;

import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for ClasspathServiceDiscover
 */
public class ContextClassLoaderServiceDiscovererTestCase {
    private static ContextClassLoaderServiceDiscoverer discover;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        discover = new ContextClassLoaderServiceDiscoverer();
    }

    @Test
    public void testDiscovery() {
        Set<ServiceDeclaration> discriptors =
            discover.discover("org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint", false);
        Assert.assertEquals(1, discriptors.size());
        discriptors =
            discover.discover("notthere", false);
        Assert.assertEquals(0, discriptors.size());
    }
    
    @Test
    public void testDiscoveryFirst() {
        Set<ServiceDeclaration> discriptors =
            discover.discover("org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint", true);
        Assert.assertEquals(1, discriptors.size());
        discriptors =
            discover.discover("notthere", true);
        Assert.assertEquals(0, discriptors.size());
    }


    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

}
