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

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests connecting an inbound system wire to an outbound system wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemInboundtoOutboundTestCase extends MockObjectTestCase {

    public void testWire() throws NoSuchMethodException {
        Target target = new TargetImpl();
        Mock mockWire = mock(SystemOutboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        SystemOutboundWire<Target> outboundWire = (SystemOutboundWire<Target>) mockWire.proxy();
        SystemInboundWire<Target> inboundWire = new SystemInboundWireImpl<Target>("service", Target.class);
        inboundWire.setTargetWire(outboundWire);
        assertSame(inboundWire.getTargetService(), target);
    }

}
