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
package org.apache.tuscany.persistence.store.journal;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.net.URI;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.services.store.StoreExpirationEvent;
import org.apache.tuscany.spi.services.store.StoreMonitor;

import junit.framework.TestCase;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;
import org.objectweb.howl.log.LogEventListener;

/**
 * @version $Rev$ $Date$
 */
public class JournalStoreExpireTestCase extends TestCase {
    @SuppressWarnings({"FieldCanBeLocal"})
    private JournalStore store;
    private SCAObject owner;

    public void testNotifyOnExpire() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        Journal journal = EasyMock.createNiceMock(Journal.class);
        journal.setLogEventListener(EasyMock.isA(LogEventListener.class));
        EasyMock.replay(journal);

        RuntimeEventListener listener = org.easymock.EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(org.easymock.EasyMock.isA(StoreExpirationEvent.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch.countDown();
                return null;
            }
        });
        org.easymock.EasyMock.replay(listener);

        store = new JournalStore(monitor, journal) {
        };
        store.addListener(listener);
        store.init();
        final String id = UUID.randomUUID().toString();
        store.insertRecord(owner, id, "test", 1);
        if (!latch.await(10000, TimeUnit.MILLISECONDS)) {
            // failed to notify listener
            fail();
        }
        store.destroy();
        EasyMock.verify(listener);
    }


    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        owner = EasyMock.createMock(SCAObject.class);
        URI uri = URI.create("foo");
        EasyMock.expect(owner.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.replay(owner);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanupLog();
    }
}
