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

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.services.store.Store;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.spi.services.store.StoreMonitor;

import org.apache.tuscany.service.persistence.store.jdbc.converter.AbstractConverter;
import org.apache.tuscany.service.persistence.store.jdbc.converter.HSQLDBConverter;
import org.easymock.EasyMock;

/**
 * Verifies store insert operations using HSQLDB
 *
 * @version $Rev$ $Date$
 */
public class JDBCStoreInsertTestCase extends TestCase {
    private DataSource ds;
    private JDBCStore store;

    public void testInsertMetaData() throws Exception {
        SCAObject object = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(object.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(object);
        store.init();
        JDBCStoreInsertTestCase.Foo foo = new JDBCStoreInsertTestCase.Foo("test");
        String id = UUID.randomUUID().toString();
        store.insertRecord(object, id, foo, Store.NEVER);
        Statement stmt = ds.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(TestUtils.SELECT_SQL);
        rs.next();
        assertEquals(id, rs.getString(AbstractConverter.ID));
        Assert.assertEquals(Store.NEVER, rs.getLong(AbstractConverter.EXPIRATION));
    }

    public void testInsertRead() throws Exception {
        store.init();
        SCAObject object = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(object.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(object);
        JDBCStoreInsertTestCase.Foo foo = new JDBCStoreInsertTestCase.Foo("test");
        String id = UUID.randomUUID().toString();
        store.insertRecord(object, id, foo, Store.NEVER);
        Foo foo2 = (Foo) store.readRecord(object, id);
        assertEquals("test", foo2.data);
    }

    public void testInsertOverwriteRead() throws Exception {
        store.init();
        SCAObject object = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(object.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(object);
        JDBCStoreInsertTestCase.Foo foo = new JDBCStoreInsertTestCase.Foo("test");
        String id = UUID.randomUUID().toString();
        store.insertRecord(object, id, foo, Store.NEVER);
        foo.data = "test2";
        store.insertRecord(object, id, foo, Store.NEVER);
        Foo foo2 = (Foo) store.readRecord(object, id);
        assertEquals("test2", foo2.data);
    }

    /**
     * Verifies multiple resources belonging to different owners but sharing the same id are inserted
     */
    public void testMultipleInsert() throws Exception {
        SCAObject owner1 = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner1.getCanonicalName()).andReturn("baz").atLeastOnce();
        EasyMock.replay(owner1);
        SCAObject owner2 = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner2.getCanonicalName()).andReturn("bar").atLeastOnce();
        EasyMock.replay(owner2);
        store.init();
        Foo foo1 = new Foo("test");
        String id1 = UUID.randomUUID().toString();
        Foo foo2 = new Foo("test2");
        store.insertRecord(owner1, id1, foo1, Store.NEVER);
        store.insertRecord(owner2, id1, foo2, Store.NEVER);
        Foo retFoo1 = (Foo) store.readRecord(owner1, id1);
        assertEquals("test", retFoo1.data);
        Foo retFoo2 = (Foo) store.readRecord(owner2, id1);
        assertEquals("test2", retFoo2.data);
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
