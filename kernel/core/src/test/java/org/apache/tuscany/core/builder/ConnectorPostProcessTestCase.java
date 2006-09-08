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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collections;

import junit.framework.TestCase;

import org.apache.tuscany.spi.builder.WirePostProcessorRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorPostProcessTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testInboundToOutboundPostProcessCalled() throws Exception {
        OutboundWire owire = createNiceMock(OutboundWire.class);
        replay(owire);
        InboundWire iwire = createNiceMock(InboundWire.class);
        expect(iwire.getInvocationChains()).andReturn(Collections.emptyMap());
        replay(iwire);
        WirePostProcessorRegistry registry = createMock(WirePostProcessorRegistry.class);
        registry.process(EasyMock.eq(iwire), EasyMock.eq(owire));
        replay(registry);
        WireService wireService = createMock(WireService.class);
        expect(wireService.isWireable((ServiceContract<?>)EasyMock.anyObject(), 
                (ServiceContract<?>)EasyMock.anyObject())).andReturn(true).anyTimes();
        replay(wireService);
        ConnectorImpl connector = new ConnectorImpl(wireService, registry);
        connector.connect(iwire, owire, false);
        verify(registry);
    }

    @SuppressWarnings("unchecked")
    public void testOutboundToInboundPostProcessCalled() throws Exception {
        OutboundWire owire = createNiceMock(OutboundWire.class);
        expect(owire.getInvocationChains()).andReturn(Collections.emptyMap());
        expect(owire.getTargetCallbackInvocationChains()).andReturn(Collections.emptyMap());
        replay(owire);
        InboundWire iwire = createNiceMock(InboundWire.class);
        expect(iwire.getSourceCallbackInvocationChains("Component")).andReturn(Collections.emptyMap());
        replay(iwire);
        WirePostProcessorRegistry registry = createMock(WirePostProcessorRegistry.class);
        registry.process(EasyMock.eq(owire), EasyMock.eq(iwire));
        replay(registry);
        WireService wireService = createMock(WireService.class);
        expect(wireService.isWireable((ServiceContract<?>)EasyMock.anyObject(), 
                (ServiceContract<?>)EasyMock.anyObject())).andReturn(true).anyTimes();
        replay(wireService);        
        ConnectorImpl connector = new ConnectorImpl(wireService, registry);
        Component source = createNiceMock(Component.class);
        expect(source.getName()).andReturn("Component");
        replay(source);
        connector.connect(source, null, owire, iwire, false);
        verify(registry);
    }



}
