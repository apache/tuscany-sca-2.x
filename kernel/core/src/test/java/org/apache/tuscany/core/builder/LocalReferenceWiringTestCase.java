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
import java.util.Collections;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Verifies various wiring "scenarios" or paths through the connector
 *
 * @version $Rev$ $Date$
 */
public class LocalReferenceWiringTestCase extends AbstractConnectorImplTestCase {
    protected ReferenceBinding referenceBinding;
    private Reference reference;

    /**
     * Verifies the case where the outbound reference wire is connected to a target atomic component that is a sibling
     * to the reference's parent composite. This wiring scenario occurs when a reference is configured with the local
     * binding.
     */
    public void testConnectLocalReferenceBindingToAtomicComponentService() throws Exception {
        final AtomicComponent atomicComponent = createAtomicTarget();
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET.toString());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return atomicComponent;
            }
        });
        EasyMock.replay(topComposite);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
        parent.register(reference);
        // connect to the target
        connector.connect(parent);
        // connect the internal reference chains
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    /**
     * Verifies the case where the outbound reference wire is connected to a target composite service that is a sibling
     * to the reference's parent composite. This wiring scenario occurs when a reference is configured with the local
     * binding.
     */
    public void testConnectLocalReferenceBindingToCompositeService() throws Exception {
        final CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);

        topComposite.getInboundWire(TARGET.toString());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return createLocalInboundWire(topComposite);
            }
        });
        final Service service = createLocalService(topComposite);
        topComposite.getChild(TARGET.toString());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);
        reference = createLocalReference(parent, TARGET_NAME);
        parent.register(reference);
        connector.connect(parent);
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    /**
     * Verfies an exception if the target composite service (a sibling to the reference's parent) does not have a local
     * binding
     */
    public void testConnectLocalReferenceBindingToCompositeServiceNoMatchingBinding() throws Exception {
        final Service service = createServiceNonLocalBinding();
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(topComposite.getName()).andReturn("foo");
        topComposite.getChild(TARGET.toString());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);
        reference = createLocalReference(parent, TARGET_NAME);
        parent.register(reference);
        try {
            connector.connect(parent);
            fail();
        } catch (NoCompatibleBindingsException e) {
            // expected
        }
    }

    /**
     * Verifies a connection to a service offered by a sibling composite of the reference's parent
     *
     * @throws Exception
     */
    public void testConnectLocalReferenceBindingToSiblingCompositeService() throws Exception {
        final CompositeComponent sibling = EasyMock.createMock(CompositeComponent.class);
        final InboundWire localServiceInboundWire = createLocalInboundWire(sibling);
        EasyMock.expect(sibling.getName()).andReturn("sibling").atLeastOnce();
        sibling.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyList()).atLeastOnce();
        EasyMock.expect(sibling.isSystem()).andReturn(false).atLeastOnce();
        sibling.getInboundWire(TARGET_SERVICE);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return localServiceInboundWire;
            }
        });
        EasyMock.expect(sibling.getScope()).andReturn(Scope.SYSTEM).anyTimes();
        EasyMock.replay(sibling);

        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(topComposite.getName()).andReturn("foo").atLeastOnce();
        topComposite.getChild(TARGET.toString());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return sibling;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
        parent.register(reference);
        parent.register(sibling);
        connector.connect(parent);
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectLocalReferenceBindingToSiblingCompositeServiceNoMatchingBinding() throws Exception {
        try {
            final CompositeComponent sibling = EasyMock.createMock(CompositeComponent.class);
            sibling.getInboundWire(TARGET_SERVICE);
            EasyMock.expectLastCall().andReturn(null);
            EasyMock.expect(sibling.getName()).andReturn("sibling").atLeastOnce();
            EasyMock.expect(sibling.getScope()).andReturn(Scope.SYSTEM).atLeastOnce();
            EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
                public Object answer() throws Throwable {
                    return null;
                }
            });
            EasyMock.replay(sibling);

            CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
            topComposite.getChild(TARGET.toString());
            EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
                public Object answer() throws Throwable {
                    return sibling;
                }
            });
            EasyMock.expect(topComposite.getName()).andReturn("top").atLeastOnce();
            EasyMock.replay(topComposite);

            CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);

            reference = createLocalReference(parent, TARGET_SERVICE_NAME);
            parent.register(reference);
            connector.connect(parent);
            fail();
        } catch (TargetServiceNotFoundException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }


    private Reference createLocalReference(CompositeComponent parent, QualifiedName target) throws Exception {
        referenceBinding = createLocalReferenceBinding(target);
        Reference reference = new ReferenceImpl(URI.create("foo"), parent, contract);
        reference.addReferenceBinding(referenceBinding);
        return reference;
    }


}
