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

import java.util.List;
import java.util.UUID;

import org.apache.tuscany.spi.component.WorkContext;
import static org.apache.tuscany.spi.services.store.Store.NEVER;

import junit.framework.TestCase;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.deserialize;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SerializationHelperTestCase extends TestCase {

    public void testTwoEvenChunks() throws Exception {
        byte[] bytes = "this is a test".getBytes();
        List<byte[]> chunks = SerializationHelper.partition(bytes, 7);
        assertEquals(2, chunks.size());
        assertEquals("this is", new String(chunks.get(0)));
        assertEquals(" a test", new String(chunks.get(1)));
    }

    public void testUnevenChunks() throws Exception {
        byte[] bytes = "this is a test123".getBytes();
        List<byte[]> chunks = SerializationHelper.partition(bytes, 7);
        assertEquals(3, chunks.size());
        assertEquals("this is", new String(chunks.get(0)));
        assertEquals(" a test", new String(chunks.get(1)));
        assertEquals("123", new String(chunks.get(2)));
    }

    public void testChunkSizeGreater() throws Exception {
        byte[] bytes = "this is a test".getBytes();
        List<byte[]> chunks = SerializationHelper.partition(bytes, 512);
        assertEquals(1, chunks.size());
        byte[] chunk = chunks.get(0);
        assertEquals(14, chunk.length);
        assertEquals("this is a test", new String(chunk));
    }

    public void testSerializeDeserializeNonSCAExternalizable() throws Exception {
        byte[] bytes = SerializationHelper.serialize("foo");
        assertEquals("foo", deserialize(bytes, null));
    }

    public void testSerializeDeserializeSCAExternalizable() throws Exception {
        byte[] bytes = SerializationHelper.serialize(new MockSCAExternalizable());
        WorkContext context = EasyMock.createNiceMock(WorkContext.class);
        MockSCAExternalizable externalized = (MockSCAExternalizable) deserialize(bytes, context);
        assertTrue(externalized.isReactivated());
    }

    public void testDeserializeHeader() throws Exception {
        String id = UUID.randomUUID().toString();
        byte[] bytes = SerializationHelper.serializeHeader(Header.INSERT, 2, "foo", id, NEVER);
        Header header = SerializationHelper.deserializeHeader(new MockHeader(bytes));
        assertEquals(Header.INSERT, header.getOperation());
        assertEquals(2, header.getNumBlocks());
        assertEquals("foo", header.getOwnerId());
        assertEquals(id, header.getId());
        assertEquals(NEVER, header.getExpiration());
    }

    private class MockHeader extends Header {
        public MockHeader(byte[] bytes) {
            super();
            fields = new byte[][]{bytes};
        }
    }


}
