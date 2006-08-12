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
package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class ServiceExtensionTestCase extends TestCase {

    public void testScope() throws Exception {
        ServiceExtension<Object> service = new ServiceExtension<Object>(null, null, null, null);
        assertEquals(Scope.COMPOSITE, service.getScope());
    }

    @SuppressWarnings("unchecked")
    public void testSetGetInterface() throws Exception {
        InboundWire wire = createMock(InboundWire.class);
        wire.getBusinessInterface();
        expectLastCall().andReturn(getClass());
        replay(wire);
        ServiceExtension<?> service = new ServiceExtension(null, null, null, null);
        service.setInboundWire(wire);
        service.getInterface();
    }


    public void testPrepare() throws Exception {
        ServiceExtension<Object> service = new ServiceExtension<Object>(null, null, null, null);
        service.prepare();
    }

}
