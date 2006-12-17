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

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.easymock.EasyMock;

/**
 * Verifies that a system component interacts correctly with configured, connected inbound and outbound system wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceComponentWireTestCase extends TestCase {

    public void testServiceContext() throws Exception {
        Target target = new TargetImpl();
        SystemOutboundWire outboundWire = EasyMock.createMock(SystemOutboundWire.class);
        EasyMock.expect(outboundWire.getTargetService()).andReturn(target);
        EasyMock.replay(outboundWire);
        SystemInboundWire wire = new SystemInboundWireImpl("Target", Target.class);
        SystemService serviceContext = new SystemServiceImpl("service", null, new JavaServiceContract());
        serviceContext.setInboundWire(wire);
        serviceContext.setOutboundWire(outboundWire);
        wire.setTargetWire(outboundWire);
        assertSame(target, serviceContext.getServiceInstance());
        EasyMock.verify(outboundWire);
    }
}
