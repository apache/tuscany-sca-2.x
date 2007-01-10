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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
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
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundChainHolder;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.mock.binding.MockServiceBinding;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WireServiceExtensionTestCase extends TestCase {
    private TestWireService wireService;
    private Operation<Type> operation;
    private ServiceContract<Type> contract;
    private Operation<Type> callbackOperation;

    public void testCreateInboundChain() throws Exception {
        InboundInvocationChain chain = wireService.createInboundChain(operation);
        assertEquals(operation, chain.getOperation());
    }

    public void testCreateOutboundChain() throws Exception {
        OutboundInvocationChain chain = wireService.createOutboundChain(operation);
        assertEquals(operation, chain.getOperation());
    }

    public void testCreateServiceWire() throws Exception {
        ServiceDefinition definition = new ServiceDefinition("foo", contract, false);
        TargetInvoker invoker = EasyMock.createMock(TargetInvoker.class);
        MessageImpl resp = new MessageImpl();
        EasyMock.expect(invoker.invoke(EasyMock.isA(Message.class))).andReturn(resp);
        EasyMock.replay(invoker);
        InboundWire wire = wireService.createWire(definition);
        assertEquals("foo", wire.getServiceName());
        assertEquals(1, wire.getInvocationChains().size());
        assertEquals(contract, wire.getServiceContract());
        InboundInvocationChain chain = wire.getInvocationChains().get(operation);
        assertEquals(operation, chain.getOperation());
        // verify the chain is invokable
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        assertNotNull(chain.getHeadInterceptor().invoke(msg));
        EasyMock.verify(invoker);
    }

    public void testCreateReferenceWire() throws Exception {
        ReferenceDefinition definition = new ReferenceDefinition("foo", contract);
        ReferenceTarget target = new ReferenceTarget();
        target.addTarget(new URI("bar"));
        target.setReferenceName("refName");

        OutboundWire wire = wireService.createWire(target, definition);
        assertEquals("refName", wire.getReferenceName());
        assertEquals("bar", wire.getTargetName().toString());
        assertFalse(wire.isAutowire());
        assertEquals(1, wire.getInvocationChains().size());
        assertEquals(contract, wire.getServiceContract());
        OutboundInvocationChain chain = wire.getInvocationChains().get(operation);
        assertEquals(operation, chain.getOperation());
        assertNull(chain.getHeadInterceptor());
        assertEquals(Callback.class, wire.getCallbackInterface());
        assertEquals(1, wire.getTargetCallbackInvocationChains().size());
        InboundInvocationChain callbackChain = wire.getTargetCallbackInvocationChains().get(callbackOperation);
        assertEquals(callbackOperation, callbackChain.getOperation());

        TargetInvoker invoker = EasyMock.createMock(TargetInvoker.class);
        MessageImpl resp = new MessageImpl();
        EasyMock.expect(invoker.invoke(EasyMock.isA(Message.class))).andReturn(resp);
        EasyMock.replay(invoker);
        // verify the callback chain is invokable
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        assertNotNull(callbackChain.getHeadInterceptor().invoke(msg));
        EasyMock.verify(invoker);
    }

    public void testCreateAutowireReferenceWire() throws Exception {
        ReferenceDefinition definition = new ReferenceDefinition("foo", contract);
        definition.setAutowire(true);
        ReferenceTarget target = new ReferenceTarget();
        target.setReferenceName("refName");
        OutboundWire wire = wireService.createWire(target, definition);
        assertTrue(wire.isAutowire());
        assertEquals("refName", wire.getReferenceName());
        assertEquals(contract, wire.getServiceContract());
        assertEquals(Callback.class, wire.getCallbackInterface());
    }

    public void testCreateComponentWires() throws Exception {
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ReferenceDefinition referenceDefinition = new ReferenceDefinition("refName", contract);
        type.add(referenceDefinition);
        ServiceDefinition serviceDefinition = new ServiceDefinition("foo", contract, false);
        type.add(serviceDefinition);

        Implementation<ComponentType> impl = new Implementation<ComponentType>() {
        };
        impl.setComponentType(type);

        ComponentDefinition<Implementation<ComponentType>> definition =
            new ComponentDefinition<Implementation<ComponentType>>("Foo", impl);
        ReferenceTarget target = new ReferenceTarget();
        target.addTarget(new URI("bar"));
        target.setReferenceName("refName");
        definition.add(target);

        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.addInboundWire(EasyMock.isA(InboundWire.class));
        component.addOutboundWire(EasyMock.isA(OutboundWire.class));
        EasyMock.replay(component);

        wireService.createWires(component, definition);
        EasyMock.verify(component);
    }

    public void testCreateReferenceBindingWire() throws Exception {
        ReferenceBinding binding = new MockReferenceBinding();
        QualifiedName qName = new QualifiedName("target");

        wireService.createWires(binding, contract, qName);

        InboundWire inboundWire = binding.getInboundWire();
        assertEquals(1, inboundWire.getInvocationChains().size());
        assertEquals(contract, inboundWire.getServiceContract());
        assertEquals(binding, inboundWire.getContainer());

        OutboundWire outboundWire = binding.getOutboundWire();
        assertEquals("target", outboundWire.getTargetName().toString());
        assertEquals(1, outboundWire.getInvocationChains().size());
        assertEquals(contract, outboundWire.getServiceContract());
        assertEquals(binding, outboundWire.getContainer());
    }

    public void testCreateServiceBindingWire() throws Exception {
        ServiceBinding binding = new MockServiceBinding();

        wireService.createWires(binding, contract, "target");

        InboundWire inboundWire = binding.getInboundWire();
        assertEquals(1, inboundWire.getInvocationChains().size());
        assertEquals(contract, inboundWire.getServiceContract());
        assertEquals(binding, inboundWire.getContainer());

        OutboundWire outboundWire = binding.getOutboundWire();
        assertEquals("target", outboundWire.getTargetName().toString());
        assertEquals(1, outboundWire.getInvocationChains().size());
        assertEquals(contract, outboundWire.getServiceContract());
        assertEquals(binding, outboundWire.getContainer());
        assertEquals(1, outboundWire.getTargetCallbackInvocationChains().size());
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new TestWireService(new WorkContextImpl());

        operation = new Operation<Type>("foo", null, null, null);
        callbackOperation = new Operation<Type>("foo", null, null, null);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("foo", operation);
        Map<String, Operation<Type>> callbackOperations = new HashMap<String, Operation<Type>>();
        callbackOperations.put("foo", callbackOperation);
        contract = new ServiceContract<Type>() {
        };
        contract.setOperations(operations);
        contract.setCallbackClass(Callback.class);
        contract.setCallbackName(Callback.class.getName());
        contract.setCallbackOperations(callbackOperations);
    }

    private interface Callback {

    }

    private class TestWireService extends WireServiceExtension {
        protected TestWireService(WorkContext context) {
            super(context, null);
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire) throws ProxyCreationException {
            return null;
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire, Map<Method, OutboundChainHolder> mapping)
            throws ProxyCreationException {
            return null;
        }

        public Object createCallbackProxy(Class<?> interfaze, InboundWire wire) throws ProxyCreationException {
            return null;
        }

        public WireInvocationHandler createHandler(Class<?> interfaze, Wire wire) {
            return null;
        }


        public OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def) {
            return super.createWire(reference, def);
        }
    }

    private class MockReferenceBinding extends AbstractSCAObject implements ReferenceBinding {
        private ServiceContract<?> bindingServiceContract;
        private InboundWire inboundWire;
        private OutboundWire outboundWire;

        public MockReferenceBinding() {
            super("foo", null);
        }

        public ServiceContract<?> getBindingServiceContract() {
            return bindingServiceContract;
        }

        public void setBindingServiceContract(ServiceContract<?> bindingServiceContract) {
            this.bindingServiceContract = bindingServiceContract;
        }

        public QName getBindingType() {
            return null;
        }

        public void setReference(Reference reference) {

        }

        public InboundWire getInboundWire() {
            return inboundWire;
        }

        public void setInboundWire(InboundWire inboundWire) {
            this.inboundWire = inboundWire;
        }

        public OutboundWire getOutboundWire() {
            return outboundWire;
        }

        public void setOutboundWire(OutboundWire outboundWire) {
            this.outboundWire = outboundWire;
        }

        public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation)
            throws TargetInvokerCreationException {
            return null;
        }

        public Scope getScope() {
            return null;
        }
    }


}
