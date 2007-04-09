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

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.tuscany.spi.services.store.StoreReadException;

import junit.framework.TestCase;
import org.apache.tuscany.spi.services.store.StoreWriteException;

/**
 * @version $Rev$ $Date$
 */
public class AbstractConverterTestCase extends TestCase {

    public void testSerializeDeserialize() throws Exception {
        TestConverter converter = new TestConverter();
        Foo foo = new Foo();
        foo.data = "test";
        byte[] bytes = converter.serialize(foo);
        Foo foo2 = (Foo) converter.deserialize(bytes);
        assertEquals("test", foo2.data);
    }

    private class TestConverter extends AbstractConverter {

        public void insert(PreparedStatement stmt, String ownerId, String id, long expiration, Serializable object)
            throws StoreWriteException {

        }

        public void update(PreparedStatement stmt, String ownerId, String id, Serializable object)
            throws StoreWriteException {

        }

        public boolean findAndLock(PreparedStatement stmt,
                                   String ownerId,
                                   String id
        ) throws StoreWriteException {
            return false;
        }

        public Object read(Connection conn, String ownerId, String id) throws StoreReadException {
            return null;
        }

        public void delete(PreparedStatement stmt, String ownerId, String id) throws StoreWriteException {

        }

        @Override
        public byte[] serialize(Serializable serializable) throws IOException {
            return super.serialize(serializable);
        }

        @Override
        public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
            return super.deserialize(bytes);
        }
    }

    @SuppressWarnings({"SerializableHasSerializationMethods"})
    private static class Foo implements Serializable {
        private static final long serialVersionUID = -3774909621298751358L;
        private String data;
    }
}
