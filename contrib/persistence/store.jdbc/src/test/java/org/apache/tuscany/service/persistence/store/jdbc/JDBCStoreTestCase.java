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
package org.apache.tuscany.service.persistence.store.jdbc;

import java.io.Serializable;
import java.util.UUID;
import javax.sql.DataSource;

import org.apache.tuscany.spi.component.SCAObject;

import junit.framework.TestCase;
import static org.apache.tuscany.spi.services.store.Store.NEVER;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.service.persistence.store.jdbc.converter.HSQLDBConverter;
import org.easymock.EasyMock;

/**
 * Verifies store operations using HSQLDB
 *
 * @version $Rev$ $Date$
 */
public class JDBCStoreTestCase extends TestCase {
    private DataSource ds;
    private JDBCStore store;

    public void testNotFound() throws Exception {
        store.init();
        SCAObject object = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(object.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(object);
        String id = UUID.randomUUID().toString();
        assertNull(store.readRecord(object, id));
    }

    public void testRemoveRecord() throws Exception {
        store.init();
        SCAObject object = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(object.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(object);
        Foo foo = new Foo("test");
        String id = UUID.randomUUID().toString();
        store.insertRecord(object, id, foo, NEVER);
        Foo foo2 = (Foo) store.readRecord(object, id);
        assertEquals("test", foo2.data);
        store.removeRecord(object, id);
        assertNull(store.readRecord(object, id));
    }

    public void testExpirationFromStore() throws Exception {
        store.setReaperInterval(10);
        store.init();
        SCAObject object = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(object.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(object);
        Foo foo = new Foo("test");
        String id = UUID.randomUUID().toString();
        store.insertRecord(object, id, foo, System.currentTimeMillis() + 20);
        Thread.sleep(100);
        assertNull(store.readRecord(object, id));
    }

    protected void setUp() throws Exception {
        super.setUp();
        ds = TestUtils.createTables();
        store = new JDBCStore(ds, new HSQLDBConverter(), EasyMock.createNiceMock(StoreMonitor.class));
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanup(ds);
    }

    @SuppressWarnings({"SerializableHasSerializationMethods"})
    public static class Foo implements Serializable {
        private static final long serialVersionUID = -4284779882741318884L;
        private String data;

        public Foo(String data) {
            this.data = data;
        }
    }

}
