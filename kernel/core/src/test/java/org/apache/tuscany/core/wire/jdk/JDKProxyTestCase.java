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
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKProxyTestCase extends TestCase {
    private JDKWireService wireService;
    private InboundWire inboundWire;
    private Map<Operation<?>, InboundInvocationChain> chains;

    public void testCreateProxy() {
        URI uri = URI.create("#service");
        EasyMock.expect(inboundWire.getSourceUri()).andReturn(uri).atLeastOnce();
        EasyMock.expect(inboundWire.getInboundInvocationChains()).andReturn(chains);
        EasyMock.replay(inboundWire);
        TestInterface intf = wireService.createProxy(TestInterface.class, inboundWire);
        assertTrue(Proxy.isProxyClass(intf.getClass()));
        EasyMock.verify(inboundWire);
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new JDKWireService();
        inboundWire = EasyMock.createMock(InboundWire.class);
        chains = new HashMap<Operation<?>, InboundInvocationChain>();
    }

    public static interface TestInterface {
        int primitives(int i);

        String objects(String object);
    }
}
