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

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WirePostProcessor;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.verify;

/**
 * @version $Rev$ $Date$
 */
public class WirePostProcessorRegistryImplTestCase extends TestCase {

    public void testRegisterUnregister() throws Exception {
        SCAObject container = EasyMock.createMock(SCAObject.class);
        WirePostProcessorRegistry registry = new WirePostProcessorRegistryImpl();
        OutboundWire owire = EasyMock.createMock(OutboundWire.class);
        InboundWire iwire = EasyMock.createMock(InboundWire.class);
        WirePostProcessor processor = createMock(WirePostProcessor.class);
        processor.process(EasyMock.eq(container), EasyMock.eq(owire), EasyMock.eq(container), EasyMock.eq(iwire));
        EasyMock.replay(processor);
        registry.register(processor);
        registry.process(container, owire, container, iwire);
        registry.unregister(processor);
        registry.process(container, owire, container, iwire);
        verify(processor);
    }

    public void testProcessInboundToOutbound() throws Exception {
        WirePostProcessorRegistry registry = new WirePostProcessorRegistryImpl();
        SCAObject container = EasyMock.createMock(SCAObject.class);
        OutboundWire owire = EasyMock.createMock(OutboundWire.class);
        InboundWire iwire = EasyMock.createMock(InboundWire.class);
        WirePostProcessor processor = createMock(WirePostProcessor.class);
        processor.process(EasyMock.eq(container), EasyMock.eq(iwire), EasyMock.eq(container), EasyMock.eq(owire));
        EasyMock.replay(processor);
        registry.register(processor);
        registry.process(container, iwire, container, owire);
        verify(processor);
    }


}
