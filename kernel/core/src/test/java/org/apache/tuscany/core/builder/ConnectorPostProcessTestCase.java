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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorPostProcessTestCase extends TestCase {

    public void testInboundToOutboundPostProcessCalled() throws Exception {
        SCAObject container = EasyMock.createMock(SCAObject.class);
        OutboundWire owire = new OutboundWireImpl();
        owire.setUri(URI.create("target"));
        InboundWire iwire = createNiceMock(InboundWire.class);
        Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
        expect(iwire.getInvocationChains()).andReturn(chains);
        replay(iwire);
        WirePostProcessorRegistry registry = createMock(WirePostProcessorRegistry.class);
        registry.process(EasyMock.eq(container), EasyMock.eq(iwire), EasyMock.eq(container), EasyMock.eq(owire));
        replay(registry);
        WireService wireService = createMock(WireService.class);
        EasyMock.expect(wireService.checkCompatibility((ServiceContract<?>) EasyMock.anyObject(),
            (ServiceContract<?>) EasyMock.anyObject(), EasyMock.eq(false), EasyMock.eq(false))).andReturn(true);
        replay(wireService);
        ConnectorImpl connector = new ConnectorImpl(wireService, registry, null, null, null);
        connector.connect(container, iwire, container, owire, false);
        verify(registry);
    }

    public void testOutboundToInboundPostProcessCalled() throws Exception {
        AtomicComponent source = createMock(AtomicComponent.class);
        expect(source.getUri()).andReturn(URI.create("source"));
        replay(source);

        AtomicComponent target = createMock(AtomicComponent.class);
        expect(target.getUri()).andReturn(URI.create("target"));
        replay(target);

        OutboundWire owire = new OutboundWireImpl();
        owire.setTargetUri(URI.create("target"));

        InboundWire iwire = new InboundWireImpl();
        iwire.setUri(URI.create("target"));

        WirePostProcessorRegistry registry = createMock(WirePostProcessorRegistry.class);
        registry.process(EasyMock.eq(source), EasyMock.eq(owire), EasyMock.eq(target), EasyMock.eq(iwire));
        replay(registry);

        WireService wireService = createMock(WireService.class);
        EasyMock.expect(wireService.checkCompatibility((ServiceContract<?>) EasyMock.anyObject(),
            (ServiceContract<?>) EasyMock.anyObject(), EasyMock.eq(false), EasyMock.eq(false))).andReturn(true);
        replay(wireService);
        ConnectorImpl connector = new ConnectorImpl(wireService, registry, null, null, null);
        connector.connect(source, owire, target, iwire, false);
        verify(registry);
    }


}
