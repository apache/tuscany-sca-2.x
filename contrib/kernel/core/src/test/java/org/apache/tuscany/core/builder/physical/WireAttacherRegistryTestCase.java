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
package org.apache.tuscany.core.builder.physical;

import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WireAttacherRegistryTestCase extends TestCase {
    WireAttacherRegistryImpl registry;
    private Component source;
    private Component target;
    private Wire wire;
    private WireAttacher attacher;
    private PhysicalWireSourceDefinition pwsd;
    private PhysicalWireTargetDefinition pwtd;

    @SuppressWarnings("unchecked")
    public void testSourceAttachDispatch() throws Exception {
        attacher.attachToSource(source, pwsd, target, pwtd, wire);
        EasyMock.replay(attacher);

        registry.register(PhysicalWireSourceDefinition.class, attacher);
        registry.attachToSource(source, pwsd, target, pwtd, wire);
        EasyMock.verify(attacher);
    }

    @SuppressWarnings("unchecked")
    public void testTargetAttachDispatch() throws Exception {
        attacher.attachToTarget(source, pwsd, target, pwtd, wire);
        EasyMock.replay(attacher);

        registry.register(PhysicalWireTargetDefinition.class, attacher);
        registry.attachToTarget(source, pwsd, target, pwtd, wire);
        EasyMock.verify(attacher);
    }

    protected void setUp() throws Exception {
        super.setUp();
        source = EasyMock.createMock(Component.class);
        EasyMock.replay(source);
        target = EasyMock.createMock(Component.class);
        EasyMock.replay(target);
        wire = EasyMock.createMock(Wire.class);
        EasyMock.replay(wire);
        attacher = EasyMock.createMock(WireAttacher.class);

        pwsd = new PhysicalWireSourceDefinition();
        pwtd = new PhysicalWireTargetDefinition();
        registry = new WireAttacherRegistryImpl();
    }
}
