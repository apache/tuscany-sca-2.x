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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.easymock.EasyMock;

/**
 * Testcase for testing if the PassByValueWireProcessor adds the PassByValueInterceptor to the invocation chains and also ensure that the outbound and
 * inbound chain of interceptors are linked after this insertion
 */
public class PassByValueWirePostProcessorTestCase extends TestCase {
    private PassByValueWirePostProcessor processor;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.processor = new PassByValueWirePostProcessor();
    }

    public void testProcessInclusionOfInterceptor() {
        InboundWire inboundWire = createMock(InboundWire.class);
        OutboundWire outboundWire = createMock(OutboundWire.class);

        ServiceContract<Type> serviceContract = new JavaServiceContract(null);
        serviceContract.setRemotable(true);
        Map<Operation<?>, InboundInvocationChain> inChainsMap =
                new Hashtable<Operation<?>, InboundInvocationChain>();

        Operation<?> operation1 = new Operation("testMethod", null, null, null);
        InboundInvocationChainImpl inChain = new InboundInvocationChainImpl(operation1);  
        inChainsMap.put(operation1, inChain);
        
        AtomicComponentExtension componentExtn = new FooComponent();

        Map<Operation<?>, OutboundInvocationChain> outChainsMap =
                new Hashtable<Operation<?>, OutboundInvocationChain>();
        OutboundInvocationChainImpl outChain = new OutboundInvocationChainImpl(operation1);
        outChainsMap.put(operation1, outChain);

        expect(inboundWire.getContainer()).andReturn(componentExtn).times(2);
        expect(outboundWire.getContainer()).andReturn(componentExtn).times(2);
        expect(inboundWire.getServiceContract()).andReturn(serviceContract);
        expect(inboundWire.getInvocationChains()).andReturn(inChainsMap);
        expect(outboundWire.getServiceContract()).andReturn(serviceContract).times(2);
        expect(outboundWire.getInvocationChains()).andReturn(outChainsMap).times(2);
        
        Interceptor inInterceptor = createMock(Interceptor.class);
        Interceptor outInterceptor = createMock(Interceptor.class);
        inChain.addInterceptor(0, inInterceptor);
        outChain.addInterceptor(0, outInterceptor);
        outChain.addInterceptor(new SynchronousBridgingInterceptor(inChain.getHeadInterceptor()));

        EasyMock.replay(inboundWire, outboundWire);
        processor.process(outboundWire, inboundWire);

        assertEquals(true, inChain.getHeadInterceptor() instanceof PassByValueInterceptor);
        assertEquals(true,
                outChain.getTailInterceptor().getNext() instanceof PassByValueInterceptor);
        assertEquals(true, outChain.getTailInterceptor().getNext().equals(
                inChain.getHeadInterceptor()));

    }
    
    public void testProcessExclusionOfInterceptorWhenAllowsPassByReference() {
        InboundWire inboundWire = createMock(InboundWire.class);
        OutboundWire outboundWire = createMock(OutboundWire.class);

        ServiceContract<Type> serviceContract = new JavaServiceContract(null);
        serviceContract.setRemotable(true);
        Map<Operation<?>, InboundInvocationChain> inChainsMap =
                new Hashtable<Operation<?>, InboundInvocationChain>();

        Operation<?> operation1 = new Operation("testMethod", null, null, null);
        InboundInvocationChainImpl inChain = new InboundInvocationChainImpl(operation1); 
        inChainsMap.put(operation1, inChain);
        
        AtomicComponentExtension componentExtn = new FooComponent();
        componentExtn.setAllowsPassByReference(true);
        

        Map<Operation<?>, OutboundInvocationChain> outChainsMap =
                new Hashtable<Operation<?>, OutboundInvocationChain>();
        OutboundInvocationChainImpl outChain = new OutboundInvocationChainImpl(operation1);
        outChainsMap.put(operation1, outChain);

        expect(inboundWire.getContainer()).andReturn(componentExtn).times(2);
        expect(outboundWire.getContainer()).andReturn(componentExtn).times(2);
        expect(inboundWire.getServiceContract()).andReturn(serviceContract);
        expect(inboundWire.getInvocationChains()).andReturn(inChainsMap);
        expect(outboundWire.getServiceContract()).andReturn(serviceContract).times(2);
        expect(outboundWire.getInvocationChains()).andReturn(outChainsMap).times(2);
        
        Interceptor inInterceptor = createMock(Interceptor.class);
        Interceptor outInterceptor = createMock(Interceptor.class);
        inChain.addInterceptor(0, inInterceptor);
        outChain.addInterceptor(0, outInterceptor);
        outChain.addInterceptor(new SynchronousBridgingInterceptor(inChain.getHeadInterceptor()));

        EasyMock.replay(inboundWire, outboundWire);
        processor.process(outboundWire, inboundWire);

        assertEquals(false, inChain.getHeadInterceptor() instanceof PassByValueInterceptor);
        assertEquals(false,
                outChain.getTailInterceptor().getNext() instanceof PassByValueInterceptor);
        assertEquals(true, outChain.getTailInterceptor().getNext().equals(
                inChain.getHeadInterceptor()));
    }
    
    private class FooComponent extends AtomicComponentExtension {

        public FooComponent() {
            super(null,null,null, null, null, null, null, 0);
        }
        
        public Object createInstance() throws ObjectCreationException {
            // TODO Auto-generated method stub
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getServiceInstance(String name) throws TargetException {
            // TODO Auto-generated method stub
            return null;
        }

        public List<Class<?>> getServiceInterfaces() {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getServiceInstance() throws TargetException {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
