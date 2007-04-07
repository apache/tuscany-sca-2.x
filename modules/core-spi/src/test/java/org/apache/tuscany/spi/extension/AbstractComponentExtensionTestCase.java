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
package org.apache.tuscany.spi.extension;

import java.net.URI;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AbstractComponentExtensionTestCase extends TestCase {
    private Component composite;

    public void testGetService() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("composite#service")).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertNotNull(composite.getService("service"));
    }

    public void testGetDefaultService() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("composite#service")).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertNotNull(composite.getService(null));
    }

    public void testGetReference() throws Exception {
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getUri()).andReturn(URI.create("composite#service")).atLeastOnce();
        EasyMock.replay(reference);
        composite.register(reference);
        assertNotNull(composite.getReference("service"));
    }

    public void testDefaultReference() throws Exception {
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getUri()).andReturn(URI.create("composite#service")).atLeastOnce();
        EasyMock.replay(reference);
        composite.register(reference);
        assertNotNull(composite.getReference(null));
    }

    protected void setUp() throws Exception {
        super.setUp();
        composite = new AbstractComponentExtension(new URI("foo")) {

            public Scope getScope() {
                return null;
            }

            public List<Wire> getWires(String name) {
                throw new UnsupportedOperationException();
            }

            public void attachWire(Wire wire) {
                throw new UnsupportedOperationException();
            }

            public void attachWires(List<Wire> wires) {
                throw new UnsupportedOperationException();
            }

            public void attachCallbackWire(Wire wire) {
                throw new UnsupportedOperationException();
            }

            public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback)
                throws TargetInvokerCreationException {
                return null;
            }

        };
    }
}
