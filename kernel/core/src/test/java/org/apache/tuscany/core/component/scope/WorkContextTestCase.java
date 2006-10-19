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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WorkContextTestCase extends TestCase {

    public void testRemoteComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        CompositeComponent component = EasyMock.createNiceMock(CompositeComponent.class);
        CompositeComponent component2 = EasyMock.createNiceMock(CompositeComponent.class);
        ctx.setRemoteComponent(component);
        assertEquals(component, ctx.getRemoteComponent());
        ctx.setRemoteComponent(component2);
        assertEquals(component2, ctx.getRemoteComponent());
    }

    public void testNonSetRemoteComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getRemoteComponent());
    }

    public void testIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        assertEquals(id, ctx.getIdentifier(this));
    }

    public void testClearIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        ctx.clearIdentifier(this);
        assertNull(ctx.getIdentifier(this));
    }

    public void testClearIndentifiers() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        Object id2 = new Object();
        ctx.setIdentifier(id, id);
        ctx.setIdentifier(id2, id2);
        ctx.clearIdentifiers();
        assertNull(ctx.getIdentifier(id));
        assertNull(ctx.getIdentifier(id2));
    }

    public void testClearNonExistentIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ctx.clearIdentifier(this);
    }

    public void testNullIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        ctx.clearIdentifier(null);
        assertEquals(id, ctx.getIdentifier(this));
    }

    public void testNoIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getIdentifier(this));
    }
    
    public void testSetGetMessageIds() {
        WorkContext context = new WorkContextImpl();
        context.setCurrentMessageId("msg-009");
        context.setCurrentCorrelationId("msg-005");
        assertEquals(context.getCurrentMessageId(), "msg-009");
        assertEquals(context.getCurrentCorrelationId(), "msg-005");
        context.setCurrentMessageId(null);
        context.setCurrentCorrelationId(null);
        assertNull(context.getCurrentMessageId());
        assertNull(context.getCurrentCorrelationId());
    }

    public void testSetGetMessageIdsInNewThread() throws InterruptedException {
        WorkContext context = new WorkContextImpl();
        context.setCurrentMessageId("msg-009");
        context.setCurrentCorrelationId("msg-005");
        assertEquals(context.getCurrentMessageId(), "msg-009");
        assertEquals(context.getCurrentCorrelationId(), "msg-005");
        context.setIdentifier("TX", "002");
        ChildThread t = new ChildThread(context);
        t.start();
        t.join();
        assertTrue(t.passed);
        context.setCurrentMessageId(null);
        context.setCurrentCorrelationId(null);
        assertNull(context.getCurrentMessageId());
        assertNull(context.getCurrentCorrelationId());
    }

    private static final class ChildThread extends Thread {
        private WorkContext context;
        private boolean passed = true;

        private ChildThread(WorkContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                assertNull(context.getCurrentMessageId());
                assertNull(context.getCurrentCorrelationId());
                assertEquals("002", context.getIdentifier("TX"));
            } catch (AssertionError e) {
                passed = false;
            }
        }

    }

}
