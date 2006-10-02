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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetNotFoundException;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @version $Rev$ $Date$
 */
public class SystemOutboundAutowireTestCase extends TestCase {

    public void testAutowire() {
        CompositeComponent component = createMock(CompositeComponent.class);
        expect(component.resolveSystemInstance(Object.class)).andReturn(new Object());
        replay(component);
        SystemOutboundAutowire wire = new SystemOutboundAutowire("foo", Object.class, component, false);
        assertNotNull(wire.getTargetService());
        verify(component);
    }


    public void testNonExistentAutowire() {
        CompositeComponent component = createMock(CompositeComponent.class);
        expect(component.resolveSystemInstance(Object.class)).andReturn(null);
        replay(component);
        SystemOutboundAutowire wire = new SystemOutboundAutowire("foo", Object.class, component, true);
        try {
            wire.getTargetService();
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
        verify(component);
    }


    public void testNonExistentAutowireNotRequired() {
        CompositeComponent component = createMock(CompositeComponent.class);
        expect(component.resolveSystemInstance(Object.class)).andReturn(null);
        replay(component);
        SystemOutboundAutowire wire = new SystemOutboundAutowire("foo", Object.class, component, false);
        try {
            assertNull(wire.getTargetService());
        } catch (TargetNotFoundException e) {
            fail();
        }
        verify(component);
    }


}
