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

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.implementation.java.context.ModelHelper;
import org.apache.tuscany.implementation.java.proxy.JDKCallbackInvocationHandler;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandlerTestCase extends TestCase {

    public void testToString() {
        Wire wire = new WireImpl();
        URI uri = URI.create("#wire");
        wire.setSourceUri(uri);
        List<Wire> wires = new ArrayList<Wire>();
        wires.add(wire);
        wire.setSourceContract(ModelHelper.createReference("foo", Foo.class).getInterfaceContract());
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wires, new WorkContextImpl());
        Foo foo = (Foo)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Foo.class}, handler);
        assertNotNull(foo.toString());
    }

    public void testHashCode() {
        Wire wire = new WireImpl();
        wire.setSourceContract(ModelHelper.createReference("foo", Foo.class).getInterfaceContract());
        URI uri = URI.create("#wire");
        wire.setSourceUri(uri);
        List<Wire> wires = new ArrayList<Wire>();
        wires.add(wire);
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wires, new WorkContextImpl());
        Foo foo = (Foo)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Foo.class}, handler);
        assertNotNull(foo.hashCode());
    }

    private interface Foo {

    }
}
