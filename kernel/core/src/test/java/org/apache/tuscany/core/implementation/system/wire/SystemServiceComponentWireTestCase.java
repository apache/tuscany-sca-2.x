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
package org.apache.tuscany.core.implementation.system.wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.component.SystemService;
import org.apache.tuscany.core.implementation.system.component.SystemServiceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.easymock.EasyMock;

/**
 * Verifies that a system context interacts correctly with configured, connected inbound and outbound system wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceComponentWireTestCase extends TestCase {

    public void testServiceContext() throws NoSuchMethodException {
        Target target = new TargetImpl();
        SystemOutboundWire outboundWire = EasyMock.createMock(SystemOutboundWire.class);
        EasyMock.expect(outboundWire.getTargetService()).andReturn(target);
        EasyMock.replay(outboundWire);
        SystemInboundWire wire = new SystemInboundWireImpl("Target", Target.class);
        SystemService serviceContext = new SystemServiceImpl("service", null);
        serviceContext.setInboundWire(wire);
        serviceContext.setOutboundWire(outboundWire);
        wire.setTargetWire(outboundWire);
        assertSame(target, serviceContext.getServiceInstance());
        EasyMock.verify(outboundWire);
    }
}
