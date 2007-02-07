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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;

/**
 * Verifies wiring local reference to targets
 *
 * @version $Rev$ $Date$
 */
public class LocalReferenceWiringTestCase extends AbstractConnectorImplTestCase {
    private ReferenceBinding referenceBinding;
    private Reference reference;

    /**
     * Verifies the case where the outbound wire with a local binding is connected to a target atomic component.
     */
    public void testConnectLocalReferenceBindingToAtomicComponent() throws Exception {
        AtomicComponent atomicComponent = createAtomicTarget();
        componentManager.register(atomicComponent);
        CompositeComponent topComposite = new CompositeComponentImpl(URI.create("topComposite"), null, connector, null);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);
        reference = createLocalReference(parent, TARGET_NAME);
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
     * Verifies the case where the outbound reference wire is connected to a target composite service
     */
    public void testConnectLocalReferenceBindingToCompositeService() throws Exception {
        CompositeComponent topComposite = new CompositeComponentImpl(URI.create("topComposite"), null, connector, null);
        topComposite.register(createLocalService(topComposite));
        componentManager.register(topComposite);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), topComposite, connector, null);
        reference = createLocalReference(parent, URI.create("topComposite#target"));
        parent.register(reference);
        componentManager.register(parent);
        connector.connect(parent);
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
    }


    private Reference createLocalReference(CompositeComponent parent, URI target) throws Exception {
        URI uri = URI.create("reference");
        referenceBinding = createLocalReferenceBinding(uri, target);
        Reference reference = new ReferenceImpl(uri, parent, contract);
        reference.addReferenceBinding(referenceBinding);
        return reference;
    }


}
