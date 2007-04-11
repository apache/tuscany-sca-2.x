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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Proxy;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class JDKProxyTestCaseFIXME extends TestCase {
    private JDKProxyService proxyService;

    public void testCreateProxy() {
        URI uri = URI.create("#service");
        Wire wire = new WireImpl();
        wire.setSourceUri(uri);
        Contract contract = new DefaultAssemblyFactory().createComponentReference();
        wire.setSourceContract(contract.getInterfaceContract());
        TestInterface proxy = proxyService.createProxy(TestInterface.class, wire);
        assertTrue(Proxy.isProxyClass(proxy.getClass()));
    }

    protected void setUp() throws Exception {
        super.setUp();
        proxyService = new JDKProxyService();
    }

    public static interface TestInterface {
        int primitives(int i);

        String objects(String object);
    }
}
