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
import java.net.URI;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.services.store.StoreMonitor;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JournalStoreOverflowTestCase extends TestCase {
    @SuppressWarnings({"FieldCanBeLocal"})
    private JournalStore store;
    private SCAObject owner;

    /**
     * Validates records are moved forward during a log overflow
     *
     * @throws Exception
     */
    public void testOverflow() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        store = new JournalStore(monitor);
        store.setMaxBlocksPerFile(3);
        store.init();
        long expire = System.currentTimeMillis() + 200;
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire);
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire);
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire);
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire);
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire);   //
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire);
        Thread.sleep(250);
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire + 20000);
        store.insertRecord(owner, UUID.randomUUID().toString(), "test", expire + 20000);
        store.destroy();
    }


    public void testOverflowAtInsertHeader() throws Exception {

    }

    public void testOverflowAtUpdateHeader() throws Exception {

    }

    public void testOverflowAtDeleteHeader() throws Exception {

    }

    public void testOverflowAtBlock() throws Exception {

    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        owner = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner.getUri()).andReturn(URI.create("foo")).atLeastOnce();
        EasyMock.replay(owner);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanupLog();
    }

}
