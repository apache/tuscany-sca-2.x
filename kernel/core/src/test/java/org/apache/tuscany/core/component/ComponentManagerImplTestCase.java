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
package org.apache.tuscany.core.component;

import java.net.URI;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.DuplicateNameException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class ComponentManagerImplTestCase extends TestCase {
    private ComponentManager manager;

    public void testRegister() throws Exception {
        Component child = EasyMock.createMock(Component.class);
        URI name = URI.create("child");
        EasyMock.expect(child.getUri()).andReturn(name).atLeastOnce();
        EasyMock.replay(child);
        manager.register(child);
        assertEquals(child, manager.getComponent(name));
    }

    public void testRegisterDuplicate() throws Exception {
        Component component1 = EasyMock.createMock(Component.class);
        URI name = URI.create("child");
        EasyMock.expect(component1.getUri()).andReturn(name).atLeastOnce();
        EasyMock.replay(component1);

        Component component2 = EasyMock.createMock(Component.class);
        EasyMock.expect(component2.getUri()).andReturn(name).atLeastOnce();
        EasyMock.replay(component2);

        manager.register(component1);
        try {
            manager.register(component2);
            fail();
        } catch (DuplicateNameException e) {
            // expected
        }
    }

    public void testRegisterSameNameDifferentSchemes() throws Exception {
        Component component1 = EasyMock.createMock(Component.class);
        URI name = URI.create("foo://component");
        EasyMock.expect(component1.getUri()).andReturn(name).atLeastOnce();
        EasyMock.replay(component1);

        Component component2 = EasyMock.createMock(Component.class);
        URI name2 = URI.create("bar://component");
        EasyMock.expect(component2.getUri()).andReturn(name2).atLeastOnce();
        EasyMock.replay(component2);

        manager.register(component1);
        manager.register(component2);
    }

    public void testUnRegister() throws Exception {
        Component component = EasyMock.createMock(Component.class);
        URI name = URI.create("component");
        EasyMock.expect(component.getUri()).andReturn(name).atLeastOnce();
        component.removeListener(EasyMock.isA(ComponentManager.class));
        EasyMock.replay(component);
        manager.register(component);
        manager.unregister(component);
        assertNull(manager.getComponent(name));
    }

    public void testStartNotification() throws Exception {
        Component child1 = EasyMock.createMock(Component.class);
        URI name1 = URI.create("sca://foo/child1");
        EasyMock.expect(child1.getUri()).andReturn(name1).atLeastOnce();
        child1.start();
        EasyMock.replay(child1);

        Component child2 = EasyMock.createMock(Component.class);
        URI name2 = URI.create("sca://bar/child2");
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        EasyMock.replay(child2);

        manager.register(child1);
        manager.register(child2);
        ComponentStart event = new ComponentStart(this, URI.create("sca://foo"));
        manager.onEvent(event);
        EasyMock.verify(child1);
        EasyMock.verify(child2);
    }

    public void testChildStartNotification() throws Exception {
        URI parentUri = URI.create("foo://foo");
        Component child1 = EasyMock.createMock(Component.class);
        URI name = URI.create("foo://foo/child1");
        EasyMock.expect(child1.getUri()).andReturn(name).atLeastOnce();
        child1.start();
        EasyMock.replay(child1);

        Component child2 = EasyMock.createMock(Component.class);
        URI name2 = URI.create("foo://foo/child2");
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        child2.start();
        EasyMock.replay(child2);

        manager.register(child1);
        manager.register(child2);
        ComponentStart event = new ComponentStart(this, parentUri);
        manager.onEvent(event);
        EasyMock.verify(child2);
        EasyMock.verify(child2);
    }

    public void testStopNotification() throws Exception {
        Component child1 = EasyMock.createMock(Component.class);
        URI name = URI.create("foo://foo/child1");
        EasyMock.expect(child1.getUri()).andReturn(name).atLeastOnce();
        child1.stop();
        EasyMock.replay(child1);

        Component child2 = EasyMock.createMock(Component.class);
        URI name2 = URI.create("foo://bar/child2");
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        EasyMock.replay(child2);

        manager.register(child1);
        manager.register(child2);
        ComponentStop event = new ComponentStop(this,  URI.create("foo://foo"));
        manager.onEvent(event);
        EasyMock.verify(child1);
        EasyMock.verify(child2);
    }

    public void testChildStartStopNotification() throws Exception {
        URI parentUri = URI.create("foo://foo");

        Component child1 = EasyMock.createMock(Component.class);
        URI name = URI.create("foo://foo/child1");
        EasyMock.expect(child1.getUri()).andReturn(name).atLeastOnce();
        child1.start();
        child1.stop();
        EasyMock.replay(child1);

        Component child2 = EasyMock.createMock(Component.class);
        URI name2 = URI.create("foo://foo/child2");
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        child2.start();
        child2.stop();
        EasyMock.replay(child2);

        manager.register(child1);
        manager.register(child2);
        manager.onEvent(new ComponentStart(this, parentUri));
        manager.onEvent(new ComponentStop(this, parentUri));
        EasyMock.verify(child2);
        EasyMock.verify(child2);
    }

    public void testChildRestart() throws Exception {
        URI parentUri = URI.create("foo://foo");

        Component child1 = EasyMock.createMock(Component.class);
        URI name = URI.create("foo://foo/child1");
        EasyMock.expect(child1.getUri()).andReturn(name).atLeastOnce();
        child1.start();
        child1.stop();
        child1.start();
        EasyMock.replay(child1);

        Component child2 = EasyMock.createMock(Component.class);
        URI name2 = URI.create("foo://foo/child2");
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        child2.start();
        child2.stop();
        child2.start();
        EasyMock.replay(child2);

        manager.register(child1);
        manager.register(child2);
        manager.onEvent(new ComponentStart(this, parentUri));
        manager.onEvent(new ComponentStop(this, parentUri));
        manager.onEvent(new ComponentStart(this, parentUri));
        EasyMock.verify(child2);
        EasyMock.verify(child2);
    }

    public void testMutiLevelChildStopNotification() throws Exception {
        URI parentUri = URI.create("foo://foo");

        Component child1 = EasyMock.createMock(Component.class);
        URI name = URI.create("foo://foo/child");
        EasyMock.expect(child1.getUri()).andReturn(name).atLeastOnce();
        child1.stop();
        EasyMock.replay(child1);

        Component child2 = EasyMock.createMock(Component.class);
        final URI name2 = URI.create("foo://foo/bar/child");
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        child2.stop();
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                manager.onEvent(new ComponentStop(this, name2));
                return null;
            }
        });
        EasyMock.replay(child2);

        manager.register(child1);
        manager.register(child2);
        ComponentStop event = new ComponentStop(this, parentUri);
        manager.onEvent(event);
        EasyMock.verify(child1);
        EasyMock.verify(child2);
    }

    public void testRegisterParentAfterChildStopNotification() throws Exception {
        URI parentUri = URI.create("foo://foo");
        final URI name1 = URI.create("foo://foo/child");
        Component child1 = EasyMock.createMock(Component.class);
        EasyMock.expect(child1.getUri()).andReturn(name1).atLeastOnce();
        child1.stop();
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                manager.onEvent(new ComponentStop(this, name1));
                return null;
            }
        });
        EasyMock.replay(child1);

        final URI name2 = URI.create("foo://foo/child/child");
        Component child2 = EasyMock.createMock(Component.class);
        EasyMock.expect(child2.getUri()).andReturn(name2).atLeastOnce();
        child2.stop();
        EasyMock.replay(child2);

        // register child2 before child1
        manager.register(child2);
        manager.register(child1);
        ComponentStop event = new ComponentStop(this, parentUri);
        manager.onEvent(event);
        EasyMock.verify(child1);
        EasyMock.verify(child2);
    }

    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComponentManagerImpl();
    }
}
