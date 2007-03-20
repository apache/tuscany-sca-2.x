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

import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.easymock.EasyMock;

/**
 * Tests reference wires are injected properly into system component instances
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentWireInvocationTestCase extends TestCase {
    private URI groupId;

    public void testWireResolution() throws Exception {
        groupId = URI.create("composite");
        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        scope.startContext(groupId, groupId);
        Target target = new TargetImpl();
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.addReferenceSite("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        configuration.setInstanceFactory(new PojoObjectFactory<SourceImpl>(SourceImpl.class.getConstructor()));
        configuration.setName(new URI("source"));
        configuration.setGroupId(groupId);
        AtomicComponent component = new SystemAtomicComponentImpl(configuration);
        component.setScopeContainer(scope);
        Wire wire = EasyMock.createMock(Wire.class);
        URI uri = URI.create("#setTarget");
        EasyMock.expect(wire.getSourceUri()).andReturn(uri).atLeastOnce();
        EasyMock.expect(wire.getTargetInstance()).andReturn(target);
        EasyMock.replay(wire);
        component.attachWire(wire);
        component.start();
        assertSame(((Source) component.getTargetInstance()).getTarget(), target);
        EasyMock.verify(wire);
    }
}
