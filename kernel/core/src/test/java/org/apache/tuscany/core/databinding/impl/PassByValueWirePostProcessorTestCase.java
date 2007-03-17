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

package org.apache.tuscany.core.databinding.impl;

import java.net.URI;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;

/**
 * Testcase for testing if the PassByValueWireProcessor adds the PassByValueInterceptor to the invocation chains and
 * also ensure that the outbound and inbound chain of interceptors are linked after this insertion
 *
 * @version $Rev$ $Date$
 */
public class PassByValueWirePostProcessorTestCase extends TestCase {
    private PassByValueWirePostProcessor processor;

    public void testProcessInclusionOfInterceptor() {
//
//        InboundWire inboundWire = createMock(InboundWire.class);
//        OutboundWire outboundWire = createMock(OutboundWire.class);
//
//        ServiceContract<Type> serviceContract = new JavaServiceContract(null);
//        serviceContract.setRemotable(true);
//        Map<Operation<?>, InboundInvocationChain> inChainsMap =
//            new Hashtable<Operation<?>, InboundInvocationChain>();
//
//        Operation<Type> operation1 = new Operation<Type>("testMethod", null, null, null);
//        operation1.setServiceContract(serviceContract);
//        InboundInvocationChainImpl inChain = new InboundInvocationChainImpl(operation1);
//        inChainsMap.put(operation1, inChain);
//
//        AtomicComponentExtension componentExtn = new FooComponent();
//
//        Map<Operation<?>, OutboundInvocationChain> outChainsMap =
//            new Hashtable<Operation<?>, OutboundInvocationChain>();
//        OutboundInvocationChainImpl outChain = new OutboundInvocationChainImpl(operation1);
//        outChainsMap.put(operation1, outChain);
//
//        expect(inboundWire.getSourceContract()).andReturn(serviceContract);
//        expect(inboundWire.getInboundInvocationChains()).andReturn(inChainsMap);
//        expect(outboundWire.getSourceContract()).andReturn(serviceContract).times(2);
//        expect(outboundWire.getOutboundInvocationChains()).andReturn(outChainsMap).times(2);
//
//        Interceptor inInterceptor = createMock(Interceptor.class);
//        Interceptor outInterceptor = createMock(Interceptor.class);
//        inChain.addInterceptor(0, inInterceptor);
//        outChain.addInterceptor(0, outInterceptor);
//        //outChain.addInterceptor(new SynchronousBridgingInterceptor(inChain.getHeadInterceptor()));
//
//        EasyMock.replay(inboundWire, outboundWire);
//        processor.process(componentExtn, outboundWire, componentExtn, inboundWire);
//
//        assertEquals(true, inChain.getHeadInterceptor() instanceof PassByValueInterceptor);
//        assertEquals(true,
//            outChain.getTailInterceptor().getNext() instanceof PassByValueInterceptor);
//        assertEquals(true, outChain.getTailInterceptor().getNext().equals(
//            inChain.getHeadInterceptor()));
//
    }

    public void testProcessExclusionOfInterceptorWhenAllowsPassByReference() {
//        Wire inboundWire = createMock(Wire.class);
//        Wire outboundWire = createMock(Wire.class);
//
//        ServiceContract<Type> serviceContract = new JavaServiceContract(null);
//        serviceContract.setRemotable(true);
//        Map<Operation<?>, InvocationChain> inChainsMap =
//            new Hashtable<Operation<?>, InvocationChain>();
//
//        Operation<?> operation1 = new Operation<Type>("testMethod", null, null, null);
//        InvocationChainImpl inChain = new InvocationChainImpl(operation1);
//        inChainsMap.put(operation1, inChain);
//
//        AtomicComponentExtension componentExtn = new FooComponent();
//        componentExtn.setAllowsPassByReference(true);
//
//
//        Map<Operation<?>, InvocationChain> outChainsMap =
//            new Hashtable<Operation<?>, InvocationChain>();
//        InvocationChainImpl outChain = new InvocationChainImpl(operation1);
//        outChainsMap.put(operation1, outChain);
//
//        expect(inboundWire.getSourceContract()).andReturn(serviceContract);
//        expect(inboundWire.getInvocationChains()).andReturn(inChainsMap);
//        expect(outboundWire.getSourceContract()).andReturn(serviceContract).times(2);
//        expect(outboundWire.getInvocationChains()).andReturn(outChainsMap).times(2);
//
//        Interceptor inInterceptor = createMock(Interceptor.class);
//        Interceptor outInterceptor = createMock(Interceptor.class);
//        inChain.addInterceptor(0, inInterceptor);
//        outChain.addInterceptor(0, outInterceptor);
//        //outChain.addInterceptor(new SynchronousBridgingInterceptor(inChain.getHeadInterceptor()));
//
//        EasyMock.replay(inboundWire, outboundWire);
//        processor.process(componentExtn, outboundWire, componentExtn, inboundWire);
//
//        assertEquals(false, inChain.getHeadInterceptor() instanceof PassByValueInterceptor);
//        assertEquals(false,
//            outChain.getTailInterceptor().getNext() instanceof PassByValueInterceptor);
//        assertEquals(true, outChain.getTailInterceptor().getNext().equals(
//            inChain.getHeadInterceptor()));
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.processor = new PassByValueWirePostProcessor();
        DataBindingRegistry dataBindingRegistry = createMock(DataBindingRegistry.class);
        processor.setDataBindingRegistry(dataBindingRegistry);
    }

    private class FooComponent extends AtomicComponentExtension {

        public FooComponent() {
            super(URI.create("foo"), null, null, null, 0, -1, -1);
        }

        public List<Wire> getWires(String name) {
            return null;
        }

        public void attachCallbackWire(Wire wire) {

        }

        public void attachWire(Wire wire) {

        }

        public void attachWires(List<Wire> wires) {

        }

        public Object createInstance() throws ObjectCreationException {
            return null;
        }

        public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
            return null;
        }

        public Object getTargetInstance() throws TargetResolutionException {
            return null;
        }

        public Object getAssociatedTargetInstance() throws TargetResolutionException {
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
            throws TargetInvokerCreationException {
            return null;
        }

    }
}
