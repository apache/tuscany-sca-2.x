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
package org.apache.tuscany.spi.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SCAObjectInputStreamTestCase extends TestCase {

    public void testSCAExternalizable() throws Exception {
        WorkContext context = EasyMock.createMock(WorkContext.class);
        MockSCAExternalizable ext = new MockSCAExternalizable();
        MockSerializable serializable = new MockSerializable();
        serializable.setExternalizable(ext);
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(bas);
        o.writeObject(serializable);
        o.close();
        ByteArrayInputStream bytes = new ByteArrayInputStream(bas.toByteArray());
        SCAObjectInputStream stream = new SCAObjectInputStream(bytes, context);
        MockSerializable deserialized = (MockSerializable) stream.readObject();
        MockSCAExternalizable deserializedExt = deserialized.getExternalizable();
        assertTrue(deserializedExt.isActivated());
        assertEquals(context, deserializedExt.getContext());
    }
}
