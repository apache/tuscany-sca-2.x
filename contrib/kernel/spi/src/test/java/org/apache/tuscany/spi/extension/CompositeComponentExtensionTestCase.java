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

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentExtensionTestCase extends TestCase {
    private Component composite;


    public void testCreateTargetInvoker() throws Exception {
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.createTargetInvoker(EasyMock.eq("service"), EasyMock.isA(Operation.class)))
            .andReturn(null);
        EasyMock.replay(binding);
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("composite#service")).atLeastOnce();
        EasyMock.expect(service.getServiceBindings()).andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        Operation<Type> operation = new Operation<Type>("op", null, null, null);
        composite.createTargetInvoker("service", operation);
        EasyMock.verify(binding);

    }

    protected void setUp() throws Exception {
        super.setUp();
        composite = new CompositeComponentExtension(new URI("foo")) {

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

        };
    }
}
