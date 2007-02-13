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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessorTestCase extends TestCase {
    private DataBindingWirePostProcessor processor;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Mediator mediator = new MediatorImpl();
        this.processor = new DataBindingWirePostProcessor(mediator);
    }

    public void testProcess1() {
        InboundWire inboundWire = createMock(InboundWire.class);
        OutboundWire outboundWire = createMock(OutboundWire.class);

        Component component = createMock(Component.class);
        CompositeComponent composite = createMock(CompositeComponent.class);

        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        DataType<Type> type1 = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        types.add(type1);
        DataType<List<DataType<Type>>> inputType1 = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> outputType1 = new DataType<Type>(String.class, String.class);
        Operation<Type> op1 = new Operation<Type>("test", inputType1, outputType1, null);
        ServiceContract<Type> outboundContract = new JavaServiceContract(null);
        outboundContract.setDataBinding(String.class.getName());
        op1.setServiceContract(outboundContract);

        OutboundInvocationChain outboundChain = createMock(OutboundInvocationChain.class);
        outboundChains.put(op1, outboundChain);
        expect(outboundWire.getInvocationChains()).andReturn(outboundChains);
        outboundChain.addInterceptor(EasyMock.anyInt(), (Interceptor) EasyMock.anyObject());

        Map<Operation<?>, InboundInvocationChain> inboundChains =
            new HashMap<Operation<?>, InboundInvocationChain>();
        DataType<Type> type2 = new DataType<Type>(Node.class, Node.class);
        List<DataType<Type>> types2 = new ArrayList<DataType<Type>>();
        types2.add(type2);
        DataType<List<DataType<Type>>> inputType2 =
            new DataType<List<DataType<Type>>>(Object[].class, types2);
        DataType<Type> outputType2 = new DataType<Type>(String.class, String.class);
        Operation<Type> op2 = new Operation<Type>("test", inputType2, outputType2, null);
        ServiceContract<Type> inboundContract = new JavaServiceContract(null);
        inboundContract.setDataBinding(Node.class.getName());
        op2.setServiceContract(inboundContract);

        InboundInvocationChain inboundChain = createMock(InboundInvocationChain.class);
        inboundChains.put(op2, inboundChain);
        expect(inboundWire.getInvocationChains()).andReturn(inboundChains);

        ServiceContract<Type> contract = new JavaServiceContract();
        Map<String, Operation<Type>> operations = Collections.emptyMap();
        contract.setCallbackOperations(operations);
        expect(outboundWire.getServiceContract()).andReturn(contract);

        EasyMock.replay(composite, component, inboundWire, outboundWire, inboundChain, outboundChain);

        processor.process(component, outboundWire, component, inboundWire);
    }

    public void testProcess2() {
        InboundWire inboundWire = createMock(InboundWire.class);
        OutboundWire outboundWire = createMock(OutboundWire.class);

        ReferenceBinding referenceBinding = createMock(ReferenceBinding.class);
        CompositeComponent composite = createMock(CompositeComponent.class);

        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        DataType<Type> type1 = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        types.add(type1);
        DataType<List<DataType<Type>>> inputType1 = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> outputType1 = new DataType<Type>(String.class, String.class);
        Operation<Type> op1 = new Operation<Type>("test", inputType1, outputType1, null);
        ServiceContract<Type> outboundContract = new JavaServiceContract(null);
        outboundContract.setDataBinding(String.class.getName());
        op1.setServiceContract(outboundContract);

        OutboundInvocationChain outboundChain = createMock(OutboundInvocationChain.class);
        outboundChains.put(op1, outboundChain);
        expect(outboundWire.getInvocationChains()).andReturn(outboundChains).anyTimes();
        outboundChain.addInterceptor(EasyMock.anyInt(), (Interceptor) EasyMock.anyObject());

        Map<Operation<?>, InboundInvocationChain> inboundChains =
            new HashMap<Operation<?>, InboundInvocationChain>();
        DataType<Type> type2 = new DataType<Type>(Node.class, Node.class);
        List<DataType<Type>> types2 = new ArrayList<DataType<Type>>();
        types2.add(type2);
        DataType<List<DataType<Type>>> inputType2 =
            new DataType<List<DataType<Type>>>(Object[].class, types2);
        DataType<Type> outputType2 = new DataType<Type>(String.class, String.class);
        Operation<Type> op2 = new Operation<Type>("test", inputType2, outputType2, null);
        ServiceContract<Type> inboundContract = new JavaServiceContract(null);
        inboundContract.setDataBinding(Node.class.getName());
        op2.setServiceContract(inboundContract);

        InboundInvocationChain inboundChain = createMock(InboundInvocationChain.class);
        inboundChains.put(op2, inboundChain);
        expect(inboundWire.getInvocationChains()).andReturn(inboundChains).anyTimes();

        ServiceContract<Type> contract = new JavaServiceContract();
        Map<String, Operation<Type>> operations = Collections.emptyMap();
        contract.setCallbackOperations(operations);
        expect(inboundWire.getServiceContract()).andReturn(contract);
        expect(inboundChain.getTailInterceptor()).andReturn(null);

        EasyMock.replay(composite, referenceBinding, inboundWire, outboundWire, inboundChain, outboundChain);

        processor.process(referenceBinding, inboundWire, referenceBinding, outboundWire);
    }

    public void testProcess3() {
        InboundWire inboundWire = createMock(InboundWire.class);
        OutboundWire outboundWire = createMock(OutboundWire.class);

        ServiceBinding serviceBinding = createMock(ServiceBinding.class);
        CompositeComponent composite = createMock(CompositeComponent.class);

        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        DataType<Type> type1 = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        types.add(type1);
        DataType<List<DataType<Type>>> inputType1 = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> outputType1 = new DataType<Type>(String.class, String.class);
        Operation<Type> op1 = new Operation<Type>("test", inputType1, outputType1, null);
        ServiceContract<Type> outboundContract = new JavaServiceContract(null);
        outboundContract.setDataBinding(String.class.getName());
        op1.setServiceContract(outboundContract);

        OutboundInvocationChain outboundChain = createMock(OutboundInvocationChain.class);
        outboundChains.put(op1, outboundChain);
        expect(outboundWire.getInvocationChains()).andReturn(outboundChains).anyTimes();
        // outboundChain.addInterceptor(EasyMock.anyInt(), (Interceptor)
        // EasyMock.anyObject());

        Map<Operation<?>, InboundInvocationChain> inboundChains =
            new HashMap<Operation<?>, InboundInvocationChain>();
        DataType<Type> type2 = new DataType<Type>(Node.class, Node.class);
        List<DataType<Type>> types2 = new ArrayList<DataType<Type>>();
        types2.add(type2);
        DataType<List<DataType<Type>>> inputType2 =
            new DataType<List<DataType<Type>>>(Object[].class, types2);
        DataType<Type> outputType2 = new DataType<Type>(String.class, String.class);
        Operation<Type> op2 = new Operation<Type>("test", inputType2, outputType2, null);
        ServiceContract<Type> inboundContract = new JavaServiceContract(null);
        inboundContract.setDataBinding(Node.class.getName());
        op2.setServiceContract(inboundContract);

        InboundInvocationChain inboundChain = createMock(InboundInvocationChain.class);
        inboundChains.put(op2, inboundChain);
        expect(inboundWire.getInvocationChains()).andReturn(inboundChains).anyTimes();
        inboundChain.addInterceptor(EasyMock.anyInt(), (Interceptor) EasyMock.anyObject());

        ServiceContract<Type> contract = new JavaServiceContract();
        Map<String, Operation<Type>> operations = Collections.emptyMap();
        contract.setCallbackOperations(operations);
        expect(inboundWire.getServiceContract()).andReturn(contract);

        EasyMock.replay(composite, serviceBinding, inboundWire, outboundWire, inboundChain, outboundChain);

        processor.process(serviceBinding, inboundWire, serviceBinding, outboundWire);
    }

}
