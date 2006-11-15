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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import javax.sql.DataSource;

import junit.framework.TestCase;
import static org.apache.tuscany.service.persistence.store.Store.NEVER;
import org.apache.tuscany.service.persistence.store.StoreMonitor;
import static org.apache.tuscany.service.persistence.store.jdbc.TestUtils.SELECT_SQL;
import org.apache.tuscany.service.persistence.store.jdbc.converter.AbstractConverter;
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

    public void testAppendMetaData() throws Exception {
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        Statement stmt = ds.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(SELECT_SQL);
        rs.next();
        assertEquals(id.getMostSignificantBits(), rs.getLong(AbstractConverter.MOST_SIGNIFICANT_BITS));
        assertEquals(id.getLeastSignificantBits(), rs.getLong(AbstractConverter.LEAST_SIGNIFICANT_BITS));
        assertEquals(NEVER, rs.getLong(AbstractConverter.EXPIRATION));
    }

    public void testAppendRead() throws Exception {
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        Foo foo2 = (Foo) store.readRecord(id);
        assertEquals("test", foo2.data);
    }


    public void testNotFound() throws Exception {
        store.init();
        UUID id = UUID.randomUUID();
        assertNull(store.readRecord(id));
    }

    public void testBatchAppend() throws Exception {
        store.setBatchSize(2);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        Statement stmt = ds.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(SELECT_SQL);
        assertFalse(rs.next());
    }

    public void testCacheFound() throws Exception {
        store.setBatchSize(2);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        Foo foo2 = (Foo) store.readRecord(id);
        assertEquals("test", foo2.data);
    }

    public void testExpirationFromCache() throws Exception {
        store.setBatchSize(2);
        store.setReaperInterval(10);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, System.currentTimeMillis() + 20);
        Thread.sleep(100);
        assertNull(store.readRecord(id));
    }

    public void testExpirationFromStore() throws Exception {
        store.setReaperInterval(10);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, System.currentTimeMillis() + 20);
        Thread.sleep(100);
        assertNull(store.readRecord(id));
    }

    public void testUpdateRead() throws Exception {
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        foo.data = "test2";
        store.updateRecord(id, foo);
        Foo foo2 = (Foo) store.readRecord(id);
        assertEquals("test2", foo2.data);
    }

    public void testCacheUpdateRead() throws Exception {
        store.setBatchSize(10);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        foo.data = "test2";
        store.updateRecord(id, foo);
        Foo foo2 = (Foo) store.readRecord(id);
        assertEquals("test2", foo2.data);
    }

    public void testBatchAppendUpdate() throws Exception {
        store.setBatchSize(3);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        foo.data = "test2";
        store.updateRecord(id, foo);
        // create a second record to force a batch write
        store.appendRecord(UUID.randomUUID(), new Foo("test3"), NEVER);
        Foo foo2 = (Foo) store.readRecord(id);
        assertEquals("test2", foo2.data);
    }

    public void testBatchUpdateUpdate() throws Exception {
        store.setBatchSize(2);
        store.init();
        Foo foo = new Foo("test");
        UUID id = UUID.randomUUID();
        store.appendRecord(id, foo, NEVER);
        UUID id2 = UUID.randomUUID();
        store.appendRecord(id2, foo, NEVER);
        foo.data = "test2";
        store.updateRecord(id, foo);
        store.updateRecord(id2, foo);
        Foo foo2 = (Foo) store.readRecord(id);
        assertEquals("test2", foo2.data);
        Foo foo3 = (Foo) store.readRecord(id2);
        assertEquals("test2", foo3.data);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ds = TestUtils.createTables();
        store = new JDBCStore(ds, new HSQLDBConverter(), EasyMock.createNiceMock(StoreMonitor.class));
        store.setWriteBehind(true);
        store.setBatchSize(0);
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanup(ds);
    }

    public static class Foo implements Serializable {
        private static final long serialVersionUID = -4284779882741318884L;
        private String data;

        public Foo(String data) {
            this.data = data;
        }
    }

}
