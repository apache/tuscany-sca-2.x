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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.apache.tuscany.spi.services.store.Store.NEVER;

import junit.framework.TestCase;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serializeHeader;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serializeRecordId;
import org.objectweb.howl.log.Configuration;
import org.objectweb.howl.log.LogRecord;

/**
 * @version $Rev$ $Date$
 */
public class JournalTestCase extends TestCase {
    private Journal journal;

    public void testWriteHeader() throws Exception {
        String id = UUID.randomUUID().toString();
        long key = journal.writeHeader(serializeHeader(Header.INSERT, 10, "foo/bar", id, NEVER), false);
        LogRecord record = journal.get(null, key);
        Header header = new Header();
        header.setFields(record.getFields());
        SerializationHelper.deserializeHeader(header);
        assertTrue(record.type == Journal.HEADER);
        assertEquals(Header.INSERT, header.getOperation());
        assertEquals(10, header.getNumBlocks());
        assertEquals("foo/bar", header.getOwnerId());
        assertEquals(id, header.getId());
        assertEquals(NEVER, header.getExpiration());
    }

    public void testWriteRecord() throws Exception {
        byte[] recordId = serializeRecordId("foo", UUID.randomUUID().toString());
        long key = journal.writeBlock("this is a test".getBytes(), recordId, true);
        LogRecord record = journal.get(null, key);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        assertEquals(record.type, Journal.RECORD);
        stream.write(record.getFields()[1]);
        JournalRecord jrecord = new JournalRecord(stream.toByteArray());
        assertEquals("this is a test", new String(jrecord.getData()));
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        Configuration config = new Configuration();
        config.setLogFileDir("../stores");
        journal = new Journal(config);
        journal.open();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        journal.close();
        TestUtils.cleanupLog();
    }
}
