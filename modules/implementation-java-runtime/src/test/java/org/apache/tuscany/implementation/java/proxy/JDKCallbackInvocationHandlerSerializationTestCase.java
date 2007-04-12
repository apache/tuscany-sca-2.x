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
package org.apache.tuscany.implementation.java.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.implementation.java.proxy.JDKCallbackInvocationHandler;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandlerSerializationTestCase extends TestCase {
    private WorkContext workContext;
    private List<Wire> wires;
    private AtomicComponent component;

    public void testSerializeDeserialize() throws Exception {
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wires, workContext);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(stream);
        ostream.writeObject(handler);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
        SCAExternalizable externalizable = (SCAExternalizable) istream.readObject();

        externalizable.setWorkContext(workContext);
        externalizable.reactivate();
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        URI uri = URI.create("#foo");
        Wire wire = new WireImpl();
        wire.setSourceUri(uri);
        wires = new ArrayList<Wire>();
        wires.add(wire);
        List<Wire> wireList = new ArrayList<Wire>();
        wireList.add(wire);
        component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getWires("foo")).andReturn(wireList);
        EasyMock.replay(component);
        workContext = new WorkContextImpl();
        workContext.setCurrentAtomicComponent(component);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workContext.setCurrentAtomicComponent(null);
    }
}
