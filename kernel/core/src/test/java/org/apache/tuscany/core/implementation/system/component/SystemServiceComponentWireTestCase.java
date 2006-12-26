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
package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.easymock.EasyMock;

/**
 * Verifies that a system component interacts correctly with configured, connected inbound and outbound system wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceComponentWireTestCase extends TestCase {

    public void testService() throws Exception {
        Target target = new TargetImpl();
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getTargetService()).andReturn(target);
        EasyMock.replay(outboundWire);
        InboundWire wire = new InboundWireImpl();
        wire.setServiceContract(new JavaServiceContract(Target.class));
        Service service = new SystemServiceImpl("service", null, new JavaServiceContract());
        service.setInboundWire(wire);
        service.setOutboundWire(outboundWire);
        wire.setTargetWire(outboundWire);
        assertSame(target, service.getInboundWire().getTargetService());
        EasyMock.verify(outboundWire);
    }
}
