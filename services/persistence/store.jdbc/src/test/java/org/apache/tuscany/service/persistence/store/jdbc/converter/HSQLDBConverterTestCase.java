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
package org.apache.tuscany.service.persistence.store.jdbc.converter;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;
import javax.sql.DataSource;

import org.apache.tuscany.spi.services.store.Store;

import junit.framework.TestCase;
import org.apache.tuscany.service.persistence.store.jdbc.Converter;
import static org.apache.tuscany.service.persistence.store.jdbc.TestUtils.cleanup;
import static org.apache.tuscany.service.persistence.store.jdbc.TestUtils.createTables;

/**
 * @version $Rev$ $Date$
 */
public class HSQLDBConverterTestCase extends TestCase {

    private Converter converter;
    private DataSource ds;

    public void testWriteRead() throws Exception {
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement(converter.getInsertSql());
        String id = UUID.randomUUID().toString();
        Foo foo = new Foo();
        foo.data = "test";
        converter.insert(stmt, "foo", id, Store.NEVER, foo);
        stmt.addBatch();
        stmt.execute();
        stmt.close();
        conn.commit();
        conn.close();
        conn = ds.getConnection();
        conn.setAutoCommit(false);
        Foo foo2 = (Foo) converter.read(conn, "foo", id);
        assertEquals("test", foo2.data);
        conn.commit();
        conn.close();
    }

    public void testNotFound() throws Exception {
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        String id = UUID.randomUUID().toString();
        assertNull(converter.read(conn, null, id));
        conn.commit();
        conn.close();
    }

    protected void setUp() throws Exception {
        super.setUp();
        converter = new HSQLDBConverter();
        ds = createTables();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        cleanup(ds);
    }

    @SuppressWarnings({"SerializableHasSerializationMethods"})
    private static class Foo implements Serializable {
        private static final long serialVersionUID = -6119188073169441225L;
        private String data;
    }

}
