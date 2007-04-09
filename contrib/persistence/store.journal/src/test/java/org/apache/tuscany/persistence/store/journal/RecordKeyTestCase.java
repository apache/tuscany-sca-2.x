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

import java.net.URI;

import org.apache.tuscany.spi.component.SCAObject;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class RecordKeyTestCase extends TestCase {

    public void testEquals() throws Exception {
        String id = "bar";
        URI uri = URI.create("foo");
        SCAObject owner1 = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner1.getUri()).andReturn(uri);
        EasyMock.replay(owner1);
        SCAObject owner2 = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner2.getUri()).andReturn(uri);
        EasyMock.replay(owner2);

        RecordKey key1 = new RecordKey(id, owner1);
        RecordKey key2 = new RecordKey(id, owner2);
        assertEquals(key1, key2);
    }

    public void testNotEqualsId() throws Exception {
        String id = "bar";
        SCAObject owner1 = EasyMock.createMock(SCAObject.class);
        URI uri = URI.create("foo");
        EasyMock.expect(owner1.getUri()).andReturn(uri);
        EasyMock.replay(owner1);
        SCAObject owner2 = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner2.getUri()).andReturn(uri);
        EasyMock.replay(owner2);
        RecordKey key1 = new RecordKey(id, owner1);
        RecordKey key2 = new RecordKey("baz", owner2);
        assertFalse(key1.equals(key2));
    }

    public void testNotEqualsOwner() throws Exception {
        String id = "bar";
        URI fooUri = URI.create("foo");
        SCAObject owner1 = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner1.getUri()).andReturn(fooUri);
        EasyMock.replay(owner1);
        SCAObject owner2 = EasyMock.createMock(SCAObject.class);
        URI barUri = URI.create("bar");
        EasyMock.expect(owner2.getUri()).andReturn(barUri);
        EasyMock.replay(owner2);
        RecordKey key1 = new RecordKey(id, owner1);
        RecordKey key2 = new RecordKey(id, owner2);
        assertFalse(key1.equals(key2));
    }

}
