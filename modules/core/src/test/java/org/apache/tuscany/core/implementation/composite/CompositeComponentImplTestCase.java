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
package org.apache.tuscany.core.implementation.composite;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.net.URI;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentImplTestCase extends TestCase {

    public void testGetScope() {
        Component composite = new CompositeComponentImpl(URI.create("parent"));
        Assert.assertEquals(Scope.SYSTEM, composite.getScope());
    }

    public void testRegisterService() throws Exception {
        Component composite = new CompositeComponentImpl(URI.create("parent"));
        Service service = new ServiceImpl(URI.create("foo#service"), null);
        composite.register(service);
        assertNotNull(composite.getService("service"));
    }

    public void testRegisterReference() throws Exception {
        Component composite = new CompositeComponentImpl(URI.create("parent"));
        Reference reference = new ReferenceImpl(URI.create("foo#reference"), null);
        composite.register(reference);
        assertNotNull(composite.getReference("reference"));
    }

    public void testOnEvent() {
        CompositeComponentImpl composite = new CompositeComponentImpl(URI.create("parent"));
        Event event = new Event() {
            public Object getSource() {
                return null;
            }
        };
        RuntimeEventListener listener = createMock(RuntimeEventListener.class);
        listener.onEvent(eq(event));
        expectLastCall();
        replay(listener);
        composite.addListener(listener);
        composite.start();
        composite.onEvent(event);
        EasyMock.verify(listener);
    }

}
