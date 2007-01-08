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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentExtensionTestCase extends TestCase {
    private CompositeComponent composite;
    private ServiceContract<?> contract;

    public void testDefaultInboundWire() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertEquals(wire, composite.getInboundWire(null));
    }

    public void testNoLocalBinding() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(new QName("foo", "foo")).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertNull(composite.getInboundWire("service"));
    }

    public void testDefaultSystemInboundWire() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(true).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertEquals(wire, composite.getInboundSystemWire(null));
    }

    public void testMoreThanOneServiceGetDefault() throws Exception {
        Service service1 = EasyMock.createMock(Service.class);
        EasyMock.expect(service1.getName()).andReturn("service1").atLeastOnce();
        EasyMock.expect(service1.isSystem()).andReturn(false).atLeastOnce();
        service1.getServiceBindings();
        EasyMock.expectLastCall().andReturn(Collections.emptyList()).atLeastOnce();
        EasyMock.replay(service1);

        Service service2 = EasyMock.createMock(Service.class);
        EasyMock.expect(service2.getName()).andReturn("service2").atLeastOnce();
        EasyMock.expect(service2.isSystem()).andReturn(false).atLeastOnce();
        service2.getServiceBindings();
        EasyMock.expectLastCall().andReturn(Collections.emptyList()).atLeastOnce();
        EasyMock.replay(service2);

        composite.register(service1);
        composite.register(service2);
        assertNull(composite.getInboundWire(null));
        assertNull(composite.getInboundSystemWire(null));
    }

    public void testInboundWire() throws Exception {
        ServiceContract<Object> contract = new ServiceContract<Object>(Object.class) {
        };
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING);
        wire.getServiceContract();
        EasyMock.expectLastCall().andReturn(contract).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);

        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        service.getServiceBindings();
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertNotNull(composite.getInboundWire("service"));
    }


    public void testGetOutboundWires() throws Exception {
        ServiceContract<Object> contract = new ServiceContract<Object>(Object.class) {
        };
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        wire.getServiceContract();
        EasyMock.expectLastCall().andReturn(contract).atLeastOnce();
        EasyMock.replay(wire);
        ReferenceBinding binding = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getName()).andReturn("reference").atLeastOnce();
        EasyMock.expect(reference.isSystem()).andReturn(false).atLeastOnce();
        List<ReferenceBinding> bindings = new ArrayList<ReferenceBinding>();
        EasyMock.expect(reference.getReferenceBindings()).andReturn(bindings).atLeastOnce();
        EasyMock.replay(reference);
        composite.register(reference);
        Map<String, List<OutboundWire>> wires = composite.getOutboundWires();
        assertNotNull(wires.get("reference"));
    }

    public void testInboundSystemWire() throws Exception {
        ServiceContract<Object> contract = new ServiceContract<Object>(Object.class) {
        };
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING);
        wire.getServiceContract();
        EasyMock.expectLastCall().andReturn(contract).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);

        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(true).atLeastOnce();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        service.getServiceBindings();
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertNotNull(composite.getInboundSystemWire("service"));
    }

    protected void setUp() throws Exception {
        super.setUp();
        contract = new ServiceContract<Object>(Object.class) {

        };
        composite = new CompositeComponentExtension("foo", null, null, null) {

            public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire)
                throws TargetInvokerCreationException {
                throw new UnsupportedOperationException();
            }

            public void setScopeContainer(ScopeContainer scopeContainer) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
