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

import java.util.UUID;

import junit.framework.TestCase;
import static org.apache.tuscany.spi.services.store.Store.NEVER;

/**
 * @version $Rev$ $Date$
 */
public class RecordTestCase extends TestCase {

    public void testInsertCompare() {
        Record record = new Record(null, UUID.randomUUID().toString(), "foo", NEVER, Record.INSERT);
        Record record2 = new Record(null, UUID.randomUUID().toString(), "bar", NEVER, Record.INSERT);
        assertEquals(0, record.compareTo(record2));
    }

    public void testUpdateCompare() {
        Record record = new Record(null, UUID.randomUUID().toString(), "foo", NEVER, Record.UPDATE);
        Record record2 = new Record(null, UUID.randomUUID().toString(), "bar", NEVER, Record.UPDATE);
        assertEquals(0, record.compareTo(record2));
    }

    public void testInsertUpdateCompare() {
        Record record = new Record(null, UUID.randomUUID().toString(), "foo", NEVER, Record.INSERT);
        Record record2 = new Record(null, UUID.randomUUID().toString(), "bar", NEVER, Record.UPDATE);
        assertEquals(-1, record.compareTo(record2));
    }

    public void testUpdateInsertCompare() {
        Record record = new Record(null, UUID.randomUUID().toString(), "foo", NEVER, Record.UPDATE);
        Record record2 = new Record(null, UUID.randomUUID().toString(), "bar", NEVER, Record.INSERT);
        assertEquals(1, record.compareTo(record2));
    }

    public void testAssertion() {
        Record record = new Record(null, UUID.randomUUID().toString(), "foo", NEVER, 4);
        Record record2 = new Record(null, UUID.randomUUID().toString(), "bar", NEVER, 5);
        try {
            record.compareTo(record2);
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

}
