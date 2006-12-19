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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.event.RuntimeEventListener;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class HttpSessionScopeInitDestroyErrorTestCase extends TestCase {

    public void testInitializeErrorMonitor() throws Exception {
        ScopeContainerMonitor monitor;
        monitor = EasyMock.createMock(ScopeContainerMonitor.class);
        monitor.eagerInitializationError(EasyMock.isA(ObjectCreationException.class));
        EasyMock.replay(monitor);
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(new WorkContextImpl(), monitor);
        scope.start();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.isA(RuntimeEventListener.class));
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.expect(component.createInstance()).andThrow(new ObjectCreationException(""));
        EasyMock.expect(component.isEagerInit()).andReturn(true);
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Object.class);
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.replay(component);
        scope.register(component);
        scope.onEvent(new HttpSessionStart(this, new Object()));
        EasyMock.verify(monitor);
    }

    public void testDestroyErrorMonitor() throws Exception {
        ScopeContainerMonitor monitor;
        monitor = EasyMock.createMock(ScopeContainerMonitor.class);
        monitor.destructionError(EasyMock.isA(TargetDestructionException.class));
        EasyMock.replay(monitor);
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(new WorkContextImpl(), monitor);
        scope.start();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addListener(EasyMock.isA(RuntimeEventListener.class));
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.expect(component.createInstance()).andReturn(new Object());
        EasyMock.expect(component.isEagerInit()).andReturn(true);
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Object.class);
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        component.init(EasyMock.isA(Object.class));
        component.destroy(EasyMock.isA(Object.class));
        EasyMock.expectLastCall().andThrow(new TargetDestructionException("", ""));
        EasyMock.replay(component);
        scope.register(component);
        Object id = new Object();
        scope.onEvent(new HttpSessionStart(this, id));
        scope.onEvent(new HttpSessionEnd(this, id));
        EasyMock.verify(monitor);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }
}
