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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.junit.Before;
import org.junit.Test;

public class DefaultExtensionPointRegistryTestCase {
    private ExtensionPointRegistry registry;

    @Before
    public void setUp() throws Exception {
        registry = new DefaultExtensionPointRegistry();
    }

    @Test
    public void testRegistry() {
        MyExtensionPoint service = new MyExtensionPointImpl();
        registry.addExtensionPoint(service);
        assertSame(service, registry.getExtensionPoint(MyExtensionPoint.class));
        registry.removeExtensionPoint(service);
        assertNull(registry.getExtensionPoint(MyExtensionPoint.class));
    }

    public static interface MyExtensionPoint {
        void doSomething();
    }

    private static class MyExtensionPointImpl implements MyExtensionPoint {

        public void doSomething() {
        }

    }

}
