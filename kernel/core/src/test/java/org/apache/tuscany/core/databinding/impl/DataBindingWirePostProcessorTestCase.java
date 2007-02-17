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

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessorTestCase extends TestCase {
    private DataBindingWirePostProcessor processor;

    protected void setUp() throws Exception {
        super.setUp();
        Mediator mediator = new MediatorImpl();
        this.processor = new DataBindingWirePostProcessor(mediator);
    }

    public void testProcess1() {
        Wire outboundWire = createMock(Wire.class);

        Component component = createMock(Component.class);
        CompositeComponent composite = createMock(CompositeComponent.class);

        Map<Operation<?>, InvocationChain> outboundChains =
            new HashMap<Operation<?>, InvocationChain>();
        DataType<Type> type1 = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        types.add(type1);
        DataType<List<DataType<Type>>> inputType1 = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> outputType1 = new DataType<Type>(String.class, String.class);
        Operation<Type> op1 = new Operation<Type>("test", inputType1, outputType1, null);
        ServiceContract<Type> outboundContract = new JavaServiceContract(null);
        outboundContract.setDataBinding(String.class.getName());
        op1.setServiceContract(outboundContract);
        Map<String, Operation<Type>> outboundOperations = new HashMap<String, Operation<Type>>();
        outboundOperations.put("test", op1);

        outboundContract.setOperations(outboundOperations);
        InvocationChain outboundChain = createMock(InvocationChain.class);
        outboundChains.put(op1, outboundChain);
        expect(outboundWire.getInvocationChains()).andReturn(outboundChains);
        outboundChain.addInterceptor(EasyMock.anyInt(), (Interceptor) EasyMock.anyObject());


        ServiceContract<Type> contract = new JavaServiceContract();
        Map<String, Operation<Type>> operations = Collections.emptyMap();
        contract.setCallbackOperations(operations);
        expect(outboundWire.getSourceContract()).andReturn(outboundContract);
        expect(outboundWire.getTargetContract()).andReturn(outboundContract);

        EasyMock.replay(composite, component, outboundWire, outboundChain);

        processor.process(outboundWire);
    }

}
