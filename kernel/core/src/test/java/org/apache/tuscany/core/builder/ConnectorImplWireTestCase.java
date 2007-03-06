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
package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorImplWireTestCase extends TestCase {
    private ComponentManager manager;
    private Connector connector;
    private ServiceContract<?> contract;

    public void testConnectWireDefinition() throws Exception {
    }


    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComponentManagerImpl();
        connector = new ConnectorImpl(null, null, manager, null, null);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(Foo.class);
    }

    private interface Foo {
        void bar();
    }

}
