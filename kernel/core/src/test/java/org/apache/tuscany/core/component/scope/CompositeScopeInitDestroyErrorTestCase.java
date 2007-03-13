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
package org.apache.tuscany.core.component.scope;

import java.net.URI;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.InstanceWrapper;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeScopeInitDestroyErrorTestCase extends TestCase {

    public void testInitializeErrorMonitor() throws Exception {
        ScopeContainerMonitor monitor;
        monitor = EasyMock.createMock(ScopeContainerMonitor.class);
        monitor.eagerInitializationError(EasyMock.isA(ObjectCreationException.class));
        EasyMock.replay(monitor);
        CompositeScopeContainer scope = new CompositeScopeContainer(monitor);
        scope.start();

        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("foo")).atLeastOnce();
        EasyMock.expect(component.createInstanceWrapper()).andThrow(new ObjectCreationException(""));
        EasyMock.expect(component.getInitLevel()).andReturn(1);
        EasyMock.replay(component);
        scope.register(null, component);
        scope.onEvent(new ComponentStart(this, null));
        EasyMock.verify(monitor);
    }

    public void testDestroyErrorMonitor() throws Exception {
        Object comp = new Object();
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        wrapper.stop();
        EasyMock.expectLastCall().andThrow(new TargetDestructionException("", ""));
        EasyMock.replay(wrapper);

        ScopeContainerMonitor monitor;
        monitor = EasyMock.createMock(ScopeContainerMonitor.class);
        monitor.destructionError(EasyMock.isA(TargetDestructionException.class));
        EasyMock.replay(monitor);
        CompositeScopeContainer scope = new CompositeScopeContainer(monitor);
        scope.start();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.expect(component.getInitLevel()).andReturn(1);
        EasyMock.replay(component);
        scope.register(null, component);
        scope.onEvent(new ComponentStart(this, null));
        scope.onEvent(new ComponentStop(this, null));
        EasyMock.verify(monitor);
        EasyMock.verify(component);
        EasyMock.verify(wrapper);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }
}
