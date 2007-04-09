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
package org.apache.tuscany.core.builder;

import java.net.URI;

import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ServiceConnectorTestCase extends TestCase {
    private static final URI PARENT = URI.create("parent");
    private static final URI SOURCE = URI.create("parent#source");
    private static final URI TARGET = URI.create("parent/target");
    private static final URI REFERENCE_TARGET = URI.create("parent#target");
    private ComponentManager manager;
    private MockConnnector connector;
    private ServiceContract<?> contract;

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component
     */
    @SuppressWarnings({"unchecked"})
    public void testConnectToAtomic() throws Exception {
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getBindingType()).andReturn(Wire.LOCAL_BINDING);
        binding.setWire(EasyMock.isA(Wire.class));
        EasyMock.replay(binding);

        Service service = new ServiceImpl(SOURCE, contract);
        service.addServiceBinding(binding);

        Component component = new CompositeComponentImpl(PARENT);
        component.register(service);
        manager.register(component);


        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getUri()).andReturn(TARGET).atLeastOnce();
        target.createTargetInvoker((String) EasyMock.isNull(), EasyMock.isA(Operation.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(target);
        manager.register(target);

        ServiceDefinition definition = new ServiceDefinition(SOURCE, contract, false);
        definition.setTarget(TARGET);
        BindingDefinition bindingDefinition = new BindingDefinition(TARGET) {
        };
        definition.addBinding(bindingDefinition);
        connector.connect(definition);

        EasyMock.verify(binding);
        EasyMock.verify(target);
    }

    @SuppressWarnings({"unchecked"})
    public void testConnectToReference() throws Exception {
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getBindingType()).andReturn(Wire.LOCAL_BINDING);
        binding.setWire(EasyMock.isA(Wire.class));
        EasyMock.replay(binding);

        Service service = new ServiceImpl(SOURCE, contract);
        service.addServiceBinding(binding);

        Component component = new CompositeComponentImpl(PARENT);
        component.register(service);

        ReferenceBinding refBinding = EasyMock.createMock(ReferenceBinding.class);
        refBinding.createTargetInvoker(EasyMock.isA(String.class), EasyMock.isA(Operation.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(refBinding);
        Reference target = new ReferenceImpl(REFERENCE_TARGET, contract);
        target.addReferenceBinding(refBinding);
        component.register(target);
        manager.register(component);

        ServiceDefinition definition = new ServiceDefinition(SOURCE, contract, false);
        definition.setTarget(REFERENCE_TARGET);
        BindingDefinition bindingDefinition = new BindingDefinition(REFERENCE_TARGET) {
        };
        definition.addBinding(bindingDefinition);
        connector.connect(definition);

        EasyMock.verify(binding);
        EasyMock.verify(refBinding);
    }


    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComponentManagerImpl();
        connector = new MockConnnector(manager);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(Foo.class);
    }


    private interface Foo {
        void bar();
    }

    private class MockConnnector extends ConnectorImpl {

        public MockConnnector(ComponentManager componentManager) {
            super(componentManager);
        }

        public void connect(ServiceDefinition definition) throws WiringException {
            super.connect(definition);
        }
    }

}
