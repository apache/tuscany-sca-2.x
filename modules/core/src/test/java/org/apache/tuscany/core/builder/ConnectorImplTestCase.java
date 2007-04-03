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

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
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
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorImplTestCase extends TestCase {
    private TestConnector connector;
    private ComponentManager manager;

    public void testConnectTargetNotFound() throws Exception {
        Implementation<ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>> impl =
            new Implementation<ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>>() {
            };
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        URI refUri = URI.create("ref");
        ReferenceDefinition referenceDefinition = new ReferenceDefinition(refUri, null);
        referenceDefinition.setRequired(true);
        type.add(referenceDefinition);
        impl.setComponentType(type);

        URI sourceUri = URI.create("source");
        ComponentDefinition<?> definition =
            new ComponentDefinition<Implementation<ComponentType<ServiceDefinition,
                ReferenceDefinition, Property<?>>>>(impl);
        definition.setUri(sourceUri);
        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(refUri);
        referenceTarget.addTarget(URI.create("NotThere"));
        definition.add(referenceTarget);
        Component component = EasyMock.createMock(Component.class);
        EasyMock.expect(component.getUri()).andReturn(sourceUri);
        EasyMock.replay(component);
        manager.register(component);
        try {
            connector.connect(definition);
            fail();
        } catch (ComponentNotFoundException e) {
            // expected
        }
    }

    /**
     * Verifies a non-existent target does not throw an error.
     * <p/>
     * TODO JFM when the allocator is in place it should optimize connecting to non-existent targets but keep it for
     * now
     */
    public void testConnectTargetNotFoundNonRequiredReference() throws Exception {
        Implementation<ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>> impl =
            new Implementation<ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>>() {
            };
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        URI refUri = URI.create("ref");
        ReferenceDefinition referenceDefinition = new ReferenceDefinition(refUri, null);
        referenceDefinition.setRequired(false);
        type.add(referenceDefinition);
        impl.setComponentType(type);

        URI sourceUri = URI.create("source");
        ComponentDefinition<?> definition =
            new ComponentDefinition<Implementation<ComponentType<ServiceDefinition,
                ReferenceDefinition, Property<?>>>>(impl);
        definition.setUri(sourceUri);
        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(refUri);
        referenceTarget.addTarget(URI.create("NotThere"));
        definition.add(referenceTarget);
        Component component = EasyMock.createMock(Component.class);
        EasyMock.expect(component.getUri()).andReturn(sourceUri);
        EasyMock.replay(component);
        manager.register(component);
        connector.connect(definition);
    }

    public void testNonOptimizableTargetComponent() throws Exception {
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isOptimizable()).andReturn(false);
        EasyMock.replay(target);

        Wire wire = EasyMock.createMock(Wire.class);
        wire.setOptimizable(false);
        EasyMock.replay(wire);
        connector.optimize(source, target, wire);
        EasyMock.verify(source);
        EasyMock.verify(target);
        EasyMock.verify(wire);
    }

    public void testOptimizableTargetComponent() throws Exception {
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isOptimizable()).andReturn(true);
        EasyMock.replay(target);

        Wire wire = EasyMock.createMock(Wire.class);
        wire.setOptimizable(true);
        wire.setTarget(EasyMock.eq(target));
        wire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        wire.getCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(wire);
        connector.optimize(source, target, wire);
        EasyMock.verify(source);
        EasyMock.verify(target);
    }

    public void testIsOptimizable() {
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.STATELESS, Scope.CONVERSATION));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.REQUEST));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.SESSION));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.COMPOSITE, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.COMPOSITE, Scope.SYSTEM));

        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.STATELESS));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.REQUEST, Scope.CONVERSATION));
        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.REQUEST));
        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.REQUEST, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.SESSION, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.SESSION, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.SESSION, Scope.REQUEST));
        assertTrue(connector.isOptimizable(Scope.SESSION, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.SESSION, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.SESSION, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.SYSTEM, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.SYSTEM, Scope.SYSTEM));

        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.STATELESS));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.SYSTEM));

    }

    public void testCreateSyncForwardWire() throws Exception {
        ServiceContract<Type> contract = new ServiceContract<Type>() {

        };
        Operation<Type> operation = new Operation<Type>("operation", null, null, null);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("operation", operation);
        contract.setOperations(operations);
        Wire wire = connector.createWire(URI.create("target"), URI.create("#ref"), contract, Wire.LOCAL_BINDING);
        assertEquals(1, wire.getInvocationChains().size());
        InvocationChain chain = wire.getInvocationChains().get(operation);
        Interceptor head = chain.getHeadInterceptor();
        assertTrue(head instanceof InvokerInterceptor);
    }

    public void testCreateSyncCallbackWire() throws Exception {
        ServiceContract<Type> contract = new ServiceContract<Type>() {

        };

        Operation<Type> operation = new Operation<Type>("operation", null, null, null);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("operation", operation);
        contract.setOperations(operations);

        Operation<Type> callbackOperation = new Operation<Type>("operation", null, null, null);
        Map<String, Operation<Type>> callbackOperations = new HashMap<String, Operation<Type>>();
        callbackOperations.put("operation", callbackOperation);
        contract.setCallbackOperations(callbackOperations);

        Wire wire = connector.createWire(URI.create("target"), URI.create("#ref"), contract, Wire.LOCAL_BINDING);
        assertEquals(1, wire.getCallbackInvocationChains().size());
        InvocationChain chain = wire.getCallbackInvocationChains().get(callbackOperation);
        Interceptor head = chain.getHeadInterceptor();
        assertTrue(head instanceof InvokerInterceptor);
    }

    public void testCreateNonBlockingForwardWire() throws Exception {
        ServiceContract<Type> contract = new ServiceContract<Type>() {

        };
        Operation<Type> operation = new Operation<Type>("operation", null, null, null);
        operation.setNonBlocking(true);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("operation", operation);
        contract.setOperations(operations);
        Wire wire = connector.createWire(URI.create("target"), URI.create("#ref"), contract, Wire.LOCAL_BINDING);
        assertEquals(1, wire.getInvocationChains().size());
        InvocationChain chain = wire.getInvocationChains().get(operation);
        Interceptor head = chain.getHeadInterceptor();
        assertTrue(head instanceof NonBlockingInterceptor);
        assertTrue(head.getNext() instanceof InvokerInterceptor);
    }

    public void testCreateNonBlockingCallbackWire() throws Exception {
        ServiceContract<Type> contract = new ServiceContract<Type>() {

        };

        Operation<Type> operation = new Operation<Type>("operation", null, null, null);
        operation.setNonBlocking(true);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("operation", operation);
        contract.setOperations(operations);

        Operation<Type> callbackOperation = new Operation<Type>("operation", null, null, null);
        callbackOperation.setNonBlocking(true);
        Map<String, Operation<Type>> callbackOperations = new HashMap<String, Operation<Type>>();
        callbackOperations.put("operation", callbackOperation);
        contract.setCallbackOperations(callbackOperations);

        Wire wire = connector.createWire(URI.create("target"), URI.create("#ref"), contract, Wire.LOCAL_BINDING);
        assertEquals(1, wire.getCallbackInvocationChains().size());
        InvocationChain chain = wire.getCallbackInvocationChains().get(callbackOperation);
        Interceptor head = chain.getHeadInterceptor();
        assertTrue(head instanceof NonBlockingInterceptor);
        assertTrue(head.getNext() instanceof InvokerInterceptor);
    }

    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComponentManagerImpl();
        connector = new TestConnector(manager);
    }

    private class TestConnector extends ConnectorImpl {

        public TestConnector(ComponentManager componentManager) {
            super(componentManager);
        }

        protected Wire createWire(URI sourceURI, URI targetUri, ServiceContract<?> contract, QName bindingType) {
            return super.createWire(sourceURI, targetUri, contract, bindingType);
        }

        public boolean isOptimizable(Scope pReferrer, Scope pReferee) {
            return super.isOptimizable(pReferrer, pReferee);
        }

    }

}
