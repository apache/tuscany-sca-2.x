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

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AtomicConnectorTestCase extends TestCase {
    private static final URI PARENT = URI.create("parent");
    private static final URI SOURCE = URI.create("source");
    private static final URI TARGET = URI.create("parent#target");
    private static final URI TARGET_NOFRAGMENT = URI.create("target");
    private static final URI REFERENCE_NAME = URI.create("#ref");
    private ComponentManager manager;
    private Connector connector;
    private ServiceContract<?> contract;

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component
     */
    @SuppressWarnings({"unchecked"})
    public void testConnectToAtomic() throws Exception {
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getUri()).andReturn(SOURCE).atLeastOnce();
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        source.attachWire(EasyMock.isA(Wire.class));
        EasyMock.replay(source);
        manager.register(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isOptimizable()).andReturn(false);
        EasyMock.expect(target.getUri()).andReturn(TARGET_NOFRAGMENT).atLeastOnce();
        target.createTargetInvoker((String) EasyMock.isNull(), EasyMock.isA(Operation.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(target);
        manager.register(target);

        Implementation impl = new Implementation() {
        };
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ReferenceDefinition referenceDefinition = new ReferenceDefinition(REFERENCE_NAME, contract);
        type.add(referenceDefinition);
        impl.setComponentType(type);

        ComponentDefinition<?> definition = new ComponentDefinition(impl);
        definition.setUri(SOURCE);
        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(REFERENCE_NAME);
        referenceTarget.addTarget(TARGET_NOFRAGMENT);
        definition.add(referenceTarget);

        connector.connect(definition);
        EasyMock.verify(source);
        EasyMock.verify(target);
    }

    @SuppressWarnings({"unchecked"})
    public void testConnectToReference() throws Exception {
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getUri()).andReturn(SOURCE).atLeastOnce();
        source.attachWire(EasyMock.isA(Wire.class));
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(source);
        manager.register(source);

        ReferenceBinding binding = EasyMock.createMock(ReferenceBinding.class);
        binding.createTargetInvoker(EasyMock.isA(String.class), EasyMock.isA(Operation.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(binding);

        Reference reference = new ReferenceImpl(TARGET, contract);
        reference.addReferenceBinding(binding);

        Component component = new CompositeComponentImpl(PARENT);
        component.register(reference);
        manager.register(component);

        Implementation impl = new Implementation() {
        };
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ReferenceDefinition referenceDefinition = new ReferenceDefinition(REFERENCE_NAME, contract);
        type.add(referenceDefinition);
        impl.setComponentType(type);

        ComponentDefinition<?> definition = new ComponentDefinition(impl);
        definition.setUri(SOURCE);
        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(REFERENCE_NAME);
        referenceTarget.addTarget(TARGET);
        definition.add(referenceTarget);

        connector.connect(definition);
        EasyMock.verify(source);
        EasyMock.verify(binding);
    }

    @SuppressWarnings({"unchecked"})
    public void testConnectToService() throws Exception {
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getUri()).andReturn(SOURCE).atLeastOnce();
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        source.attachWire(EasyMock.isA(Wire.class));
        EasyMock.replay(source);
        manager.register(source);

        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        binding.createTargetInvoker(EasyMock.isA(String.class), EasyMock.isA(Operation.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(binding);

        Service service = new ServiceImpl(TARGET, contract);
        service.addServiceBinding(binding);

        Component component = new CompositeComponentImpl(PARENT);
        component.register(service);
        manager.register(component);

        Implementation impl = new Implementation() {
        };
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ReferenceDefinition referenceDefinition = new ReferenceDefinition(REFERENCE_NAME, contract);
        type.add(referenceDefinition);
        impl.setComponentType(type);

        ComponentDefinition<?> definition = new ComponentDefinition(impl);
        definition.setUri(SOURCE);
        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(REFERENCE_NAME);
        referenceTarget.addTarget(TARGET);
        definition.add(referenceTarget);

        connector.connect(definition);
        EasyMock.verify(source);
        EasyMock.verify(binding);
    }

    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComponentManagerImpl();
        connector = new ConnectorImpl(null, null, null, manager, null, null);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(Foo.class);
    }


    private interface Foo {
        void bar();
    }

}
